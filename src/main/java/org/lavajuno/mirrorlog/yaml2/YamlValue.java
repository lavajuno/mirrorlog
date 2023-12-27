package org.lavajuno.mirrorlog.yaml2;

/**
 * YamlValue represents a string or numeric value.
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

    /**
     * @return Integer value of this YamlValue
     * @throws NumberFormatException If this YamlValue cannot be converted to an Integer
     */
    public int toInt() throws NumberFormatException { return Integer.parseInt(STR_VALUE); }

    /**
     * @return Long value of this YamlValue
     * @throws NumberFormatException If this YamlValue cannot be converted to a Long
     */
    public long toLong() throws NumberFormatException { return Long.parseLong(STR_VALUE); }

    /**
     * @return Float value of this YamlValue
     * @throws NumberFormatException If this YamlValue cannot be converted to a Float
     */
    public float toFloat() throws NumberFormatException { return Float.parseFloat(STR_VALUE); }

    /**
     * @return Double value of this YamlValue
     * @throws NumberFormatException If this YamlValue cannot be converted to a Double
     */
    public double toDouble() throws NumberFormatException { return Double.parseDouble(STR_VALUE); }

    /**
     * @param indent (Ignored) Indent to print with
     * @param list (Ignored) Whether to print as part of a list
     * @return This YamlValue as a String
     */
    protected String toString(int indent, boolean list) {
        if(!list) { return STR_VALUE; }
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent)).append("- ");
        sb.append(STR_VALUE).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0, false); }
}
