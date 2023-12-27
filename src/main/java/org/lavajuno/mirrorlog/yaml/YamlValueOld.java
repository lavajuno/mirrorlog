package org.lavajuno.mirrorlog.yaml;

/**
 * YamlValue is a YamlElement that contains a String.
 * A YamlValue never has children (YamlValue.getElements() will always be null).
 */
public class YamlValueOld extends YamlElementOld {
    /**
     * The contents of this YamlValue
     */
    private final String CONTENTS;

    /**
     * Constructs a YamlValue from a line of YAML.
     * We assume that we have checked the input line to make sure it actually contains
     * a value, otherwise this function may behave unexpectedly.
     * @param key Key of this YAMLValue
     * @param line Line of YAML to parse
     * @param list Whether this value should be parsed as part of a list
     */
    YamlValueOld(String key, String line, boolean list) {
        super(key);
        /* Parse line and set value accordingly */
        String value = line.split(": ", 2)[1];
        String head = value.substring(0, 1);
        String tail = value.substring(value.length() - 1);
        /* Trim quotes if they exist */
        if((head.equals("\"") && tail.equals("\"")) ||
                (head.equals("'") && tail.equals("'"))) {
            CONTENTS = value.substring(1, value.length() - 1);
        } else {
            CONTENTS = value;
        }
    }

    /**
     * This YamlValue's contents.
     * @return This YamlValue's contents.
     */
    public String getContents() { return CONTENTS; }

    @Override
    String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        sb.append(indent_prefix).append(this.KEY).append(": ").append(this.CONTENTS).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
