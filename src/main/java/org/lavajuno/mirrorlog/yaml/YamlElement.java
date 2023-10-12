package org.lavajuno.mirrorlog.yaml;

import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

@SuppressWarnings("unused")
public class YamlElement {
    static final String IGNORE_RGX = "^(#.*)|( *)$";
    static final String ELEMENT_RGX =  "^ *[A-Za-z0-9_-]{1,64}: *$";
    static final String STRING_RGX = "^ *[A-Za-z0-9_-]{1,64}: +\"?.{1,512}\"?$";
    static final String LIST_RGX = "^ *[A-Za-z0-9_-]{1,64}: +\\[.{1,512}\\]$";
    static final String LIST_ENTRY_RGX = "^ *- *.{1,512}$";

    enum LINE_MATCHES {
        IGNORE, ELEMENT, STRING, LIST, NONE
    }

    String key;
    Vector<YamlElement> elements;

    public YamlElement(String key, Vector<YamlElement> elements) {
        this.key = key;
        this.elements = elements;
    }

    public YamlElement(Vector<String> lines, int begin, int end, int indent) throws InvalidPropertiesFormatException {
        this.key = parseKey(lines.get(begin));
        this.elements = parseElements(lines, begin + 1, end, indent + 2);
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
        Vector<YamlElement> new_elements = new Vector<YamlElement>();
        String line;
        int line_indent;
        LINE_MATCHES line_match;
        for (int i = begin; i < end; i++) {
            line = lines.get(i);
            line_indent = parseIndent(line);
            line_match = matchLine(line);
            System.out.println("Analyzing line \"" + line + "\".");

            // ignore comment or blank lines
            if(line_match != LINE_MATCHES.IGNORE) {
                // if indent is consistent
                if(line_indent == indent) {
                    switch(matchLine(line)) {
                        case ELEMENT: // element with 0 or more children
                            System.out.println("Found regular element.");
                            new_elements.add(new YamlElement(lines, i, end, indent + 2));
                            break;
                        case STRING: // element with a string
                            System.out.println("Found string element.");
                            new_elements.add(new YamlString(line));
                            break;
                        case LIST: // element with either a single or multi line list
                            if (lines.get(i + 1).matches(LIST_ENTRY_RGX)) {
                                System.out.println("Found multi-line list element.");
                                new_elements.add(new YamlList(lines, i, end));
                            } else {
                                System.out.println("Found single-line list element.");
                                new_elements.add(new YamlList(line));
                            }
                            break;
                        case NONE: // no match
                            System.err.println("vv  Invalid line  vv");
                            System.err.println(line);
                            System.err.println("^^  Invalid line  ^^");
                            throw new InvalidPropertiesFormatException("Bad line (" + i + ").");
                    }
                } else if(line_indent < indent) {
                    break;
                }
                // just skip this line if it's a higher indent
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
        } else if(line.matches(LIST_RGX)) {
            return LINE_MATCHES.LIST;
        } else {
            return LINE_MATCHES.NONE;
        }
    }

    @Override
    public String toString() {
        return "YamlElement: Key-\"" + this.key + "\" Elements{\n" + this.elements.toString() + "\n}";
    }
}
