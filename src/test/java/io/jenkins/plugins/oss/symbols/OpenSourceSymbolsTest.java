package io.jenkins.plugins.oss.symbols;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

class OpenSourceSymbolsTest {

    @Test
    void testGetIconClassName() {
        assertEquals("symbol-test plugin-oss-symbols-api", OpenSourceSymbols.getIconClassName("test"));
    }

    @Test
    void testGetAvailableIcons() {
        Map<String, String> availableIcons = OpenSourceSymbols.getAvailableIcons();

        assertNotNull(availableIcons);
        assertFalse(availableIcons.isEmpty());

        Collection<File> icons =
                FileUtils.listFiles(new File("./src/main/resources/images/symbols/"), new String[] {"svg"}, false);
        assertEquals(icons.size(), availableIcons.size());

        List<String> iconNames = icons.stream()
                .map(icon -> StringUtils.removeEnd(icon.getName(), ".svg"))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        assertIterableEquals(iconNames, availableIcons.keySet());

        for (Entry<String, String> icon : availableIcons.entrySet()) {
            assertEquals(icon.getValue(), OpenSourceSymbols.getIconClassName(icon.getKey()));
        }
    }
}
