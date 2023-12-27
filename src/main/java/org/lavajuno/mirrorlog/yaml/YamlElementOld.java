package org.lavajuno.mirrorlog.yaml;

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
public class YamlElementOld {
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
    private enum LINE_MATCHES {
        IGNORE, ELEMENT, PAIR, LIST_ELEMENT, LIST_PAIR, LIST_VALUE, NONE
    }

    /**
     * This YamlElement's key
     */
    protected final String KEY;

    /**
     * This YamlElement's contained YamlElements
     */
    protected final TreeMap<String, YamlElementOld> ELEMENTS;

    /**
     * Constructs a YamlElement by recursively parsing lines of YAML into a structure of YamlElements.
     * The outermost element will always be called "root", and will contain all elements defined in the input YAML.
     * @param lines Lines of YAML to parse
     * @throws InvalidPropertiesFormatException If YAML is invalid or the parser cannot continue
     */
    public YamlElementOld(List<String> lines) throws InvalidPropertiesFormatException {
        this("root", lines, 0, lines.size(), 0);
    }

    /**
     * Constructs a YamlElement with the given key and no children.
     * Used by base-case inheritors of YamlElement.
     * @param key this YamlElement's key
     */
    YamlElementOld(String key) {
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
    YamlElementOld(String key, List<String> lines, int begin, int end, int indent)
            throws InvalidPropertiesFormatException {
        this.KEY = key;
        this.ELEMENTS = new TreeMap<>();
        int list_index = 0;
        for(int i = begin; i < end; i++) {
            String line = lines.get(i);                /* Current line */
            int line_indent = parseIndent(line);       /* How indented this line is */
            LINE_MATCHES line_match = matchLine(line); /* This line's matched type */
            /* If this line's indent matches that of our indented block */
            if(line_indent == indent) {
                /* If there is a syntax error in this line, output will be ignored. */
                String curr_key = parseKey(line);
                switch(line_match) {
                    case ELEMENT:
                        /* Unordered map of elements */
                        ELEMENTS.put(curr_key, new YamlElementOld(curr_key, lines, i + 1, end, indent + 2));
                        break;
                    case PAIR:
                        /* Key-value pair */
                        ELEMENTS.put(curr_key, new YamlValueOld(curr_key, line, false));
                        break;
                    case LIST_ELEMENT:
                        /* Ordered list of elements */
                        curr_key = Integer.toString(list_index);
                        ELEMENTS.put(curr_key, new YamlElementOld(curr_key, lines, i + 1, end, indent + 4));
                        list_index++;
                        break;
                    case LIST_PAIR:
                        break;
                    case LIST_VALUE:
                        curr_key = Integer.toString(list_index);
                        ELEMENTS.put(curr_key, new YamlValueOld(curr_key, line, true));

                    case NONE:
                        /* If no match was found, we don't know what to do with the line */
                        System.err.println("vv  YamlElement: Syntax error on line:  vv");
                        System.err.println(line);
                        System.err.println("^^  ----------------------------------  ^^");
                        System.err.println("(Line " + i + " of input.)");
                        throw new InvalidPropertiesFormatException("Syntax error on line " + i + " of input.");
                }
            }
            /* If the indent of this line is less than that of our indented block */
            else if(line_indent < indent && line_match != LINE_MATCHES.IGNORE) {
                /* Stop reading once we reach the end of our indented block */
                break;
            }
            /* If the indent is greater, skip the line */
            /* (At this point, we cannot be sure that our block has ended) */
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
    public YamlElementOld getElement(String key) { return ELEMENTS.get(key); }

    /**
     * Returns this YamlElement's contained elements.
     * @return This YamlElement's contained elements.
     */
    public Collection<YamlElementOld> getElements() { return ELEMENTS.values(); }

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
     * Alternate toString that preserves indents to output valid YAML.
     * @param indent Indent of this YamlElement
     * @return This YamlElement as a String
     */
    String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        sb.append(indent_prefix).append(this.KEY).append(":\n");
        for(YamlElementOld i : this.ELEMENTS.values()) {
            sb.append(i.toString(indent + 2));
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
