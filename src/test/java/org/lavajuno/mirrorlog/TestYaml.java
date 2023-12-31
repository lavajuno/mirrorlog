package org.lavajuno.mirrorlog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lavajuno.mirrorlog.yaml.*;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

public class TestYaml {
    /**
     * Tests basic YAML comprehension.
     * Instantiates an element with a few child elements of different types.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testBasicYaml() throws IOException {
        Vector<String> test_lines = new Vector<>();
        test_lines.add("MapElement1:");
        test_lines.add("  PairElement1: ValueElement1");
        test_lines.add("  ListElement1:");
        test_lines.add("    - ListElement2:");
        test_lines.add("      - ValueElement2");
        test_lines.add("    - PairElement2: ValueElement3");

        YamlMap root = new YamlMap(test_lines);
        System.out.println(root.toString());

        YamlMap map_element_1 = (YamlMap) root.getElement("MapElement1");
        YamlPair pair_element_1 = (YamlPair) map_element_1.getElement("PairElement1");
        YamlValue value_element_1 = pair_element_1.getValue();
        YamlList list_element_1 = (YamlList) map_element_1.getElement("ListElement1");
        YamlList list_element_2 = (YamlList) list_element_1.getElement(0);
        YamlValue value_element_2 = (YamlValue) list_element_2.getElement(0);
        YamlPair pair_element_2 = (YamlPair) list_element_1.getElement(1);
        YamlValue value_element_3 = pair_element_2.getValue();

        Assertions.assertEquals("MapElement1", map_element_1.getKey());
        Assertions.assertEquals("PairElement1", pair_element_1.getKey());
        Assertions.assertEquals("ValueElement1", value_element_1.toString());
        Assertions.assertEquals("ListElement1", list_element_1.getKey());
        Assertions.assertEquals("ListElement2", list_element_2.getKey());
        Assertions.assertEquals("ValueElement2", value_element_2.toString());
        Assertions.assertEquals("PairElement2", pair_element_2.getKey());
        Assertions.assertEquals("ValueElement3", value_element_3.toString());
    }

    /**
     * Tests response to bad indentation.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testBadIndentation() throws IOException {
        Vector<String> test_lines = new Vector<>();
        test_lines.add("Element1:");
        test_lines.add("    Element2:");
        test_lines.add("      Element3:");

        YamlMap root = new YamlMap(test_lines);
        YamlMap element_1 = (YamlMap) root.getElement("Element1");
        Assertions.assertNotNull(element_1);
        Assertions.assertEquals(element_1.getElements().size(), 0);
    }

    /**
     * Tests response to messy indentation.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testMessyIndentation() throws IOException {
        Vector<String> test_lines = new Vector<>();
        test_lines.add("Element1:");
        test_lines.add("    Element2:");
        test_lines.add("  Element3:");
        test_lines.add("    Element4: Value5");

        YamlMap root = new YamlMap(test_lines);
        YamlMap element_1 = (YamlMap) root.getElement("Element1");
        Assertions.assertNotNull(element_1);
        Assertions.assertEquals(element_1.getElements().size(), 1);
        YamlMap element_3 = (YamlMap) element_1.getElement("Element3");
        Assertions.assertNotNull(element_3);
    }

    /**
     * Tests response to bad keys.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testBadKeys() throws IOException {
        Vector<String> test_lines = new Vector<>();

        test_lines.add("Element1:");
        test_lines.add("  not valid");
        try {
            new YamlMap(test_lines);
            throw new IOException("Accepted bad key.");
        } catch(InvalidPropertiesFormatException ignored) {}
        test_lines.clear();

        test_lines.add("Element1: ");
        test_lines.add("  :");
        try {
            new YamlMap(test_lines);
            throw new IOException("Accepted bad key.");
        } catch(InvalidPropertiesFormatException ignored) {}
        test_lines.clear();

        test_lines.add("Element1  : ");
        test_lines.add("  Element2");
        try {
            new YamlMap(test_lines);
            throw new IOException("Accepted bad key.");
        } catch(InvalidPropertiesFormatException ignored) {}
    }

    /**
     * Tests response to bad lists.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testBadLists() throws IOException {
        Vector<String> test_lines = new Vector<>();

        test_lines.add("Element1:");
        test_lines.add("  -     ");
        try {
            new YamlMap(test_lines);
            throw new IOException("Accepted bad list.");
        } catch(InvalidPropertiesFormatException ignored) {}
        test_lines.clear();

        test_lines.add("Element1: ");
        test_lines.add("  -bad");
        try {
            new YamlMap(test_lines);
            throw new IOException("Accepted bad list.");
        } catch(InvalidPropertiesFormatException ignored) {}
    }

    /**
     * Tests response to bad strings.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testBadStrings() throws IOException {
        Vector<String> test_lines = new Vector<>();
        test_lines.add("Element1:bad");
        try {
            new YamlMap(test_lines);
            throw new IOException("YamlMap accepted bad string.");
        } catch(InvalidPropertiesFormatException ignored) {}

        test_lines.clear();
        test_lines.add("-element2bad");
        try {
            new YamlMap(test_lines);
            throw new IOException("YamlMap accepted bad string.");
        } catch(InvalidPropertiesFormatException ignored) {}
    }

    /**
     * Tests a more complex YAML structure.
     * Instantiates an element with a few child elements of different types.
     * @throws IOException Passes on IOExceptions from the YAML class
     */
    @Test
    public void testComplexYaml() throws IOException {
        Vector<String> test_lines = new Vector<>();
        test_lines.add("config:");
        test_lines.add("  version: 1.0");
        test_lines.add("  server: ");
        test_lines.add("    port: 1234");
        test_lines.add("    firewall: 0");
        test_lines.add("    allowed_ips:");
        test_lines.add("      -  10.0.0.1");
        test_lines.add("      - \"10.0.0.2\"");
        test_lines.add("    container:");
        test_lines.add("      provider: screen");
        test_lines.add("      user: server");
        test_lines.add("      args:");
        test_lines.add("        - arg1");

        YamlMap root = new YamlMap(test_lines);
        System.out.println(root);
    }
}
