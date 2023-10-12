package org.lavajuno.mirrorlog.yaml;

@SuppressWarnings("unused")
public class YamlString extends YamlElement {
    public final String CONTENTS;

    public YamlString(String key, String contents) {
        super(key, null);
        this.CONTENTS = contents;
    }

    public YamlString(String line) {
        super(YamlElement.parseKey(line), null);
        this.CONTENTS = parseValue(line);
    }

    static String parseValue(String line) {
        String value = line.split(": ")[1];
        String head = line.substring(0, 1);
        if(head.equals("\"") || head.equals("'")) {
            return value.substring(1, value.length() - 1);
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "YamlString: Key-\"" + this.key + "\" Contents-\"" + this.CONTENTS + "\".\n";
    }
}
