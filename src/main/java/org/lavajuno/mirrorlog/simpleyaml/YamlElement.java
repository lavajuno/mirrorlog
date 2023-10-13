package org.lavajuno.mirrorlog.simpleyaml;

import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

/**
 * YamlElement represents a single YAML element.
 * This can be either an element containing 0 or more children,
 * an element containing a value, or an element containing a list of values.
 * It is constructed with a vector of lines, and parses them by
 * recursively constructing new YamlElements.
 * YamlElement has two inheritors:
 *  - YamlList - A YamlElement that contains a list of values.
 *  - YamlValue - A YamlElement that contains a single value.
 * YamlList and YamlValue are the base cases for the recursive constructor.
 * They cannot have any children, and instead contain a String
 * (or a Vector of Strings).
 */
@SuppressWarnings("unused")
public class YamlElement {
    /**
     * Regex that matches lines to ignore (blank or commented)
     */
    static final String IGNORE_RGX = "^( *#.*)|( *)$";

    /**
     * Regex that matches lines containing an element or list head (ex. "ElementName: ")
     */
    static final String ELEMENT_RGX =  "^ *[A-Za-z0-9_-]{1,64}: *$";

    /**
     * Regex that matches lines containing a value (ex. "ElementName: ElementValue")
     */
    static final String STRING_RGX = "^ *[A-Za-z0-9_-]{1,64}: +\"?.{1,512}\"?$";

    /**
     * Regex that matches lines containing a list entry (ex. "  - ListItem1")
     */
    static final String LIST_ENTRY_RGX = "^ *- *.{1,512}$";

    /**
     * Possible matches for a line
     */
    enum LINE_MATCHES {
        IGNORE, ELEMENT, STRING, NONE
    }

    /**
     * This YAML element's key
     */
    public final String KEY;

    /**
     * This YAML element's children
     */
    public final Vector<YamlElement> ELEMENTS;

    /**
     * Constructs a YAMLElement.
     * @param key this YAMLElement's key
     * @param elements this YAMLElement's children
     */
    YamlElement(String key, Vector<YamlElement> elements) {
        this.KEY = key;
        this.ELEMENTS = elements;
    }

    /**
     * Constructs a YAMLElement.
     * @param lines Lines of YAML to parse
     * @param begin Index to start parsing at
     * @param end Index to stop parsing at
     * @param indent The indent of the block to parse
     * @throws InvalidPropertiesFormatException If YAML is invalid or the parser cannot continue
     */
    YamlElement(Vector<String> lines, int begin, int end, int indent) throws InvalidPropertiesFormatException {
        this(parseKey(lines.get(begin)), parseElements(lines, begin + 1, end, indent));
    }

    /**
     * Constructs a YAMLElement.
     * This is the only public constructor, and recursively constructs YAMLElements
     * with a vector of lines of YAML as input.
     * The outermost element will always be called "root", and will contain all elements
     * defined in the input YAML.
     * @param lines Lines of YAML to parse
     * @throws InvalidPropertiesFormatException If YAML is invalid or the parser cannot continue
     */
    public YamlElement(Vector<String> lines) throws InvalidPropertiesFormatException {
        this("root", parseElements(lines, 0, lines.size(), 0));
    }

    /**
     * Parses and constructs this YamlElement's children.
     * @param lines Lines of YAML to parse
     * @param begin Index to start parsing at
     * @param end Index to stop parsing at
     * @param indent Indent of the block to parse
     * @return Vector of YamlElements belonging to this YamlElement
     * @throws InvalidPropertiesFormatException If YAML is invalid or the parser cannot continue
     */
    private static Vector<YamlElement> parseElements(Vector<String> lines, int begin,
                                                     int end, int indent) throws InvalidPropertiesFormatException {
        Vector<YamlElement> my_elements = new Vector<>();
        String line;
        int line_indent;
        LINE_MATCHES line_match;

        /* Iterate over each line */
        for (int i = begin; i < end; i++) {
            line = lines.get(i);             /* Current line */
            line_indent = parseIndent(line); /* How indented this line is */
            line_match = matchLine(line);    /* This line's matched type */

            if(line_match != LINE_MATCHES.IGNORE) {
                /* Handle only lines in our indented block */
                if(line_indent == indent) {
                    switch(line_match) {
                        case ELEMENT:
                            if (i + 1 < end && lines.get(i + 1).matches(LIST_ENTRY_RGX)) {
                                /* If the line is the head of a list */
                                my_elements.add(new YamlList(lines, i, end));
                            } else {
                                /* If the line is an element with 0 or more children */
                                my_elements.add(new YamlElement(lines, i, end, indent + 2));
                            }
                            break;
                        case STRING:
                            /* If the line is an element with just a string */
                            my_elements.add(new YamlValue(line));
                            break;
                        case NONE:
                            /* If no match was found */
                            System.err.println("vv  Syntax error on line:  vv");
                            System.err.println(line);
                            System.err.println("^^  ---------------------  ^^");
                            throw new InvalidPropertiesFormatException("Syntax error on line " + i + ".");
                    }
                } else if(line_indent < indent) {
                    /* Stop reading once we reach the end of our indented block. */
                    break;
                }
                /* If the line's indent is greater, just skip reading it. */
            }
        }
        return my_elements;
    }

    /**
     * Parses a line to return the key it contains.
     * We assume that we have checked the line to make sure it actually contains
     * a key, otherwise this function may behave unexpectedly.
     * @param line The line to parse
     * @return The key contained in this line
     */
    static String parseKey(String line) { return line.stripLeading().split(":", 2)[0]; }

    /**
     * Parses a line to return the number of spaces it is indented by.
     * @param line The line to parse
     * @return How many spaces this line is indented by
     */
    static int parseIndent(String line) {
        return line.length() - line.stripLeading().length();
    }

    /**
     * Matches a line to a category
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("YamlElement - \"").append(this.KEY).append("\": {\n");
        for(YamlElement i : this.ELEMENTS) {
            sb.append(i.toString());
        }
        sb.append("} /\"").append(KEY).append("\"\n");
        return sb.toString();
    }
}
