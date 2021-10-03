package hu.glorantq.notion.render;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import hu.glorantq.notion.api.NotionAPI;
import hu.glorantq.notion.api.model.NotionPage;
import hu.glorantq.notion.api.model.NotionPaginatedResponse;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import hu.glorantq.notion.api.model.blocks.impl.*;
import hu.glorantq.notion.export.LinkResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.*;

public class NotionRenderer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NotionAPI notionAPI;
    private final LinkResolver linkResolver;

    private final String pageAuthor;
    private final String pageName;

    private final Configuration freemarkerConfiguration;

    private final Map<Class<? extends NotionBlock>, BlockRenderer> blockRendererMappings = new HashMap<>();

    public NotionRenderer(NotionAPI notionAPI, LinkResolver linkResolver, String pageAuthor, String pageName) {
        this.notionAPI = notionAPI;
        this.linkResolver = linkResolver;
        this.pageAuthor = pageAuthor;
        this.pageName = pageName;

        freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfiguration.setClassLoaderForTemplateLoading(NotionRenderer.class.getClassLoader(), "templates/");
        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        freemarkerConfiguration.setLogTemplateExceptions(false);
        freemarkerConfiguration.setWrapUncheckedExceptions(true);
        freemarkerConfiguration.setFallbackOnNullLoopVariable(false);
        freemarkerConfiguration.setObjectWrapper(new BeansWrapperBuilder(Configuration.VERSION_2_3_31).build());
        freemarkerConfiguration.setRecognizeStandardFileExtensions(true);

        logger.info("Created new NotionRenderer!");
    }

    public void registerDefaultRenderers() {
        addBlockRenderer(NotionParagraphBlock.class, new FreemarkerBlockRenderer<NotionParagraphBlock>("blocks/paragraph.ftlh"));
        addBlockRenderer(NotionHeadingOneBlock.class, new FreemarkerBlockRenderer<NotionHeadingOneBlock>("blocks/headingOne.ftlh"));
        addBlockRenderer(NotionHeadingTwoBlock.class, new FreemarkerBlockRenderer<NotionHeadingTwoBlock>("blocks/headingTwo.ftlh"));
        addBlockRenderer(NotionHeadingThreeBlock.class, new FreemarkerBlockRenderer<NotionHeadingThreeBlock>("blocks/headingThree.ftlh"));
        addBlockRenderer(NotionBulletedListItemBlock.class, new FreemarkerBlockRenderer<NotionBulletedListItemBlock>("blocks/bulletedListItem.ftlh"));
        addBlockRenderer(NotionNumberedListItemBlock.class, new FreemarkerBlockRenderer<NotionNumberedListItemBlock>("blocks/numberedListItem.ftlh"));
        addBlockRenderer(NotionChildPageBlock.class, new FreemarkerBlockRenderer<NotionChildPageBlock>("blocks/childPage.ftlh"));
        addBlockRenderer(NotionImageBlock.class, new FreemarkerBlockRenderer<NotionImageBlock>("blocks/image.ftlh"));
        addBlockRenderer(NotionCodeBlock.class, new FreemarkerBlockRenderer<NotionCodeBlock>("blocks/code.ftlh"));

        // TODO: Add blocks
    }

    public String renderPage(NotionPage notionPage) {
        try {
            Template pageTemplate = freemarkerConfiguration.getTemplate("page.ftlh", Locale.forLanguageTag("hu"), "utf-8");

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("page", notionPage);
            dataModel.put("pageName", pageName);
            dataModel.put("pageAuthor", pageAuthor);

            List<NotionPage> pageHierarchy = new ArrayList<>();
            NotionPage page = notionPage;

            do {
                pageHierarchy.add(page);

                if(page.getParent().getPageId() != null) {
                    page = notionAPI.retrievePage(page.getParent().getPageId()).execute().body();
                } else {
                    page = null;
                }
            } while(page != null);

            Collections.reverse(pageHierarchy);

            dataModel.put("pageHierarchy", pageHierarchy);
            addTemplateMethods(dataModel);

            StringWriter stringWriter = new StringWriter();
            pageTemplate.process(dataModel, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            return renderException(e);
        }
    }

    public void addBlockRenderer(Class<? extends NotionBlock> blockType, BlockRenderer blockRenderer) {
        if(blockRendererMappings.containsKey(blockType)) {
            logger.error("A renderer is already registered for block type: {}!", blockType.getName());
            return;
        }

        blockRendererMappings.put(blockType, blockRenderer);
        logger.error("Registered renderer for {}!", blockType.getName());
    }

    private void addTemplateMethods(Map<String, Object> dataModel) {
        dataModel.put("renderBlock", new RenderBlockMethod());
        dataModel.put("getBlockChildren", new GetBlockChildrenMethod());
        dataModel.put("fetchPage", new FetchPageMethod());
        dataModel.put("resolveAssetLink", new ResolveAssetLinkMethod());
        dataModel.put("resolvePageLink", new ResolvePageLinkMethod());
    }

    private class FreemarkerBlockRenderer<T extends NotionBlock> implements BlockRenderer {
        private final String templatePath;

        private FreemarkerBlockRenderer(String templatePath) {
            this.templatePath = templatePath;
        }

        @Override
        public String renderBlock(NotionBlock notionBlock, NotionPage notionPage) {
            try {
                Template blockTemplate = freemarkerConfiguration.getTemplate(templatePath, Locale.forLanguageTag("hu"), "UTF-8");

                Map<String, Object> dataModel = new HashMap<>();
                dataModel.put("block", notionBlock);
                dataModel.put("page", notionPage);
                addTemplateMethods(dataModel);

                StringWriter stringWriter = new StringWriter();
                blockTemplate.process(dataModel, stringWriter);

                return stringWriter.toString();
            } catch (Exception e) {
                return renderException(e);
            }
        }
    }

    private void renderException0(Throwable e, StringBuilder stringBuilder) {
        stringBuilder.append("<pre>");
        for(StackTraceElement traceElement : e.getStackTrace()) {
            stringBuilder.append(traceElement.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
            stringBuilder.append("\n");
        }
        stringBuilder.append("</pre>");
    }

    private String renderException(Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>");
        stringBuilder.append("<head>");
        stringBuilder.append("<meta charset=\"utf-8\" />");
        stringBuilder.append("<title>").append(pageName).append(" - Error").append("</title>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body>");
        stringBuilder.append("<h2 style=\"white-space: pre;\">").append(e.getMessage()).append("</h2>");
        stringBuilder.append("<hr />");
        renderException0(e, stringBuilder);

        Throwable cause = e.getCause();
        while(cause != null) {
            stringBuilder.append("<h3>Caused by: ").append(cause.getMessage()).append("</h3>");
            renderException0(cause, stringBuilder);

            cause = cause.getCause();
        }

        stringBuilder.append("<body>");
        stringBuilder.append("</html>");

        return stringBuilder.toString();
    }

    private class ResolvePageLinkMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 2) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String pageId = scalarModel.getAsString();

            TemplateModel pageArgumentModel = (TemplateModel) arguments.get(1);
            Object unwrapped = ((BeansWrapper) freemarkerConfiguration.getObjectWrapper()).unwrap(pageArgumentModel);

            if(!(unwrapped instanceof NotionPage)) {
                throw new TemplateModelException("Invalid type!");
            }

            String ownPageId = ((NotionPage) unwrapped).getPageId().toString();

            return linkResolver.resolvePageLink(pageId, ownPageId);
        }
    }

    private class ResolveAssetLinkMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 2) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String rawUrl = scalarModel.getAsString();

            TemplateModel pageArgumentModel = (TemplateModel) arguments.get(1);
            Object unwrapped = ((BeansWrapper) freemarkerConfiguration.getObjectWrapper()).unwrap(pageArgumentModel);

            if(!(unwrapped instanceof NotionPage)) {
                throw new TemplateModelException("Invalid type!");
            }

            String pageId = ((NotionPage) unwrapped).getPageId().toString();

            return linkResolver.resolveAssetLink(rawUrl, pageId);
        }
    }

    private class FetchPageMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String pageId = scalarModel.getAsString();

            try {
                return notionAPI.retrievePage(pageId).execute().body();
            } catch (Exception e) {
                throw new TemplateModelException("Failed to fetch page!", e);
            }
        }
    }

    private class GetBlockChildrenMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel blockArgumentModel = (TemplateModel) arguments.get(0);
            Object unwrapped = ((BeansWrapper) freemarkerConfiguration.getObjectWrapper()).unwrap(blockArgumentModel);

            if(!(unwrapped instanceof NotionPage || unwrapped instanceof NotionBlock)) {
                throw new TemplateModelException("Invalid type!");
            }

            String fetchId;
            if(unwrapped instanceof NotionBlock) {
                fetchId = ((NotionBlock) unwrapped).getId().toString();
            } else {
                fetchId = ((NotionPage) unwrapped).getPageId().toString();
            }

            List<NotionBlock> childrenBlocks = new ArrayList<>();

            String nextCursor = null;
            do {
                try {
                    NotionPaginatedResponse<NotionBlock> response = notionAPI.retrieveBlockChildren(fetchId, nextCursor, 100).execute().body();

                    if(response != null) {
                        childrenBlocks.addAll(response.getResults());
                        nextCursor = response.getNextCursor();
                    } else {
                        nextCursor = null;
                    }
                } catch (Exception e) {
                    logger.error("Failed to fetch blocks!", e);
                    break;
                }
            } while(nextCursor != null);

            return childrenBlocks;
        }
    }

    private class RenderBlockMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 2) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel blockArgumentModel = (TemplateModel) arguments.get(0);
            Object unwrapped = ((BeansWrapper) freemarkerConfiguration.getObjectWrapper()).unwrap(blockArgumentModel);

            if(!(unwrapped instanceof NotionBlock)) {
                throw new TemplateModelException("Invalid type!");
            }

            TemplateModel pageArgumentModel = (TemplateModel) arguments.get(1);
            Object unwrapped0 = ((BeansWrapper) freemarkerConfiguration.getObjectWrapper()).unwrap(pageArgumentModel);

            if(!(unwrapped0 instanceof NotionPage)) {
                throw new TemplateModelException("Invalid type!");
            }

            Class<? extends NotionBlock> typeClass = (Class<? extends NotionBlock>) unwrapped.getClass();
            BlockRenderer blockRenderer = blockRendererMappings.get(typeClass);

            if(blockRenderer != null) {
                return blockRenderer.renderBlock((NotionBlock) unwrapped, (NotionPage) unwrapped0);
            } else {
                return "<blockquote class=\"renderer-error\">\u274C No renderer for: " + typeClass.getSimpleName() + "!</blockquote>";
            }
        }
    }

    public interface BlockRenderer {
        String renderBlock(NotionBlock notionBlock, NotionPage page);
    }
}
