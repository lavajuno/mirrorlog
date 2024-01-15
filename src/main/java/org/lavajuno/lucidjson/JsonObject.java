package org.lavajuno.lucidjson;

import org.lavajuno.lucidjson.util.Pair;

import java.text.ParseException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Represents a JSON object.
 * Provides functionality for accessing and modifying its values.
 */
@SuppressWarnings("unused")
public class JsonObject extends JsonEntity {
    private final TreeMap<String, JsonEntity> values;

    /**
     * Constructs an empty JsonObject.
     */
    public JsonObject() { values = new TreeMap<>(); }

    /**
     * Constructs a JsonObject from the given map.
     * @param values Values to initialize map with
     */
    public JsonObject(TreeMap<String, JsonEntity> values) { this.values = values; }

    /**
     * Constructs a JsonObject by parsing the input.
     * @param text JSON to parse
     * @throws ParseException If an error is encountered while parsing the input
     */
    protected JsonObject(String text) throws ParseException {
        values = parseValues(text.strip());
    }

    /**
     * @param text JSON to parse
     * @return Key-value map created from the input
     * @throws ParseException If an error is encountered while parsing the input
     */
    private static TreeMap<String, JsonEntity> parseValues(String text) throws ParseException {
        TreeMap<String, JsonEntity> values = new TreeMap<>();
        Vector<String> raw_values = splitValues(text);
        for(String i : raw_values) {
            if(!i.isEmpty()) {
                Pair<String, JsonEntity> p = parsePair(i.strip());
                values.put(p.first, p.second);
            }
        }
        return values;
    }

    /**
     * Gets a JsonEntity with a given key.
     * @param key Key of the target JsonEntity
     * @return JsonEntity with the specified key, or null if it does not exist
     */
    public JsonEntity get(String key) { return values.get(key); }

    /**
     * Puts a JsonEntity under a given key. Will overwrite the previous
     * entity if it already exists.
     * @param key Key of the target JsonEntity
     * @param value New value for the target JsonEntity
     */
    public void put(String key, JsonEntity value) { values.put(key, value); }

    /**
     * Removes the JsonEntity at the specified key.
     * @param key Key of the JsonEntity to remove
     */
    public void remove(String key) { values.remove(key); }

    /**
     * Clears this JsonObject's map.
     */
    public void clear() { values.clear(); }

    /**
     * Gets a collection of all the keys in this JsonObject
     * @return This JsonObject's keys
     */
    public Collection<String> getKeys() { return values.keySet(); }

    /**
     * Gets a collection of all the values in this JsonObject
     * @return This JsonObject's values
     */
    public Collection<JsonEntity> getValues() { return values.values(); }

    /**
     * Gets ths size of this JsonObject
     * @return The number of entities contained by this JsonObject
     */
    public int size() { return values.size(); }

    @Override
    protected String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String pad_elem = " ".repeat(indent + 4);
        String pad_close = " ".repeat(indent);
        sb.append("{\n");
        Set<String> keys = values.keySet();
        int i = 0;
        for(String j : keys) {
            i++;
            sb.append(pad_elem).append("\"").append(j).append("\": ");
            sb.append(values.get(j).toString(indent + 4));
            if(i < keys.size()) { sb.append(","); }
            sb.append("\n");
        }
        sb.append(pad_close).append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Set<String> keys = values.keySet();
        int i = 0;
        for(String j : keys) {
            i++;
            sb.append("\"").append(j).append("\":").append(values.get(j));
            if(i < keys.size()) { sb.append(","); }
        }
        sb.append("}");
        return sb.toString();
    }
}
