package org.lavajuno.lucidjson;

/**
 * Represents a JSON literal value (true/false/null).
 * Provides functionality for getting and setting the value.
 */
@SuppressWarnings("unused")
public class JsonLiteral extends JsonEntity {
    Boolean value;

    /**
     * Constructs a JsonLiteral with a value of null.
     */
    public JsonLiteral() { this.value = null; }

    /**
     * Constructs a JsonLiteral with the given value.
     * @param value Value of this JsonLiteral (true/false/null)
     */
    public JsonLiteral(Boolean value) { this.value = value; }

    /**
     * Constructs a JsonLiteral by parsing the input.
     * @param text JSON to parse
     */
    protected JsonLiteral(String text) {
        switch (text) {
            case "null" -> value = null;
            case "true" -> value = true;
            case "false" -> value = false;
        }
    }

    /**
     * Sets the value of this JsonLiteral.
     * @param value Value of this JsonLiteral (true/false/null)
     */
    public void setValue(Boolean value) { this.value = value; }

    /**
     * Gets the value of this JsonLiteral.
     * @return Value of this JsonLiteral (true/false/null)
     */
    public Boolean getValue() { return value; }

    @Override
    public String toString() {
        if(value == null) { return "null"; }
        return value ? "true" : "false";
    }

    @Override
    protected String toString(int indent) { return this.toString(); }
}
