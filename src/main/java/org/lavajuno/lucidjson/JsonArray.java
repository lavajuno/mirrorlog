package org.lavajuno.lucidjson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Represents a JSON array.
 * Provides functionality for accessing and modifying its values.
 */
@SuppressWarnings("unused")
public class JsonArray extends JsonEntity {
    private final Vector<JsonEntity> values;

    /**
     * Constructs an empty JsonArray.
     */
    public JsonArray() { values = new Vector<>(); }

    /**
     * Constructs a JsonArray from the given vector of elements.
     * @param values Values to initialize array with
     */
    public JsonArray(Vector<JsonEntity> values) { this.values = values; }

    /**
     * Constructs a JsonArray by parsing the input.
     * @param text JSON to parse
     * @throws ParseException If an error is encountered while parsing the input
     */
    protected JsonArray(String text) throws ParseException {
        values = parseValues(text.strip());
    }

    /**
     * Deserializes a JSON array from a String.
     * @param text Input string
     * @return Deserialized JSON array
     * @throws ParseException if parsing fails;
     */
    public static JsonArray from(String text) throws ParseException {
        String line = text.replace("\n", "");
        if(!line.matches(ARRAY_RGX)) {
            printError(line, "Expected an array.");
            throw new ParseException("Expected an array.", 0);
        }
        return new JsonArray(line);
    }

    /**
     * Deserializes a JSON array from a list of lines (Strings).
     * @param lines Input lines
     * @return Deserialized JSON array
     * @throws ParseException If parsing fails
     */
    public static JsonArray from(List<String> lines) throws ParseException {
        StringBuilder sb = new StringBuilder();
        for(String i : lines) { sb.append(i); }
        return from(sb.toString());
    }

    /**
     * Deserializes a JSON array from a file.
     * @param file_path Path to the input file
     * @return Deserialized JSON array
     * @throws FileNotFoundException If the file could not be read
     * @throws ParseException If parsing fails
     */
    public static JsonArray fromFile(String file_path) throws FileNotFoundException, ParseException {
        Scanner file = new Scanner(new FileInputStream(file_path));
        StringBuilder lines = new StringBuilder();
        while(file.hasNextLine()) { lines.append(file.nextLine()); }
        file.close();
        return from(lines.toString());
    }

    /**
     * @param text JSON to parse
     * @return Vector created from the input
     * @throws ParseException If an error is encountered while parsing the input
     */
    private static Vector<JsonEntity> parseValues(String text) throws ParseException {
        Vector<JsonEntity> values = new Vector<>();
        Vector<String> raw_values = splitValues(text);
        for(String i : raw_values) {
            if(!i.isEmpty()) { values.add(parseEntity(i.strip())); }
        }
        return values;
    }

    /**
     * @param index Index of the target JsonEntity
     * @return JsonEntity at the given index (null if it does not exist)
     */
    public JsonEntity get(int index) {
        try {
            return values.get(index);
        } catch(ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * @param index Index of the target JsonEntity
     * @param value New value for the target JsonEntity
     */
    public void set(int index, JsonEntity value) { values.set(index, value); }

    /**
     * @param value JsonEntity to be added to this JsonArray
     */
    public void add(JsonEntity value) { values.add(value); }

    /**
     * @param index Index of the JsonEntity to remove
     */
    public void remove(int index) { values.remove(index); }

    /**
     * Clears this JsonArray
     */
    public void clear() { values.clear(); }

    /**
     * @return The number of entities contained by this JsonArray
     */
    public int size() { return values.size(); }

    /**
     * @return This JsonArray's elements
     */
    public Vector<JsonEntity> getValues() { return values; }

    /**
     * Serializes this JsonArray to a String, with indentation and newlines.
     * @param indent Indent of this JsonEntity (0)
     * @return Returns this JsonEntity as a string.
     */
    @Override
    protected String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String pad_elem = " ".repeat(indent + 4);
        String pad_close = " ".repeat(indent);
        sb.append("[\n");
        for(int i = 0; i < values.size() - 1; i++) {
            sb.append(pad_elem).append(values.get(i).toString(indent + 4)).append(",\n");
        }
        if(!values.isEmpty()) {
            sb.append(pad_elem).append(values.get(values.size() - 1)).append("\n");
        }
        sb.append(pad_close).append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < values.size() - 1; i++) {
            sb.append(values.get(i)).append(",\n");
        }
        if(!values.isEmpty()) {
            sb.append(values.get(values.size() - 1));
        }
        sb.append("]");
        return sb.toString();
    }
}
