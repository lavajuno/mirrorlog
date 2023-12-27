package org.lavajuno.mirrorlog.yaml2;

import java.util.InvalidPropertiesFormatException;
import java.util.List;

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

    protected static YamlElement parseElement(List<String> lines, int begin, Integer end, int indent)
        throws InvalidPropertiesFormatException {
        int i = begin;
        for(; i < lines.size(); i++) {
            if(!lines.get(i).matches(IGNORE_RGX)) { break; }
        }
        return switch (matchLine(lines.get(i))) {
            case ELEMENT, PAIR -> new YamlMap(lines, i, end, indent);
            case LIST_ELEMENT, LIST_PAIR, LIST_VALUE -> new YamlList(lines, i, end, indent + 2);
            default -> {
                System.err.println("vvv  YAML - Error on line:  vvv");
                System.err.println(lines.get(i));
                System.err.println("^^^  ---------------------  ^^^");
                System.err.println("(Line " + i + " of input.)");
                throw new InvalidPropertiesFormatException("Syntax error on line " + i + ".");
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

    public abstract String toString(int indent);

}
