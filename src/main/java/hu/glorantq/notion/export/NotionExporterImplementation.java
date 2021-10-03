package hu.glorantq.notion.export;

import hu.glorantq.notion.api.NotionAPI;
import hu.glorantq.notion.api.StringConstants;
import hu.glorantq.notion.api.model.NotionPage;
import hu.glorantq.notion.api.model.NotionPaginatedResponse;
import hu.glorantq.notion.api.model.NotionParent;
import hu.glorantq.notion.api.model.NotionRichText;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import hu.glorantq.notion.api.model.blocks.impl.*;
import hu.glorantq.notion.render.NotionRenderer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class NotionExporterImplementation {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected final NotionAPI notionAPI;

    protected final String rootPageId;
    protected final boolean followLinks;
    protected final String pageName;
    protected final String pageAuthor;
    protected final File outputFolder;
    protected final boolean rewriteIndex;
    protected final boolean rewriteNames;

    protected final List<NotionPage> pagesToExport;
    protected final Map<String, String> nameMapping;
    protected final Map<String, String> pagePathMapping;

    private final LinkResolver linkResolver;
    private final NotionRenderer notionRenderer;

    public NotionExporterImplementation(String integrationKey, String rootPageId, boolean rewriteIndex, boolean rewriteNames, boolean followLinks, boolean mirrorAssets, String pageName, String pageAuthor, File outputFolder) {
        this.rootPageId = rootPageId;
        this.followLinks = followLinks;
        this.pageName = pageName;
        this.pageAuthor = pageAuthor;
        this.outputFolder = outputFolder;
        this.rewriteIndex = rewriteIndex;
        this.rewriteNames = rewriteNames;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    Request newRequest = original.newBuilder()
                            .addHeader("Notion-Version", StringConstants.NOTION_API_VERSION)
                            .addHeader("Authorization", "Bearer " + integrationKey)
                            .build();

                    return chain.proceed(newRequest);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StringConstants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        this.notionAPI = retrofit.create(NotionAPI.class);

        logger.info("Loading pages...");
        this.pagesToExport = listPagesToExport();

        logger.info("Mapping page names...");
        this.nameMapping = mapPagesToNames();

        logger.info("Creating page-to-path mappings...");
        this.pagePathMapping = getPagePathMapping();

        this.linkResolver = new LinkResolver() {
            @Override
            public String resolveAssetLink(String rawUrl, String ownPageId) {
                String ownPagePath = pagePathMapping.get(cleanPageId(ownPageId));
                if(ownPagePath == null) {
                    logger.error("Invalid own page path!");
                    return rawUrl;
                }

                File currentPage = new File(outputFolder, ownPagePath);

                try {
                    URI uri = new URI(rawUrl);
                    if(uri.getScheme().equalsIgnoreCase("bundled")) {
                        return resolveRelativeToOutput(currentPage, uri.getPath());
                    }
                } catch (Exception e) {
                    logger.error("Failed to resolve asset link!", e);
                }

                if(mirrorAssets) {
                    logger.warn("Mirroring not yet supported!");
                }

                return rawUrl;
            }

            @Override
            public String resolvePageLink(String pageId, String ownPageId) {
                String mapping = pagePathMapping.get(cleanPageId(pageId));
                if(mapping == null) {
                    throw new RuntimeException("No mapping exists for " + pageId + "!");
                }

                String ownPagePath = pagePathMapping.get(cleanPageId(ownPageId));
                if(ownPagePath == null) {
                    logger.error("Invalid own page path!");
                    return mapping;
                }

                File currentPage = new File(outputFolder, ownPagePath);

                String path = resolveRelativeToOutput(currentPage, mapping);
                if(path.endsWith("index.html")) {
                    path = path.substring(0, path.length() - "index.html".length());
                }

                if(path.length() == 0) {
                    path = "#";
                }

                return path;
            }

            private String resolveRelativeToOutput(File currentPagePath, String desiredFile) {
                File file = new File(outputFolder, desiredFile);
                return currentPagePath.getParentFile().toPath().normalize().relativize(file.toPath().normalize()).toString().replaceAll("\\\\", "/");
            }
        };

        this.notionRenderer = new NotionRenderer(notionAPI, linkResolver, pageAuthor, pageName);
        notionRenderer.registerDefaultRenderers();

        logger.info("Ready to export!");

        StringBuilder pagesDataBuilder = new StringBuilder();
        pagesToExport.forEach(it -> pagesDataBuilder.append(it.getTitle()).append(", "));
        logger.info("Pages: {}", pagesDataBuilder);
        logger.info("Path mapping: {}", pagePathMapping);
    }

    public void beginRendering() {
        for(NotionPage notionPage : pagesToExport) {
            String filePath = pagePathMapping.get(cleanPageId(notionPage.getPageId().toString()));
            if(filePath == null) {
                throw new RuntimeException("Missing mapping for page: " + notionPage.getPageId());
            }

            File exportFile = new File(outputFolder, filePath);
            if(!exportFile.exists()) {
                exportFile.getParentFile().mkdirs();
            }

            logger.info("Rendering {}...", notionPage.getTitle());
            String renderedContent = notionRenderer.renderPage(notionPage);

            try {
                Files.write(exportFile.toPath(), renderedContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            } catch (Exception e) {
                logger.error("Failed to write page!", e);
            }

            logger.info("Complete!");
        }
    }

    private Map<String, String> mapPagesToNames() {
        Map<String, String> mapping = new HashMap<>();

        for(NotionPage notionPage : pagesToExport) {
            String pageId = cleanPageId(notionPage.getPageId().toString());
            String mappedName;

            if(rewriteNames) {
                String rawTitle = notionPage.getTitle();

                mappedName = Normalizer.normalize(rawTitle, Normalizer.Form.NFD)
                        .toLowerCase()
                        .replaceAll("[^a-zA-Z0-9_\\-\\s:.]", "")
                        .replaceAll("[\\s\\-_.:]", "-");

                System.out.println(rawTitle);
                System.out.println(mappedName);
            } else {
                mappedName = pageId;
            }

            mapping.put(pageId, mappedName);
        }

        return mapping;
    }

    private final List<String> exploredPages = new ArrayList<>();

    private List<NotionPage> listPagesToExport() {
        List<NotionPage> pageList = new ArrayList<>();

        try {
            NotionPage rootPage = notionAPI.retrievePage(rootPageId).execute().body();
            pageList.add(rootPage);

            if(followLinks) {
                pageList.addAll(getLinkedPages(rootPage));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build page list!", e);
        }

        return pageList;
    }

    private List<NotionPage> getLinkedPages(NotionPage page) {
        List<NotionPage> linkedPages = new ArrayList<>();

        if(page == null || exploredPages.contains(cleanPageId(page.getPageId().toString()))) {
            return linkedPages;
        }

        try {
            List<NotionBlock> pageBlocks = new ArrayList<>();

            String nextCursor = null;
            do {
                NotionPaginatedResponse<NotionBlock> pageResult = notionAPI.retrieveBlockChildren(page.getPageId().toString(), nextCursor, 100).execute().body();
                if(pageResult == null) {
                    break;
                }

                nextCursor = pageResult.getNextCursor();

                pageBlocks.addAll(pageResult.getResults());
            } while (nextCursor != null);

            addAllUnique(linkedPages, processBlockArray(pageBlocks));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get page blocks!", e);
        }

        exploredPages.add(cleanPageId(page.getPageId().toString()));

        return linkedPages;
    }

    private List<NotionPage> processBlockArray(List<NotionBlock> blocks) throws Exception {
        List<NotionPage> linkedPages = new ArrayList<>();

        List<Class<? extends NotionBlock>> linkableTypes = new ArrayList<>();
        linkableTypes.add(NotionParagraphBlock.class);
        linkableTypes.add(NotionHeadingOneBlock.class);
        linkableTypes.add(NotionHeadingTwoBlock.class);
        linkableTypes.add(NotionHeadingThreeBlock.class);
        linkableTypes.add(NotionBulletedListItemBlock.class);
        linkableTypes.add(NotionNumberedListItemBlock.class);

        for(NotionBlock block : blocks) {
            if(block instanceof NotionChildPageBlock) {
                if(exploredPages.contains(cleanPageId(block.getId().toString()))) {
                    continue;
                }

                NotionPage childPage = notionAPI.retrievePage(block.getId().toString()).execute().body();

                if(!linkedPages.contains(childPage)) {
                    linkedPages.add(childPage);
                }

                if(childPage == null) {
                    continue;
                }

                addAllUnique(linkedPages, getLinkedPages(childPage));
                exploredPages.add(cleanPageId(childPage.getPageId().toString()));

                continue;
            }

            if(linkableTypes.contains(block.getClass())) {
                Field textListField = block.getClass().getDeclaredField("text");
                textListField.setAccessible(true);

                Object textList0 = textListField.get(block);
                List<NotionRichText> textList = (List<NotionRichText>) textList0;

                List<NotionBlock> childrenBlocks = null;
                if(block.isHasChildren()) {
                    Field childrenField = block.getClass().getDeclaredField("children");
                    childrenField.setAccessible(true);

                    childrenBlocks = (List<NotionBlock>) childrenField.get(block);
                }

                for(NotionRichText richText : textList) {
                    if(richText.getType() != NotionRichText.Type.MENTION) {
                        continue;
                    }

                    NotionRichText.MentionObject mentionObject = richText.getMention();
                    if(mentionObject.getType() == NotionRichText.MentionObject.Type.PAGE) {
                        String pageId = String.valueOf(mentionObject.getPage().get("id"));

                        if(exploredPages.contains(cleanPageId(pageId)) || cleanPageId(pageId).equalsIgnoreCase(cleanPageId(rootPageId))) {
                            continue;
                        }

                        NotionPage linkedPage = notionAPI.retrievePage(pageId).execute().body();

                        if(linkedPage == null) {
                            continue;
                        }

                        if(!linkedPages.contains(linkedPage)) {
                            linkedPages.add(linkedPage);
                        }

                        addAllUnique(linkedPages, getLinkedPages(linkedPage));
                        exploredPages.add(cleanPageId(pageId));
                    }
                }

                if(childrenBlocks != null) {
                    addAllUnique(linkedPages, processBlockArray(childrenBlocks));
                }
            }
        }

        return linkedPages;
    }

    private <T> void addAllUnique(List<T> listToAddTo, List<T> listToAdd) {
        List<T> filtered = listToAdd.stream().filter(it -> !listToAddTo.contains(it)).collect(Collectors.toList());
        listToAddTo.addAll(filtered);
    }

    private String getPageName(String pageId) {
        String mappedName = nameMapping.get(pageId);

        if(mappedName == null) {
            throw new RuntimeException("No name mapping!");
        }

        return mappedName;
    }

    private Map<String, String> getPagePathMapping() {
        Map<String, String> mapping = new HashMap<>();

        if(rewriteIndex) {
            mapping.put(cleanPageId(rootPageId), "./index.html");
        }

        for(NotionPage page : pagesToExport) {
            String pageId = cleanPageId(page.getPageId().toString());

            if(!hasMappingFor(mapping, pageId)) {
                NotionParent parent = page.getParent();
                if(parent.isWorkspace()) {
                    mapping.put(pageId, "./" + getPageName(pageId) + "/index.html");
                    continue;
                }

                if(parent.getPageId() != null) {
                    List<String> pathElements = new ArrayList<>();
                    pathElements.add("index.html");
                    pathElements.add(getPageName(pageId));

                    String parentId = cleanPageId(parent.getPageId());
                    do {
                        pathElements.add(getPageName(parentId));

                        NotionPage parent0 = findPage(parentId);
                        if(parent0 == null || parent0.getParent().isWorkspace()) {
                            parentId = null;
                        } else {
                            parentId = cleanPageId(parent0.getPageId().toString());
                        }
                    } while (parentId != null);

                    Collections.reverse(pathElements);
                    mapping.put(pageId, "./" + String.join("/", pathElements));
                }
            }
        }

        return mapping;
    }

    private String cleanPageId(String pageId) {
        return pageId.replaceAll("-", "");
    }

    private NotionPage findPage(String pageId) {
        for(NotionPage notionPage : pagesToExport) {
            if(cleanPageId(notionPage.getPageId().toString()).equalsIgnoreCase(pageId)) {
                return notionPage;
            }
        }

        return null;
    }

    private boolean hasMappingFor(Map<String, String> map, String id) {
        return map.containsKey(id) || map.containsKey(cleanPageId(id));
    }
}
