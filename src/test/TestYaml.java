package src.test;

import org.junit.jupiter.api.Test;
import org.lavajuno.mirrorlog.yaml.YamlElement;

import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

public class TestYaml {
    @Test
    public void testYamlRead() {
        Vector<String> lines = new Vector<String>();
        lines.add(" # TestComment");
        lines.add("TestObject:");
        lines.add("  TOMember1: \"Test1\"");
        lines.add("  TOMember2: Test2");
        lines.add(" # TestComment");
        lines.add("  TOMember3:");
        lines.add("    - TOMember3E1");
        lines.add(" # TestComment");
        lines.add("    - TOMember2E1");
        lines.add("  TOMember4: Test3");
        lines.add("TestObject2:");
        try {
            YamlElement root = new YamlElement(lines);
            System.out.println(root.toString());
        } catch(InvalidPropertiesFormatException e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

    }


}
