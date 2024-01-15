package org.lavajuno.lucidjson;

/**
 * Represents a JSON string value.
 * Provides functionality for getting and setting the value.
 */
@SuppressWarnings("unused")
public class JsonString extends JsonEntity {
    private String value;

    /**
     * Constructs a JsonValue by parsing the input.
     * @param text JSON string to parse
     */
    public JsonString(String text) {
        if(text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"') {
            value = text.substring(1, text.length() - 1);
        } else {
            value = text;
        }
    }

    /**
     * Gets the value of this JsonString.
     * @return Value of this JsonString
     */
    public String getValue() { return value; }

    /**
     * Sets the value of this JsonString
     * @param value Value of this JsonString
     */
    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    protected String toString(int indent) { return this.toString(); }
}
