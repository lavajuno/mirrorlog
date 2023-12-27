package org.lavajuno.mirrorlog.yaml2;

/**
 * YamlPair represents an element containing a key-value pair.
 */
public class YamlPair extends YamlElement {
    protected final String KEY;
    protected final YamlValue VALUE;

    /**
     * Constructs a YamlValue from a line of YAML.
     * We assume that we have checked the input line to make sure it actually contains
     * a value, otherwise this function may behave unexpectedly.
     * @param line Line of YAML to parse
     */
    protected YamlPair(String line) {
        KEY = parseKey(line);
        VALUE = new YamlValue(line.split(":", 2)[1].stripLeading());
    }

    /**
     * @return This YamlPair's value
     */
    public YamlValue getValue() { return VALUE; }

    /**
     * @return This YamlPair's key
     */
    public String getKey() { return KEY; }

    /**
     * @param indent Indent to print with
     * @param list Whether to print as part of a list
     * @return This YamlPair as a String.
     */
    protected String toString(int indent, boolean list) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent));
        if(list) { sb.append("- "); }
        sb.append(this.KEY).append(": ").append(this.VALUE.toString()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0, false); }
}
