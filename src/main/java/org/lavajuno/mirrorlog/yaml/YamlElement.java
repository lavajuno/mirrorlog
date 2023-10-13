package org.lavajuno.mirrorlog.yaml;

import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

public class YamlElement {
    static final String IGNORE_RGX = "^(#.*)|( *)$";
    static final String ELEMENT_RGX =  "^ *[A-Za-z0-9_-]{1,64}: *$";
    static final String STRING_RGX = "^ *[A-Za-z0-9_-]{1,64}: +\"?.{1,512}\"?$";
    static final String LIST_ENTRY_RGX = "^ *- *.{1,512}$";

    enum LINE_MATCHES {
        IGNORE, ELEMENT, STRING, NONE
    }

    String key;
    Vector<YamlElement> elements;

    public YamlElement(String key, Vector<YamlElement> elements) {
        this.key = key;
        this.elements = elements;
    }

    public YamlElement(Vector<String> lines, int begin, int end, int indent) throws InvalidPropertiesFormatException {
        this.key = parseKey(lines.get(begin));
        this.elements = parseElements(lines, begin + 1, end, indent);
    }

    public YamlElement(Vector<String> lines) throws InvalidPropertiesFormatException {
        this.key = "root";
        this.elements = parseElements(lines, 0, lines.size(), 0);
    }

    public String key() {
        return this.key;
    }

    public Vector<YamlElement> elements() {
        return this.elements;
    }

    static String parseKey(String s) { return s.stripLeading().split(":")[0]; }

    static int parseIndent(String s) {
        return s.length() - s.stripLeading().length();
    }

    static Vector<YamlElement> parseElements(Vector<String> lines,
                                             int begin, int end, int indent) throws InvalidPropertiesFormatException {
        Vector<YamlElement> new_elements = new Vector<>();
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
                            if (lines.get(i + 1).matches(LIST_ENTRY_RGX)) {
                                /* If the line is the head of a list */
                                new_elements.add(new YamlList(lines, i, end));
                            } else {
                                /* If the line is an element with 0 or more children */
                                new_elements.add(new YamlElement(lines, i, end, indent + 2));
                            }
                            break;
                        case STRING:
                            /* If the line is an element with just a string */
                            new_elements.add(new YamlString(line));
                            break;
                        case NONE:
                            /* If no match was found */
                            System.err.println("vv  Invalid YAML:  vv");
                            System.err.println(line);
                            System.err.println("^^  -------------  ^^");
                            throw new InvalidPropertiesFormatException("Invalid YAML. (Line " + i + ").");
                    }
                } else if(line_indent < indent) {
                    /* Stop reading once we reach the end of our indented block. */
                    break;
                }
                /* If the line's indent is greater, just skip reading it. */
            }
        }
        return new_elements;
    }

    private static LINE_MATCHES matchLine(String line) {
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
        sb.append("YamlElement -- Key: \"").append(this.key).append("\", Elements: \n{\n");
        for(YamlElement i : this.elements) {
            sb.append(i.toString());
        }
        sb.append("}\n");
        return sb.toString();
    }
}
