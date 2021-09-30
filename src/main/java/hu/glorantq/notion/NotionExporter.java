package hu.glorantq.notion;

import hu.glorantq.notion.api.NotionAPI;
import hu.glorantq.notion.api.StringConstants;
import hu.glorantq.notion.api.model.NotionPage;
import hu.glorantq.notion.api.model.NotionPaginatedResponse;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import hu.glorantq.notion.api.model.blocks.NotionNotImplementedBlock;
import hu.glorantq.notion.render.NotionRenderer;
import me.grison.jtoml.impl.Toml;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class NotionExporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotionExporter.class);

    public static void main(String[] args) {
        ArgumentParser argumentParser = ArgumentParsers.newFor("notion-exporter").build()
                .defaultHelp(true)
                .description("Export notion workspace as static HTML/CSS/JS page");

        argumentParser.addArgument("-c", "--config")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Configuration file to use");

        argumentParser.addArgument("--pageName", "-pn")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Generated site's name");

        argumentParser.addArgument("--pageAuthor", "-pa")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Generated site's author");

        argumentParser.addArgument("--favicon", "-fi")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Favicon's path or URL");

        argumentParser.addArgument("--pageId", "-id")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Root Notion page's ID");

        argumentParser.addArgument("--key", "-k")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Notion integration's authentication key");

        argumentParser.addArgument("--followLinks", "-fl")
                .nargs("?")
                .type(Boolean.class)
                .action(Arguments.store())
                .help("Follow linked pages");

        argumentParser.addArgument("--output", "-o")
                .nargs("?")
                .type(String.class)
                .action(Arguments.store())
                .help("Output folder path");

        argumentParser.addArgument("--rewriteIndex", "-ri")
                .nargs("?")
                .type(Boolean.class)
                .action(Arguments.store())
                .help("Whether to rewrite the root page as index.html or not");

        Namespace namespace;
        try {
            namespace = argumentParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argumentParser.handleError(e);
            System.exit(1);
            return;
        }

        if(namespace.getString("config") != null) {
            handleStartupFile(namespace.getString("config"));
        } else {
            handleStartupConsole(namespace);
        }
    }

    /*
        pageName: String
        pageAuthor: String
        faviconPath: String
        -------------------
        rootNotionPageId: String
        notionIntegrationKey: String
        followLinkedPages: Bool
     */

    private static void handleStartupFile(String filePath) {
        File configFile = new File(filePath);

        if(!configFile.exists() || !configFile.canRead() || configFile.isDirectory()) {
            LOGGER.error("Invalid configuration file: {}", configFile.getAbsolutePath());
            return;
        }

        Toml parsedConfig;

        try {
            parsedConfig = Toml.parse(configFile);
        } catch (Exception e) {
            LOGGER.error("Failed to parse configuration file!", e);
            return;
        }

        LOGGER.info("Using configuration file: {}", configFile.getAbsolutePath());

        try {
            Map<String, Object> siteProperties = parsedConfig.getMap("site");

            String pageName = getValueFromMap(siteProperties, "name");
            String pageAuthor = getValueFromMap(siteProperties, "author");
            String faviconPath = getValueFromMap(siteProperties, "favicon");

            Map<String, Object> notionProperties = parsedConfig.getMap("notion");

            String rootNotionPageId = getValueFromMap(notionProperties, "rootPageId");
            boolean followLinkedPages = Boolean.parseBoolean(getValueFromMap(notionProperties, "followLinks"));
            String notionIntegrationKey = getValueFromMap(notionProperties, "integrationKey");

            Map<String, Object> exportProperties = parsedConfig.getMap("export");
            String outFolder = getValueFromMap(exportProperties, "folder");
            boolean rewriteIndex = Boolean.parseBoolean(getValueFromMap(exportProperties, "rewriteIndex"));

            handleStartup(pageName, pageAuthor, faviconPath, rootNotionPageId, notionIntegrationKey, followLinkedPages, outFolder, rewriteIndex);
        } catch (Exception e) {
            LOGGER.error("Failed to process configuration!", e);
        }
    }

    private static String getValueFromMap(Map<String, Object> map, String property) {
        if(map == null) {
            LOGGER.error("Missing sections from configuration file!");
            throw new RuntimeException("Missing sections");
        }

        Object value = map.get(property);

        if(value == null) {
            LOGGER.error("Failed to fetch key \"{}\" from configuration file!", property);
            throw new RuntimeException("Missing value");
        }

        return String.valueOf(value);
    }

    private static void handleStartupConsole(Namespace namespace) {
        try {
            String pageName = getValueFromNamespace(namespace, "pageName");
            String pageAuthor = getValueFromNamespace(namespace, "pageAuthor");
            String favicon = getValueFromNamespace(namespace, "favicon");

            String rootPageId = getValueFromNamespace(namespace, "pageId");
            String notionKey = getValueFromNamespace(namespace, "key");
            boolean followLinks = getValueFromNamespace(namespace, "followLinks");

            String outFolder = getValueFromNamespace(namespace, "output");
            boolean rewriteIndex = getValueFromNamespace(namespace, "rewriteIndex");

            handleStartup(pageName, pageAuthor, favicon, rootPageId, notionKey, followLinks, outFolder, rewriteIndex);
        } catch (Exception e) {
            LOGGER.error("Failed to process arguments!", e);
        }
    }

    private static <T> T getValueFromNamespace(Namespace namespace, String key) {
        T value = namespace.get(key);

        if(value == null) {
            LOGGER.error("Missing argument: {}", key);
            throw new RuntimeException();
        }

        return value;
    }

    private static void handleStartup(String pageName, String pageAuthor, String faviconPath, String rootNotionPageId, String notionIntegrationKey, boolean followLinkedPages,
                                      String outFolder, boolean rewriteIndex) {
        LOGGER.info("{} {} {} {} {} {} {} {}", pageName, pageAuthor, faviconPath, rootNotionPageId, notionIntegrationKey, followLinkedPages, outFolder, rewriteIndex);

        File outputFolder = new File(outFolder);
        if(outputFolder.exists()) {
            if(!outputFolder.isDirectory()) {
                LOGGER.error("Output folder is not a directory!");
                return;
            }

            File[] containedFiles = outputFolder.listFiles();
            if(containedFiles != null && containedFiles.length != 0) {
                LOGGER.error("Output directory is not empty!");
                return;
            }
        } else if(!outputFolder.mkdirs()) {
            LOGGER.error("Failed to create output directory!");
            return;
        }

        NotionBlock.Type[] blockTypes = NotionBlock.Type.values();
        for(NotionBlock.Type blockType : blockTypes) {
            if(blockType.getTypeClass() == NotionNotImplementedBlock.class) {
                LOGGER.warn("Found unimplemented block supported by the API: {}!", blockType.name().toLowerCase());
            }
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    Request newRequest = original.newBuilder()
                            .addHeader("Notion-Version", StringConstants.NOTION_API_VERSION)
                            .addHeader("Authorization", "Bearer " + notionIntegrationKey)
                            .build();

                    return chain.proceed(newRequest);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StringConstants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        NotionAPI notionAPI = retrofit.create(NotionAPI.class);

        try {
            NotionPage rootPage = notionAPI.retrievePage(rootNotionPageId).execute().body();
            LOGGER.info("{}", rootPage);

            NotionBlock pageBlock = notionAPI.retrieveBlock(rootNotionPageId).execute().body();
            LOGGER.info("{}", pageBlock);

            NotionPaginatedResponse<NotionBlock> pageChildren = notionAPI.retrieveBlockChildren(rootNotionPageId, 100).execute().body();
            LOGGER.info("{}", pageChildren);

            NotionRenderer notionRenderer = new NotionRenderer(notionAPI, pageAuthor, pageName);
            notionRenderer.registerDefaultRenderers();

            String renderedPage = notionRenderer.renderPage(rootPage);
            File outFile = new File(outputFolder, "test.html");
            Files.write(outFile.toPath(), renderedPage.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch page!", e);
        }
    }
}
