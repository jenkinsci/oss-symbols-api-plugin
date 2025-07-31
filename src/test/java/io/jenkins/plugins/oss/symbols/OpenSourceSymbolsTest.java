package io.jenkins.plugins.oss.symbols;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class OpenSourceSymbolsTest {

    @Test
    void testGetIconClassName() {
        assertEquals("symbol-test plugin-oss-symbols-api", OpenSourceSymbols.getIconClassName("test"));
    }

    @Test
    void testGetAvailableIcons() throws Exception {
        Map<String, String> availableIcons = OpenSourceSymbols.getAvailableIcons();

        assertNotNull(availableIcons);
        assertFalse(availableIcons.isEmpty());

        Path iconsDir = Path.of("./src/main/resources/images/symbols/");
        try (Stream<Path> stream = Files.list(iconsDir)) {
            List<String> iconNames = stream.filter(path -> path.toString().endsWith(".svg"))
                    .map(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.endsWith(".svg") ? fileName.substring(0, fileName.length() - 4) : fileName;
                    })
                    .sorted(Comparator.naturalOrder())
                    .toList();

            assertEquals(iconNames.size(), availableIcons.size());
            assertIterableEquals(iconNames, availableIcons.keySet());

            for (Map.Entry<String, String> icon : availableIcons.entrySet()) {
                assertEquals(icon.getValue(), OpenSourceSymbols.getIconClassName(icon.getKey()));
            }
        }
    }
}
