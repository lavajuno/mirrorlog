package org.lavajuno.mirrorlog.yaml2;

/**
 * Represents a string or numeric value.
 */
public class YamlValue extends YamlElement {
    private final String STR_VALUE;

    /**
     * Constructs a YamlValue from a string.
     * @param raw String value of this YamlValue
     */
    protected YamlValue(String raw) {
        if(raw.matches("^\".*\"$")) {
            STR_VALUE = raw.substring(1, raw.length() - 1);
        } else {
            STR_VALUE = raw;
        }
    }

    public String getString() { return STR_VALUE; }

    public int getInt() throws NumberFormatException { return Integer.parseInt(STR_VALUE); }

    public long getLong() throws NumberFormatException { return Long.parseLong(STR_VALUE); }

    public float getFloat() throws NumberFormatException { return Float.parseFloat(STR_VALUE); }

    public double getDouble() throws NumberFormatException { return Double.parseDouble(STR_VALUE); }

    public String toString(int indent) { return STR_VALUE; }

    @Override
    public String toString() { return this.toString(0); }
}
