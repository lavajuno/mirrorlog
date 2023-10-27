package org.lavajuno.mirrorlog.yaml;

/**
 * YamlValue is a YamlElement that contains a String.
 * A YamlValue never has children (YamlValue.ELEMENTS will always be null).
 */
public class YamlValue extends YamlElement {
    /**
     * The contents of this YamlValue
     */
    public final String CONTENTS;

    /**
     * Constructs a YamlValue from a line of YAML.
     * We assume that we have checked the input line to make sure it actually contains
     * a value, otherwise this function may behave unexpectedly.
     * @param line Line of YAML to parse
     */
    YamlValue(String line) {
        super(YamlElement.parseKey(line), null);
        /* Parse line and set value accordingly */
        String value = line.split(": ", 2)[1];
        String head = value.substring(0, 1);
        /* Trim quotes if they exist */
        if(head.equals("\"") || head.equals("'")) {
            CONTENTS = value.substring(1, value.length() - 1);
        } else {
            CONTENTS = value;
        }
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        sb.append(indent_prefix).append(this.KEY).append(": ").append(this.CONTENTS).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
