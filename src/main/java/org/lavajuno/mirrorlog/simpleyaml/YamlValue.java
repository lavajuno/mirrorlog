package org.lavajuno.mirrorlog.simpleyaml;

/**
 * YamlValue is a YamlElement that contains a String.
 * A YamlValue never has children (YamlValue.ELEMENTS will always be null).
 */
public class YamlValue extends YamlElement {
    public final String CONTENTS;

    /**
     * Constructs a YamlValue.
     * @param line Line of YAML to parse
     */
    YamlValue(String line) {
        super(YamlElement.parseKey(line), null);
        this.CONTENTS = parseValue(line);
    }

    /**
     * Parses a line of YAML containing an element with a value and returns its value.
     * We assume that we have checked the line to make sure it actually contains
     * a value, otherwise this function may behave unexpectedly.
     * @param line Line of YAML to parse
     * @return The value of the YAML element in the given line
     */
    private static String parseValue(String line) {
        String value = line.split(": ", 2)[1];
        String head = value.substring(0, 1);
        /* Trim quotes if they exist */
        if(head.equals("\"") || head.equals("'")) {
            return value.substring(1, value.length() - 1);
        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        return "YamlValue - Key: \"" + this.KEY + "\", Contents: \"" + this.CONTENTS + "\"\n";
    }
}
