package org.lavajuno.lucidjson;

/**
 * Represents a JSON number value.
 * Provides functionality for getting and setting the value as an int, long, float, or double.
 */
@SuppressWarnings("unused")
public class JsonNumber extends JsonEntity {
    private String value;

    /**
     * Constructs a JsonNumber from an int.
     * @param value Value of this JsonNumber
     */
    public JsonNumber(int value) { this.value = Integer.toString(value); }

    /**
     * Constructs a JsonNumber from a long.
     * @param value Value of this JsonNumber
     */
    public JsonNumber(long value) { this.value = Long.toString(value); }

    /**
     * Constructs a JsonNumber from a float.
     * @param value Value of this JsonNumber
     */
    public JsonNumber(float value) { this.value = Float.toString(value); }

    /**
     * Constructs a JsonNumber from a double.
     * @param value Value of this JsonNumber
     */
    public JsonNumber(double value) { this.value = Double.toString(value); }

    /**
     * Constructs a JsonNumber from the input.
     * @param value JSON number
     */
    protected JsonNumber(String value) {
        this.value = value;
    }

    /**
     * Gets the value of this JsonNumber.
     * @return Value of this JsonNumber as an int
     * @throws NumberFormatException If this JsonNumber cannot be parsed as an int
     */
    public int getInt() throws NumberFormatException { return Integer.parseInt(value); }

    /**
     * Gets the value of this JsonNumber.
     * @return Value of this JsonNumber as a long
     * @throws NumberFormatException If this JsonNumber cannot be parsed as a long
     */
    public long getLong() throws NumberFormatException { return Long.parseLong(value); }

    /**
     * Gets the value of this JsonNumber.
     * @return Value of this JsonNumber as a float
     * @throws NumberFormatException If this JsonNumber cannot be parsed as a float
     */
    public float getFloat() throws NumberFormatException { return Float.parseFloat(value); }

    /**
     * Gets the value of this JsonNumber.
     * @return Value of this JsonNumber as a double
     * @throws NumberFormatException If this JsonNumber cannot be parsed as a double
     */
    public double getDouble() throws NumberFormatException { return Double.parseDouble(value); }

    /**
     * Sets the value of this JsonNumber.
     * @param value Int value of this JsonNumber
     */
    public void set(int value) { this.value = Integer.toString(value); }

    /**
     * Sets the value of this JsonNumber.
     * @param value Long value of this JsonNumber
     */
    public void set(long value) { this.value = Long.toString(value); }

    /**
     * Sets the value of this JsonNumber.
     * @param value Float value of this JsonNumber
     */
    public void set(float value) { this.value = Float.toString(value); }

    /**
     * Sets the value of this JsonNumber.
     * @param value Double value of this JsonNumber
     */
    public void set(double value) { this.value = Double.toString(value); }

    @Override
    public String toString() { return value; }

    @Override
    protected String toString(int indent) { return this.toString(); }
}
