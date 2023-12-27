package org.lavajuno.mirrorlog.yaml;

import java.util.InvalidPropertiesFormatException;
import java.util.List;

/**
 * YamlElement represents a single YAML element that can contain one of the following:
 *  - An ordered list of elements
 *  - An unordered map of elements
 *  - A key-value pair
 *  - A value by itself
 */
public abstract class YamlElement {
    /**
     * Matches lines to ignore (blank or commented)
     */
    static final String IGNORE_RGX = "^( *#.*)|( *)$";

    /**
     * Matches lines containing an element (ex. "ElementName: ")
     */
    static final String ELEMENT_RGX =  "^ *(- +)?[A-Za-z0-9_-]+: *$";

    /**
     * Matches lines containing a key-value pair (ex. "ElementName: ElementValue")
     */
    static final String PAIR_RGX = "^ *(- +)?[A-Za-z0-9_-]+: +.+$";

    /**
     * Matches lines containing a list item (ex. "  - ListItem")
     */
    static final String LIST_RGX = "^ *- +[^ ].+$";

    /**
     * Possible matches for a line
     */
    protected enum LINE_MATCHES {
        IGNORE, ELEMENT, PAIR, LIST_ELEMENT, LIST_PAIR, LIST_VALUE, NONE
    }

    /**
     * Constructs and returns a list/map element dependent on the input.
     * @param key Key for the new element
     * @param lines Lines we are parsing
     * @param begin Where to begin parsing
     * @param indent Indent of the new element's elements
     * @return A new YamlElement. (Either a YamlList or a YamlMap)
     * @throws InvalidPropertiesFormatException If an error is encountered while parsing
     */
    protected static YamlElement parseElement(String key, List<String> lines, int begin, int indent)
        throws InvalidPropertiesFormatException {
        int i = begin + 1;
        for(; i < lines.size(); i++) {
            if(!lines.get(i).matches(IGNORE_RGX)) { break; }
        }
        if (i >= lines.size()) {
            throw new InvalidPropertiesFormatException("Reached end of input.");
        }
        return switch (matchLine(lines.get(i))) {
            case LIST_ELEMENT, LIST_PAIR, LIST_VALUE -> new YamlList(key, lines, i, indent);
            case ELEMENT, PAIR -> new YamlMap(key, lines, i, indent);
            default -> {
                printParseError(lines.get(i), i, "Unexpected line.");
                throw new InvalidPropertiesFormatException("Parse error on line " + i + ".");
            }
        };
    }

    /**
     * Matches a line to a predefined category
     * @param line Line to match
     * @return Category this line is matched to (can be NONE)
     */
    protected static LINE_MATCHES matchLine(String line) {
        /* Filter comments and blank lines */
        if(line.matches(IGNORE_RGX)) { return LINE_MATCHES.IGNORE; }
        /* Ordered list elements */
        if(line.matches(LIST_RGX)) {
            if(line.matches(ELEMENT_RGX)) { return LINE_MATCHES.LIST_ELEMENT; }
            if(line.matches(PAIR_RGX)) { return LINE_MATCHES.LIST_PAIR; }
            return LINE_MATCHES.LIST_VALUE;
        }
        /* Unordered map elements */
        if(line.matches(ELEMENT_RGX)) { return LINE_MATCHES.ELEMENT; }
        if(line.matches(PAIR_RGX)) { return LINE_MATCHES.PAIR; }
        /* No match */
        return LINE_MATCHES.NONE;
    }

    /**
     * Parses a line to return the key it contains.
     * If the line does not contain a key, this function may behave unexpectedly.
     * @param line The line to parse
     * @return The key contained in this line
     */
    protected static String parseKey(String line) { return line.stripLeading().split(":", 2)[0]; }

    /**
     * Parses a line to return the number of spaces it is indented by.
     * Probably not the best way to do this, but not a huge issue for now.
     * @param line The line to parse
     * @return How many spaces this line is indented by
     */
    protected static int parseIndent(String line) { return line.length() - line.stripLeading().length(); }

    /**
     * Prints a parse error to stderr.
     * @param line Line to show in the message
     * @param line_index Line index to show in the message
     * @param explanation Explanation to show in the message
     */
    protected static void printParseError(String line, int line_index, String explanation) {
        System.err.println("vvv  YAML - Parse error on line:  vvv");
        System.err.println(line);
        System.err.println("^^^  ---------------------------  ^^^");
        System.err.println("(Line " + line_index + " of input.)");
        System.err.println(explanation + "\n"); /* extra newline */
    }

    /**
     * @param indent Indent of this element
     * @param list Whether this element is a list entry
     * @return This element as a String
     */
    protected abstract String toString(int indent, boolean list);
}
