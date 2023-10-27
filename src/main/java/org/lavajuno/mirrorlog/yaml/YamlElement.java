package org.lavajuno.mirrorlog.yaml;

import java.io.IOException;
import java.util.*;

/**
 * YamlElement represents a single YAML element.
 * This can be either an element containing 0 or more YamlElements,
 * an element containing a string, or an element containing a list of strings.
 * It is constructed with a list of lines, and parses them by
 * recursively constructing new YamlElements.
 * YamlElement has two inheritors:
 *  - YamlList - A YamlElement that contains a list of values.
 *  - YamlValue - A YamlElement that contains a single value.
 * YamlList and YamlValue are the base cases for the recursive constructor.
 * They cannot have any children, and instead contain a String (or a Vector of Strings).
 * A YamlElement's children are stored in a red-black tree, so it is quick
 * to access individual elements. You can also access all elements as a Collection.
 */
@SuppressWarnings("unused")
public class YamlElement {
    /**
     * Matches lines to ignore (blank or commented)
     */
    static final String IGNORE_RGX = "^( *#.*)|( *)$";

    /**
     * Matches lines containing an element or list head (ex. "ElementName: ")
     */
    static final String ELEMENT_RGX =  "^ *[A-Za-z0-9_-]+: *$";

    /**
     * Matches lines containing a value (ex. "ElementName: ElementValue")
     */
    static final String STRING_RGX = "^ *[A-Za-z0-9_-]+: +\"?.+\"?$";

    /**
     * Matches lines containing a list entry (ex. "  - ListItem1")
     */
    static final String LIST_ENTRY_RGX = "^ *- +.+$";

    /**
     * Possible matches for a line
     */
    private enum LINE_MATCHES {
        IGNORE, ELEMENT, STRING, NONE
    }

    /**
     * This YamlElement's key
     */
    private final String KEY;

    /**
     * This YamlElement's contained YamlElements
     */
    private final TreeMap<String, YamlElement> ELEMENTS;

    /**
     * Constructs a YamlElement by recursively parsing lines of YAML into a structure of YamlElements.
     * The outermost element will always be called "root", and will contain all elements defined in the input YAML.
     * @param lines Lines of YAML to parse
     * @throws InvalidPropertiesFormatException If YAML is invalid or the parser cannot continue
     */
    public YamlElement(List<String> lines) throws InvalidPropertiesFormatException {
        this("root", lines, 0, lines.size(), 0);
    }

    /**
     * Constructs a YamlElement with the given key and no children.
     * Used by base-case inheritors of YamlElement.
     * @param key this YamlElement's key
     */
    YamlElement(String key) {
        this.KEY = key;
        this.ELEMENTS = null;
    }

    /**
     * Constructs a YamlElement.
     * @param key The key (name) of this YamlElement
     * @param lines Lines of YAML that we are parsing
     * @param begin Index to start parsing this element at
     * @param end Index to stop parsing this element at
     * @param indent The indent of the block to parse
     * @throws InvalidPropertiesFormatException If YAML is invalid or the parser cannot continue
     */
    YamlElement(String key, List<String> lines, int begin, int end, int indent)
            throws InvalidPropertiesFormatException {
        /* TODO Switch and if/else structure should be improved, right now it is not very readable */
        this.KEY = key;
        this.ELEMENTS = new TreeMap<>();
        /* Iterate over each line in the range we are parsing */
        for (int i = begin + 1; i < end; i++) {
            String line = lines.get(i);                /* Current line */
            int line_indent = parseIndent(line);       /* How indented this line is */
            LINE_MATCHES line_match = matchLine(line); /* This line's matched type */
            /* If this line is something we need to parse */
            if(line_match != LINE_MATCHES.IGNORE) {
                /* If this line matches our target indent */
                if(line_indent == indent) {
                    /* If there is a syntax error in this line, parseKey output will be ignored. */
                    String k = parseKey(line);
                    switch(line_match) {
                        case ELEMENT:
                            /* If the line is the head of a list */
                            if (i + 1 < end && lines.get(i + 1).matches(LIST_ENTRY_RGX)) {
                                ELEMENTS.put(k, new YamlList(k, lines, i, end));
                            }
                            /* If the line is an element with 0 or more children */
                            else {
                                ELEMENTS.put(k, new YamlElement(k, lines, i, end, indent + 2));
                            }
                            break;
                        case STRING:
                            /* If the line is an element with just a string */
                            ELEMENTS.put(k, new YamlValue(k, line));
                            break;
                        case NONE:
                            /* If no match was found, we don't know what to do with the line */
                            System.err.println("vv  YamlElement: Syntax error on line:  vv");
                            System.err.println(line);
                            System.err.println("^^  ----------------------------------  ^^");
                            System.err.println("(Line " + i + " of input.)");
                            throw new InvalidPropertiesFormatException("Syntax error on line " + i + " of input.");
                    }
                } else if(line_indent < indent) {
                    /* Stop reading once we reach the end of our indented block */
                    break;
                }
                /* If the line's indent is greater, just skip reading it */
                /* (not sure if we're done yet at this point) */
            }
        }
    }

    /**
     * Returns this YamlElement's key
     * @return this YamlElement's key
     */
    public String getKey() { return this.KEY; }

    /**
     * Returns the element with the given key in this YamlElement's contained elements.
     * @param key The key to match
     * @return The element with the given key (or null if none is found)
     */
    public YamlElement getElement(String key) { return ELEMENTS.get(key); }

    /**
     * Returns this YamlElement's contained elements.
     * @return This YamlElement's contained elements.
     */
    public Collection<YamlElement> getElements() { return ELEMENTS.values(); }

    /**
     * Parses a line to return the key it contains.
     * If the line does not contain a key, this function may behave unexpectedly.
     * @param line The line to parse
     * @return The key contained in this line
     */
    static String parseKey(String line) { return line.stripLeading().split(":", 2)[0]; }

    /**
     * Parses a line to return the number of spaces it is indented by.
     * Probably not the best way to do this, but not a huge issue for now.
     * @param line The line to parse
     * @return How many spaces this line is indented by
     */
    static int parseIndent(String line) { return line.length() - line.stripLeading().length(); }

    /**
     * Matches a line to a predefined category
     * @param line Line to match
     * @return Category this line is matched to (can be NONE)
     */
    static LINE_MATCHES matchLine(String line) {
        if(line.matches(IGNORE_RGX)) {
            return LINE_MATCHES.IGNORE;
        } else if(line.matches(ELEMENT_RGX)) {
            return LINE_MATCHES.ELEMENT;
        } else if(line.matches(STRING_RGX)) {
            return LINE_MATCHES.STRING;
        } else {
            return LINE_MATCHES.NONE;
        }
    }

    /**
     * Alternate toString that preserves indents to output valid YAML.
     * @param indent Indent of this YamlElement
     * @return This YamlElement as a String
     */
    String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        sb.append(indent_prefix).append(this.KEY).append(":\n");
        for(YamlElement i : this.ELEMENTS.values()) {
            sb.append(i.toString(indent + 2));
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
