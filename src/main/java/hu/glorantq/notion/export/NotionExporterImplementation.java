package hu.glorantq.notion.export;

import com.fewlaps.slimjpg.SlimJpg;
import com.googlecode.pngtastic.PngtasticOptimizer;
import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import hu.glorantq.notion.api.NotionAPI;
import hu.glorantq.notion.api.QueryDatabaseBody;
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
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class NotionExporterImplementation {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NotionAPI notionAPI;

    private final String rootPageId;
    private final boolean followLinks;
    private final File outputFolder;
    private final boolean rewriteIndex;
    private final boolean rewriteNames;

    private final List<NotionPage> pagesToExport;
    private final Map<String, String> nameMapping;
    private final Map<String, String> pagePathMapping;

    private final NotionRenderer notionRenderer;

    private final Map<String, String> mirroredFiles = new HashMap<>();
    private final PngOptimizer pngOptimizer = new PngOptimizer();

    public NotionExporterImplementation(String integrationKey, String rootPageId, boolean rewriteIndex, boolean rewriteNames, boolean followLinks, boolean mirrorAssets, String pageName, String pageAuthor, File outputFolder) {
        this.rootPageId = rootPageId;
        this.followLinks = followLinks;
        this.outputFolder = outputFolder;
        this.rewriteIndex = rewriteIndex;
        this.rewriteNames = rewriteNames;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    if(!chain.request().url().toString().contains(StringConstants.API_BASE_URL)) {
                        return chain.proceed(chain.request());
                    }

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

        try {
            NotionPage testDatabase = notionAPI.retrieveDatabase("d7a88c70129d475dbff6a1ef5ee4334d").execute().body();
            System.out.println(testDatabase);

            NotionPaginatedResponse<NotionPage> paginatedResponse = notionAPI.queryDatabase("d7a88c70129d475dbff6a1ef5ee4334d", new QueryDatabaseBody()).execute().body();
            System.out.println(paginatedResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Loading pages...");
        this.pagesToExport = listPagesToExport();

        logger.info("Mapping page names...");
        this.nameMapping = mapPagesToNames();

        logger.info("Creating page-to-path mappings...");
        this.pagePathMapping = getPagePathMapping();

        LinkResolver linkResolver = new LinkResolver() {
            @Override
            public String resolveAssetLink(String rawUrl, String ownPageId) {
                String ownPagePath = pagePathMapping.get(cleanPageId(ownPageId));
                if (ownPagePath == null) {
                    logger.error("Invalid own page path!");
                    return rawUrl;
                }

                File currentPage = new File(outputFolder, ownPagePath);

                try {
                    URI uri = new URI(rawUrl);
                    if (uri.getScheme().equalsIgnoreCase("bundled")) {
                        return resolveRelativeToOutput(currentPage, uri.getPath());
                    }
                } catch (Exception e) {
                    logger.error("Failed to resolve asset link!", e);
                }

                if (mirrorAssets) {
                    try {
                        URL url = new URL(rawUrl);

                        String fileExtension = FilenameUtils.getExtension(url.getPath());
                        String fileName = FilenameUtils.getName(url.getPath());
                        String baseName = FilenameUtils.getBaseName(url.getPath());

                        Request request = new Request.Builder()
                                .url(url)
                                .get()
                                .build();

                        Response response = okHttpClient.newCall(request).execute();
                        ResponseBody responseBody = response.body();
                        byte[] fileBytes = responseBody.bytes();
                        responseBody.close();

                        String fileHash = DigestUtils.sha1Hex(fileBytes).toLowerCase();
                        if (mirroredFiles.containsKey(fileHash)) {
                            logger.info("Got cache hit for {}!", fileName);

                            return resolveRelativeToOutput(currentPage, mirroredFiles.get(fileHash));
                        }

                        File mirrorDirectory = new File(outputFolder, "mirror");
                        if (!mirrorDirectory.exists()) {
                            mirrorDirectory.mkdirs();
                        }

                        File outputFile = new File(mirrorDirectory, fileHash + "-" + baseName + "." + fileExtension);
                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                        if (fileExtension.equalsIgnoreCase("png")) {
                            logger.info("PNG-optimizing {}...", fileName);

                            PngImage pngImage = new PngImage(new ByteArrayInputStream(fileBytes));
                            pngOptimizer.optimize(pngImage);

                            pngImage.writeDataOutputStream(fileOutputStream);
                        } else if (fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("bmp")) {
                            logger.info("JPEG-optimizing {}...", fileName);

                            byte[] optimizedData = SlimJpg.file(fileBytes)
                                    .maxVisualDiff(0.5d)
                                    .deleteMetadata()
                                    .optimize()
                                    .getPicture();

                            fileOutputStream.write(optimizedData);
                        } else {
                            logger.info("Mirroring {}...", fileName);

                            fileOutputStream.write(fileBytes);
                        }

                        fileOutputStream.close();

                        String relativePath = mirrorDirectory.getName() + "/" + outputFile.getName();
                        mirroredFiles.put(fileHash, relativePath);

                        return resolveRelativeToOutput(currentPage, relativePath);
                    } catch (Exception e) {
                        logger.error("Failed to mirror URL!", e);
                    }
                }

                return rawUrl;
            }

            @Override
            public String resolvePageLink(String pageId, String ownPageId) {
                String mapping = pagePathMapping.get(cleanPageId(pageId));
                if (mapping == null) {
                    logger.error("No mapping exists for " + pageId + "!");
                    return "#";
                }

                String ownPagePath = pagePathMapping.get(cleanPageId(ownPageId));
                if (ownPagePath == null) {
                    logger.error("Invalid own page path!");
                    return mapping;
                }

                File currentPage = new File(outputFolder, ownPagePath);

                String path = resolveRelativeToOutput(currentPage, mapping);
                if (path.endsWith("index.html")) {
                    path = path.substring(0, path.length() - "index.html".length());
                }

                if (path.length() == 0) {
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

            if(block instanceof NotionChildDatabaseBlock) {
                if(exploredPages.contains(cleanPageId(block.getId().toString()))) {
                    continue;
                }

                NotionPage childDatabase = notionAPI.retrieveDatabase(block.getId().toString()).execute().body();

                if(!linkedPages.contains(childDatabase)) {
                    linkedPages.add(childDatabase);
                }

                if(childDatabase == null) {
                    continue;
                }

                List<NotionPage> databaseRecords = new ArrayList<>();
                String nextCursor = null;
                do {
                    NotionPaginatedResponse<NotionPage> response = notionAPI.queryDatabase(childDatabase.getPageId().toString(), new QueryDatabaseBody(nextCursor, 100, null, null)).execute().body();

                    if(response != null) {
                        databaseRecords.addAll(response.getResults());
                        nextCursor = response.getNextCursor();
                    } else {
                        nextCursor = null;
                    }
                } while (nextCursor != null);

                addAllUnique(linkedPages, databaseRecords);

                exploredPages.add(cleanPageId(childDatabase.getPageId().toString()));
                exploredPages.addAll(databaseRecords.stream().map(it -> cleanPageId(it.getPageId().toString())).collect(Collectors.toList()));

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

    private String getParentId(NotionParent parent) {
        if(parent.getParentType().equalsIgnoreCase("page_id")) {
            return cleanPageId(parent.getPageId());
        } else if(parent.getParentType().equalsIgnoreCase("database_id")) {
            return cleanPageId(parent.getDatabaseId());
        }

        return null;
    }

    private Map<String, String> getPagePathMapping() {
        Map<String, String> mapping = new HashMap<>();

        if(rewriteIndex) {
            mapping.put(cleanPageId(rootPageId), "./index.html");
        }

        for(NotionPage page : pagesToExport) {
            System.out.println(page); // TODO: Remove
            String pageId = cleanPageId(page.getPageId().toString());

            if(!hasMappingFor(mapping, pageId)) {
                NotionParent parent = page.getParent();
                if(parent.isWorkspace()) {
                    mapping.put(pageId, "./" + getPageName(pageId) + "/index.html");
                    continue;
                }

                String parentId = getParentId(parent);
                if(parentId != null) {
                    List<String> pathElements = new ArrayList<>();
                    pathElements.add("index.html");
                    pathElements.add(getPageName(pageId));

                    String parentId0 = parentId;
                    do {
                        pathElements.add(getPageName(parentId0));

                        NotionPage parent0 = findPage(parentId0);
                        if(parent0 == null || parent0.getParent().isWorkspace()) {
                            parentId0 = null;
                        } else {
                            parentId0 = getParentId(parent0.getParent());
                        }
                    } while (parentId0 != null);

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
