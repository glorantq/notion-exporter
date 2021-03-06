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

package hu.glorantq.notion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import hu.glorantq.notion.api.QueryDatabaseBody;
import hu.glorantq.notion.api.model.blocks.NotionBlock;
import hu.glorantq.notion.api.model.blocks.NotionNotImplementedBlock;
import hu.glorantq.notion.export.NotionExporterImplementation;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

        argumentParser.addArgument("--fancyNames", "-fn")
                .nargs("?")
                .type(Boolean.class)
                .action(Arguments.store())
                .help("Rewrite page files with human-readable names");

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

    private static void handleStartupFile(String filePath) {
        File configFile = new File(filePath);

        if(!configFile.exists() || !configFile.canRead() || configFile.isDirectory()) {
            LOGGER.error("Invalid configuration file: {}", configFile.getAbsolutePath());
            return;
        }

        Toml parsedConfig;

        try {
            parsedConfig = new Toml().read(configFile);
        } catch (Exception e) {
            LOGGER.error("Failed to parse configuration file!", e);
            return;
        }

        Map<String, DatabaseConfiguration> databaseConfigurationMap =  new HashMap<>();
        File databaseConfigFile = configFile.toPath().resolveSibling("database.toml").toFile();
        if(databaseConfigFile.exists()) {
            Map<String, Object> databaseConfig;

            try {
                databaseConfig = new Toml().read(databaseConfigFile).toMap();
            } catch (Exception e) {
                LOGGER.error("Failed to parse database configuration!", e);
                return;
            }

            for(Map.Entry<String, Object> rootEntry : databaseConfig.entrySet()) {
                Gson gson = new GsonBuilder().create();

                String databaseKey = rootEntry.getKey();

                Map<String, Object> values = (Map<String, Object>) rootEntry.getValue();
                Object filterJson0 = values.get("filter");
                Object sortsJson0 = values.get("sorts");
                Object columnsJson0 = values.get("columns");
                Object display0 = values.get("display");
                Object groupBy0 = values.get("groupBy");

                String filterJson;
                if(filterJson0 == null) {
                    filterJson = null;
                } else {
                    filterJson = String.valueOf(filterJson0);
                }

                QueryDatabaseBody.NotionSortObject[] sorts;
                if(sortsJson0 == null) {
                    sorts = new QueryDatabaseBody.NotionSortObject[0];
                } else {
                    sorts = Arrays.stream(((ArrayList) sortsJson0).toArray(new Object[0]))
                            .map(it -> gson.fromJson(String.valueOf(it), QueryDatabaseBody.NotionSortObject.class))
                            .toArray(QueryDatabaseBody.NotionSortObject[]::new);
                }

                String[] columns;
                if(columnsJson0 == null) {
                    columns = new String[0];
                    LOGGER.warn("Invalid columns declaration for {}, everything will be rendered!", databaseKey);
                } else {
                    columns = Arrays.stream(((ArrayList) columnsJson0).toArray(new Object[0]))
                            .map(String::valueOf)
                            .toArray(String[]::new);
                }

                DatabaseConfiguration.Display display;
                if(display0 != null) {
                    display = DatabaseConfiguration.Display.valueOf(String.valueOf(display0).toUpperCase());
                } else {
                    display = DatabaseConfiguration.Display.TABLE;
                    LOGGER.warn("No display specified for {}, using table!", databaseKey);
                }

                String groupBy;
                if(groupBy0 == null) {
                    groupBy = null;
                } else {
                    groupBy = String.valueOf(groupBy0);
                }

                DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(sorts, filterJson, columns, display, groupBy);
                databaseConfigurationMap.put(databaseKey, databaseConfiguration);
            }
        } else {
            LOGGER.warn("No database.toml found!");
        }

        LOGGER.info("Using configuration file: {}", configFile.getAbsolutePath());

        try {
            Map<String, Object> siteProperties = parsedConfig.getTable("site").toMap();

            String pageName = getValueFromMap(siteProperties, "name");
            String pageAuthor = getValueFromMap(siteProperties, "author");
            String faviconPath = getValueFromMap(siteProperties, "favicon");

            Map<String, Object> notionProperties = parsedConfig.getTable("notion").toMap();

            String rootNotionPageId = getValueFromMap(notionProperties, "rootPageId");
            boolean followLinkedPages = Boolean.parseBoolean(getValueFromMap(notionProperties, "followLinks"));
            String notionIntegrationKey = getValueFromMap(notionProperties, "integrationKey");

            Map<String, Object> exportProperties = parsedConfig.getTable("export").toMap();
            String outFolder = getValueFromMap(exportProperties, "folder");
            boolean rewriteIndex = Boolean.parseBoolean(getValueFromMap(exportProperties, "rewriteIndex"));
            boolean rewriteNames = Boolean.parseBoolean(getValueFromMap(exportProperties, "fancyNames"));

            handleStartup(pageName, pageAuthor, faviconPath, rootNotionPageId, notionIntegrationKey, followLinkedPages, outFolder, rewriteIndex, rewriteNames, databaseConfigurationMap);
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
            boolean rewriteNames = getValueFromNamespace(namespace, "fancyNames");

            LOGGER.warn("Can't handle advanced configuration for databases in CLI mode! Consider using a configuration file instead.");
            handleStartup(pageName, pageAuthor, favicon, rootPageId, notionKey, followLinks, outFolder, rewriteIndex, rewriteNames, new HashMap<>());
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
                                      String outFolder, boolean rewriteIndex, boolean rewriteNames, Map<String, DatabaseConfiguration> databaseConfigurationMap) {
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

        File assetsDirectory = new File(outputFolder, "assets");
        File userAssets = new File(assetsDirectory, "user");
        if(!userAssets.exists()) {
            userAssets.mkdirs();
        }

        File iconFile = new File(faviconPath);
        if(!iconFile.exists()) {
            LOGGER.warn("Missing or invalid icon path!");
        } else {
            File outIcon = new File(userAssets, "favicon.png");

            try {
                FileInputStream fileInputStream = new FileInputStream(iconFile);
                FileOutputStream fileOutputStream = new FileOutputStream(outIcon);
                IOUtils.copy(fileInputStream, fileOutputStream);
                fileOutputStream.close();
                fileInputStream.close();

                LOGGER.info("Copied icon!");
            } catch (Exception e) {
                LOGGER.error("Failed to copy icon!", e);
            }
        }

        LOGGER.info("Extracting asset bundle...");
        try {
            File tempAssetBundle = new File(assetsDirectory, "assetbundle_temp.zip");
            InputStream resourceStream = NotionExporter.class.getClassLoader().getResourceAsStream("assetbundle");
            FileOutputStream fileOutputStream = new FileOutputStream(tempAssetBundle);
            IOUtils.copy(resourceStream, fileOutputStream);
            fileOutputStream.close();
            resourceStream.close();

            FileSystem zipFileSystem = FileSystems.newFileSystem(tempAssetBundle.toPath(), null);
            Path zipRoot = zipFileSystem.getPath("/");
            Files.walkFileTree(zipRoot, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path zipFilePath = zipRoot.relativize(file);
                    Path outPath = assetsDirectory.toPath().resolve(zipFilePath.toString());
                    Files.createDirectories(outPath.getParent());
                    Files.copy(file, outPath);

                    return FileVisitResult.CONTINUE;
                }
            });

            tempAssetBundle.delete();
            LOGGER.info("Extracted assets!");
        } catch (Exception e) {
            LOGGER.error("Failed to extract asset bundle!", e);
        }

        NotionBlock.Type[] blockTypes = NotionBlock.Type.values();
        for(NotionBlock.Type blockType : blockTypes) {
            if(blockType.getTypeClass() == NotionNotImplementedBlock.class) {
                LOGGER.warn("Found unimplemented block supported by the API: {}!", blockType.name().toLowerCase());
            }
        }

        try {
            NotionExporterImplementation exporterImplementation = new NotionExporterImplementation(notionIntegrationKey, rootNotionPageId, rewriteIndex, rewriteNames, followLinkedPages,
                    true, pageName, pageAuthor, outputFolder, databaseConfigurationMap);
            exporterImplementation.beginRendering();
        } catch (Exception e) {
            LOGGER.error("Failed to fetch page!", e);
        }
    }
}
