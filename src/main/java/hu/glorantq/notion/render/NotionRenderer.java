/*
 *      Copyright (C) 2021 Gerber Lóránt Viktor
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.glorantq.notion.render;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import freemarker.template.utility.DeepUnwrap;
import hu.glorantq.notion.api.NotionAPI;
import hu.glorantq.notion.api.QueryDatabaseBody;
import hu.glorantq.notion.api.model.NotionPage;
import hu.glorantq.notion.api.model.NotionPaginatedResponse;
import hu.glorantq.notion.api.model.NotionPropertyValueObject;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import hu.glorantq.notion.api.model.blocks.impl.*;
import hu.glorantq.notion.export.DatabaseResolver;
import hu.glorantq.notion.export.LinkResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class NotionRenderer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NotionAPI notionAPI;
    private final LinkResolver linkResolver;
    private final DatabaseResolver databaseResolver;

    private final String pageAuthor;
    private final String pageName;

    private final Configuration freemarkerConfiguration;

    private final Map<Class<? extends NotionBlock>, BlockRenderer> blockRendererMappings = new HashMap<>();

    public NotionRenderer(NotionAPI notionAPI, LinkResolver linkResolver, DatabaseResolver databaseResolver, String pageAuthor, String pageName) {
        this.notionAPI = notionAPI;
        this.linkResolver = linkResolver;
        this.databaseResolver = databaseResolver;
        this.pageAuthor = pageAuthor;
        this.pageName = pageName;

        freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        freemarkerConfiguration.setClassLoaderForTemplateLoading(NotionRenderer.class.getClassLoader(), "templates/");
        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        freemarkerConfiguration.setURLEscapingCharset("UTF-8");
        freemarkerConfiguration.setOutputEncoding("UTF-8");
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
        addBlockRenderer(NotionCalloutBlock.class, new FreemarkerBlockRenderer<NotionCalloutBlock>("blocks/callout.ftlh"));
        addBlockRenderer(NotionFileBlock.class, new FreemarkerBlockRenderer<NotionFileBlock>("blocks/file.ftlh"));
        addBlockRenderer(NotionEquationBlock.class, new FreemarkerBlockRenderer<NotionEquationBlock>("blocks/equation.ftlh"));
        addBlockRenderer(NotionDividerBlock.class, new FreemarkerBlockRenderer<NotionDividerBlock>("blocks/divider.ftlh"));
        addBlockRenderer(NotionBreadcrumbBlock.class, new FreemarkerBlockRenderer<NotionBreadcrumbBlock>("blocks/breadcrumb.ftlh"));
        addBlockRenderer(NotionQuoteBlock.class, new FreemarkerBlockRenderer<NotionQuoteBlock>("blocks/quote.ftlh"));
        addBlockRenderer(NotionChildDatabaseBlock.class, new FreemarkerBlockRenderer<NotionChildDatabaseBlock>("blocks/childDatabase.ftlh"));
        addBlockRenderer(NotionToDoBlock.class, new FreemarkerBlockRenderer<NotionToDoBlock>("blocks/todo.ftlh"));
        addBlockRenderer(NotionToggleBlock.class, new FreemarkerBlockRenderer<NotionToggleBlock>("blocks/toggle.ftlh"));
        addBlockRenderer(NotionEmbedBlock.class, new FreemarkerBlockRenderer<NotionEmbedBlock>("blocks/embed.ftlh"));
        addBlockRenderer(NotionPDFBlock.class, new FreemarkerBlockRenderer<NotionPDFBlock>("blocks/pdf.ftlh"));
        addBlockRenderer(NotionColumnListBlock.class, new FreemarkerBlockRenderer<NotionColumnListBlock>("blocks/columnList.ftlh"));
        addBlockRenderer(NotionColumnBlock.class, new FreemarkerBlockRenderer<NotionColumnBlock>("blocks/column.ftlh"));

        addBlockRenderer(NotionUnsupportedBlock.class, (notionBlock, page) -> "");

        // TODO: Add blocks
    }

    public boolean hasRenderer(Class<? extends NotionBlock> block) {
        return blockRendererMappings.get(block) != null;
    }

    public String renderPage(NotionPage notionPage) {
        try {
            Template pageTemplate = freemarkerConfiguration.getTemplate("page.ftlh", Locale.forLanguageTag("hu"), "utf-8");

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("page", notionPage);
            dataModel.put("pageName", pageName);
            dataModel.put("pageAuthor", pageAuthor);
            dataModel.put("pageHierarchy", getPageHierarchy(notionPage));
            addTemplateMethods(dataModel);

            StringWriter stringWriter = new StringWriter();
            pageTemplate.process(dataModel, stringWriter);

            return stringWriter.toString();
        } catch (Exception e) {
            return renderException(e, null);
        }
    }

    private List<NotionPage> getPageHierarchy(NotionPage page) throws IOException {
        List<NotionPage> pageHierarchy = new ArrayList<>();

        do {
            pageHierarchy.add(page);

            if(page.getParent().getPageId() != null || page.getParent().getDatabaseId() != null) {
                String parentId;
                if(page.getParent().getPageId() != null) {
                    parentId = page.getParent().getPageId();
                    page = notionAPI.retrievePage(parentId).execute().body();
                } else {
                    parentId = page.getParent().getDatabaseId();
                    page = notionAPI.retrieveDatabase(parentId).execute().body();
                }
            } else {
                page = null;
            }
        } while(page != null);

        Collections.reverse(pageHierarchy);

        return pageHierarchy;
    }

    public void addBlockRenderer(Class<? extends NotionBlock> blockType, BlockRenderer blockRenderer) {
        if(blockRendererMappings.containsKey(blockType)) {
            logger.error("A renderer is already registered for block type: {}!", blockType.getName());
            return;
        }

        blockRendererMappings.put(blockType, blockRenderer);
        logger.info("Registered renderer for {}!", blockType.getName());
    }

    private void addTemplateMethods(Map<String, Object> dataModel) {
        dataModel.put("renderBlock", new RenderBlockMethod());
        dataModel.put("getBlockChildren", new GetBlockChildrenMethod());
        dataModel.put("fetchPage", new FetchPageMethod());
        dataModel.put("resolveAssetLink", new ResolveAssetLinkMethod());
        dataModel.put("resolvePageLink", new ResolvePageLinkMethod());
        dataModel.put("fetchDatabase", new FetchDatabaseMethod());
        dataModel.put("queryDatabase", new QueryDatabaseMethod());
        dataModel.put("getColumnsToRender", new GetColumnsToRenderMethod());
        dataModel.put("getDatabaseDisplay", new GetDatabaseDisplayMethod());
        dataModel.put("getGroupBy", new GetGroupByMethod());
        dataModel.put("performKanbanGrouping", new PerformKanbanGroupingMethod());
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
                dataModel.put("pageHierarchy", getPageHierarchy(notionPage));
                addTemplateMethods(dataModel);

                StringWriter stringWriter = new StringWriter();
                blockTemplate.process(dataModel, stringWriter);

                return stringWriter.toString();
            } catch (Exception e) {
                return renderException(e, notionBlock);
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

    private String renderException(Exception e, NotionBlock block) {
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

        stringBuilder.append("<h3>").append(String.valueOf(block)).append("</h3>");
        stringBuilder.append("</body>");
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
                Response<NotionPage> page = notionAPI.retrievePage(pageId).execute();
                if(page.isSuccessful()) {
                    return page.body();
                } else {
                    throw new TemplateModelException(page.errorBody().string());
                }
            } catch (Exception e) {
                throw new TemplateModelException("Failed to fetch page!", e);
            }
        }
    }

    private class FetchDatabaseMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String databaseId = scalarModel.getAsString();

            try {
                Response<NotionPage> page = notionAPI.retrieveDatabase(databaseId).execute();
                if(page.isSuccessful()) {
                    return page.body();
                } else {
                    throw new TemplateModelException(page.errorBody().string());
                }
            } catch (Exception e) {
                throw new TemplateModelException("Failed to fetch database!", e);
            }
        }
    }

    private class GetDatabaseDisplayMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String databaseId = scalarModel.getAsString();

            return databaseResolver.getDisplay(databaseId).name();
        }
    }

    private class GetGroupByMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String databaseId = scalarModel.getAsString();

            return databaseResolver.getGroupBy(databaseId);
        }
    }

    private class GetColumnsToRenderMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String databaseId = scalarModel.getAsString();

            String[] columns = databaseResolver.getColumnsToRender(databaseId);
            if(columns.length != 0) {
                return columns;
            }

            try {
                Response<NotionPage> page = notionAPI.retrieveDatabase(databaseId).execute();
                if(page.isSuccessful()) {
                    NotionPage database = page.body();

                    List<String> columnsFetched = new ArrayList<>();
                    for(Map.Entry<String, NotionPropertyValueObject> entry : database.getProperties().entrySet()) {
                        columnsFetched.add(entry.getKey());
                    }

                    Collections.reverse(columnsFetched);

                    return columnsFetched.toArray(new String[0]);
                } else {
                    throw new TemplateModelException(page.errorBody().string());
                }
            } catch (Exception e) {
                throw new TemplateModelException("Failed to fetch database!", e);
            }
        }
    }

    private class QueryDatabaseMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel pageIdModel = (TemplateModel) arguments.get(0);
            TemplateScalarModel scalarModel = (TemplateScalarModel) pageIdModel;
            String databaseId = scalarModel.getAsString();

            QueryDatabaseBody.NotionSortObject[] sorts = databaseResolver.getSorts(databaseId);
            Map<String, Object> filter = databaseResolver.getFilter(databaseId);

            try {
                List<NotionPage> databaseRecords = new ArrayList<>();
                String nextCursor = null;
                do {
                    NotionPaginatedResponse<NotionPage> response = notionAPI.queryDatabase(databaseId, new QueryDatabaseBody(nextCursor, 100, filter, sorts)).execute().body();

                    if(response != null) {
                        databaseRecords.addAll(response.getResults());
                        nextCursor = response.getNextCursor();
                    } else {
                        nextCursor = null;
                    }
                } while (nextCursor != null);

                return databaseRecords;
            } catch (Exception e) {
                throw new TemplateModelException("Failed to query database!", e);
            }
        }
    }

    private class PerformKanbanGroupingMethod implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 2) {
                throw new TemplateModelException("Invalid parameters!");
            }

            TemplateModel recordsTemplateModel = (TemplateModel) arguments.get(0);
            Object unwrapped = ((BeansWrapper) freemarkerConfiguration.getObjectWrapper()).unwrap(recordsTemplateModel);

            TemplateScalarModel scalarModel = (TemplateScalarModel) arguments.get(1);
            String groupBy = scalarModel.getAsString();

            if(!(unwrapped instanceof List)) {
                throw new TemplateModelException("Invalid type " + unwrapped.getClass().getCanonicalName() + "!");
            }

            List<NotionPage> databaseRecords = (List<NotionPage>) unwrapped;

            LinkedHashMap<NotionPropertyValueObject, List<NotionPage>> grouped = new LinkedHashMap<>();
            for(NotionPage record : databaseRecords) {
                NotionPropertyValueObject groupByProperty = record.getProperties().get(groupBy);

                if(!grouped.containsKey(groupByProperty)) {
                    grouped.put(groupByProperty, new ArrayList<>());
                }

                grouped.get(groupByProperty).add(record);
            }

            return grouped;
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
