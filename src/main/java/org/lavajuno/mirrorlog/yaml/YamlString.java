package org.lavajuno.mirrorlog.yaml;

public class YamlString extends YamlElement {
    final String contents;

    public YamlString(String line) {
        super(YamlElement.parseKey(line), null);
        this.contents = parseValue(line);
    }

    static String parseValue(String line) {
        String value = line.split(": ")[1];
        String head = value.substring(0, 1);
        /* Trim quotes if they exist */
        if(head.equals("\"") || head.equals("'")) {
            return value.substring(1, value.length() - 1);
        } else {
            return value;
        }
    }

    public String contents() {
        return contents;
    }

    @Override
    public String toString() {
        return "YamlString -- Key: \"" + this.key + "\", Contents: \"" + this.contents + "\"\n";
    }
}
