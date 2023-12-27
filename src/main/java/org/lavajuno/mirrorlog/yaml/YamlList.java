package org.lavajuno.mirrorlog.yaml;

import java.util.InvalidPropertiesFormatException;
import java.util.Vector;
import java.util.List;

/**
 * YamlList represents an element containing an ordered list of elements.
 */
public class YamlList extends YamlElement {
    protected final String KEY;
    protected final Vector<YamlElement> ELEMENTS;
    /**
     * Constructs a YamlMap.
     * @param key This YamlMap's key
     * @param lines Input we are parsing
     * @param begin Where to begin parsing
     * @param end This will be set to where we stop parsing
     * @param indent Indent of this YamlMap's elements
     * @throws InvalidPropertiesFormatException If an error is encountered while parsing
     */
    protected YamlList(String key, List<String> lines, int begin, Integer end, int indent) throws InvalidPropertiesFormatException {
        this.KEY = key;
        this.ELEMENTS = new Vector<>();

        for(int i = begin; i < lines.size(); i++) {
            String line = lines.get(i);
            int line_indent = parseIndent(line);
            LINE_MATCHES line_match = matchLine(line);

            if(line_indent == indent) { /* If indent matches */
                switch(line_match) {
                    case LIST_ELEMENT:
                        String curr_key = parseKey(line.split("-", 2)[1]);
                        ELEMENTS.add(parseElement(curr_key, lines, i, end, indent + 2));
                        break;
                    case LIST_PAIR:
                        ELEMENTS.add(new YamlPair(line.split("-", 2)[1].stripLeading()));
                        break;
                    case LIST_VALUE:
                        ELEMENTS.add(new YamlValue(line.split("-", 2)[1].stripLeading()));
                        break;
                    case IGNORE:
                        break;
                    default:
                        /* If no match was found, we don't know what to do with the line */
                        printParseError(line, i, "Unexpected element. (Not part of an ordered list)");
                        throw new InvalidPropertiesFormatException("Parse error on line " + i + " of input.");
                }
            } else if(line_indent < indent && line_match != LINE_MATCHES.IGNORE) {
                break; /* End of indented block */
            }
            /* If the indent is greater, skip the line. */
        }
    }

    /**
     * @return This YamlList's key
     */
    public String getKey() { return KEY; }

    /**
     * @return This YamlList's elements
     */
    public List<YamlElement> getElements() { return ELEMENTS; }

    /**
     * @param index Index of the element to get
     * @return Gets the element at the specified index
     */
    public YamlElement getElement(int index) { return ELEMENTS.get(index); }

    /**
     * @return The number of elements in this YamlList
     */
    public int getSize() { return ELEMENTS.size(); }

    protected String toString(int indent, boolean list) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(indent));
        if(list) { sb.append("- "); }
        sb.append(KEY).append(":\n");
        for(YamlElement i : ELEMENTS) {
            sb.append(i.toString(indent + 2, true));
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0, false); }
}
