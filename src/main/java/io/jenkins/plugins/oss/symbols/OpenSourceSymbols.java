package io.jenkins.plugins.oss.symbols;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;

public final class OpenSourceSymbols {

    private static final Logger LOGGER = Logger.getLogger(OpenSourceSymbols.class.getName());
    private static final String SVG_FILE_ENDING = ".svg";
    private static final String IMAGES_SYMBOLS_PATH = "images/symbols/";
    private static final String API_PLUGIN = "oss-symbols-api";
    private static final String ICON_CLASS_NAME_PATTERN = "symbol-%s plugin-" + API_PLUGIN;

    private static final OpenSourceSymbols INSTANCE = new OpenSourceSymbols();

    private final Map<String, String> availableIcons = new ConcurrentSkipListMap<>();

    private OpenSourceSymbols() {
        try {
            Enumeration<URL> urls = getClass().getClassLoader().getResources(IMAGES_SYMBOLS_PATH);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();

                if (StringUtils.contains(url.toExternalForm(), API_PLUGIN)) {
                    URI uri = url.toURI();

                    if (StringUtils.equals(uri.getScheme(), "jar")) {
                        try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                            collectIcons(fileSystem.getPath(IMAGES_SYMBOLS_PATH));
                        }
                    } else {
                        collectIcons(Paths.get(uri));
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Unable to read available icons: Resource unavailable.", ex);
        }
    }

    private void collectIcons(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path, 1)) {
            stream.filter(icon -> StringUtils.endsWith(icon.getFileName().toString(), SVG_FILE_ENDING))
                    .forEach(icon -> {
                        String iconName =
                                StringUtils.removeEnd(icon.getFileName().toString(), SVG_FILE_ENDING);
                        availableIcons.put(iconName, getIconClassName(iconName));
                    });
        }
    }

    public static String getIconClassName(String icon) {
        return String.format(ICON_CLASS_NAME_PATTERN, icon);
    }

    public static Map<String, String> getAvailableIcons() {
        return INSTANCE.availableIcons;
    }
}
