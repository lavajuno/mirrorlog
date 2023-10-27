package src.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lavajuno.mirrorlog.yaml.YamlElement;
import org.lavajuno.mirrorlog.yaml.YamlList;
import org.lavajuno.mirrorlog.yaml.YamlValue;

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
        test_lines.add("Element1:");
        test_lines.add("  Element2:");
        test_lines.add("    -  ListItem1");
        test_lines.add("    -  \"ListItem2\"");
        test_lines.add("  Element3: Value3");
        test_lines.add("  Element4: \"Value4\"");

        YamlElement root = new YamlElement(test_lines);
        YamlElement element_1 = root.getElement("Element1");
        Assertions.assertNotNull(element_1);
        YamlList element_2 = (YamlList) element_1.getElement("Element2");
        Assertions.assertNotNull(element_2);
        YamlValue element_3 = (YamlValue) element_1.getElement("Element3");
        Assertions.assertNotNull(element_3);
        YamlValue element_4 = (YamlValue) element_1.getElement("Element4");
        Assertions.assertNotNull(element_4);

        Assertions.assertEquals("ListItem1", element_2.getContents().get(0));
        Assertions.assertEquals("ListItem2", element_2.getContents().get(1));
        Assertions.assertEquals("Value3", element_3.getContents());
        Assertions.assertEquals("Value4", element_4.getContents());
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

        YamlElement root = new YamlElement(test_lines);
        YamlElement element_1 = root.getElement("Element1");
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

        YamlElement root = new YamlElement(test_lines);
        YamlElement element_1 = root.getElement("Element1");
        Assertions.assertNotNull(element_1);
        Assertions.assertEquals(element_1.getElements().size(), 1);
        YamlElement element_3 = element_1.getElement("Element3");
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
            new YamlElement(test_lines);
            throw new IOException("YamlElement accepted bad key.");
        } catch(InvalidPropertiesFormatException ignored) {}
        test_lines.clear();

        test_lines.add("Element1: ");
        test_lines.add("  :");
        try {
            new YamlElement(test_lines);
            throw new IOException("YamlElement accepted bad key.");
        } catch(InvalidPropertiesFormatException ignored) {}
        test_lines.clear();

        test_lines.add("Element1  : ");
        test_lines.add("  Element2");
        try {
            new YamlElement(test_lines);
            throw new IOException("YamlElement accepted bad key.");
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
            new YamlElement(test_lines);
            throw new IOException("YamlElement accepted bad list.");
        } catch(InvalidPropertiesFormatException ignored) {}
        test_lines.clear();

        test_lines.add("Element1: ");
        test_lines.add("  -bad");
        try {
            new YamlElement(test_lines);
            throw new IOException("YamlElement accepted bad list.");
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
            new YamlElement(test_lines);
            throw new IOException("YamlElement accepted bad string.");
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
        test_lines.add("    -  10.0.0.1");
        test_lines.add("    - \"10.0.0.2\"");
        test_lines.add("    container:");
        test_lines.add("      provider: screen");
        test_lines.add("      user: server");
        test_lines.add("      args:");
        test_lines.add("        - arg1");

        YamlElement root = new YamlElement(test_lines);
        System.out.println(root);
    }
}
