package org.lavajuno.mirrorlog.yaml2;

/**
 * Represents a key-value pair. (ex. "MyKey: MyValue")
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

    public YamlValue getValue() { return VALUE; }

    public String getKey() { return KEY; }

    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.KEY).append(": ").append(this.VALUE.getString()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
