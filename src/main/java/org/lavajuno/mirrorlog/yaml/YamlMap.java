package org.lavajuno.mirrorlog.yaml;

import java.util.*;

/**
 * YamlMap represents an element containing an unordered map of elements.
 */
public class YamlMap extends YamlElement {
    protected final String KEY;
    protected final TreeMap<String, YamlElement> ELEMENTS;

    /**
     * Constructs a YamlMap by parsing the input.
     * @param lines YAML to parse
     * @throws InvalidPropertiesFormatException If an error is encountered while parsing
     */
    public YamlMap(List<String> lines) throws InvalidPropertiesFormatException {
        this("root", lines, 0, 0, 0);
    }

    /**
     * Constructs a YamlMap.
     * @param key This YamlMap's key
     * @param lines Input we are parsing
     * @param begin Where to begin parsing
     * @param end This will be set to where we stop parsing
     * @param indent Indent of this YamlMap's elements
     * @throws InvalidPropertiesFormatException If an error is encountered while parsing
     */
    protected YamlMap(String key, List<String> lines, int begin, Integer end, int indent) throws InvalidPropertiesFormatException {
        this.KEY = key;
        this.ELEMENTS = new TreeMap<>();
        for(int i = begin; i < lines.size(); i++) {
            String line = lines.get(i);
            int line_indent = parseIndent(line);
            LINE_MATCHES line_match = matchLine(line);

            if(line_indent == indent) { /* If indent matches */
                String curr_key;
                switch(line_match) {
                    case ELEMENT:
                        curr_key = parseKey(line);
                        ELEMENTS.put(curr_key, parseElement(curr_key, lines, i, end, indent + 2));
                        break;
                    case PAIR:
                        curr_key = parseKey(line);
                        ELEMENTS.put(curr_key, new YamlPair(line));
                        break;
                    case IGNORE:
                        break;
                    default:
                        /* If no match was found, we don't know what to do with the line */
                        printParseError(line, i, "Unexpected element. (Not part of an unordered map)");
                        throw new InvalidPropertiesFormatException("Parse error on line " + i + " of input.");
                }
            } else if(line_indent < indent && line_match != LINE_MATCHES.IGNORE) {
                break; /* End of indented block */
            }
            /* If the indent is greater, skip the line. */
        }
    }

    /**
     * @return This YamlMap's key
     */
    public String getKey() { return KEY; }

    /**
     * @return A key-value map of this YamlMap's elements
     */
    public Map<String, YamlElement> getElements() { return ELEMENTS; }

    /**
     * @param key Key of the element to get
     * @return Gets the element with the specified key
     */
    public YamlElement getElement(String key) { return ELEMENTS.get(key); }

    protected String toString(int indent, boolean list) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent));
        if(list) { sb.append("- "); }
        sb.append(KEY).append(":\n");
        for(String i : ELEMENTS.keySet()) {
            sb.append(ELEMENTS.get(i).toString(indent + 2, false));
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0, false); }
}
