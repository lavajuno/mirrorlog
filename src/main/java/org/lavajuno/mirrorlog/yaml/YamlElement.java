package org.lavajuno.mirrorlog.yaml;

import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

@SuppressWarnings("unused")
public class YamlElement {
    static final String WHITESPACE_REGEX = "^ *$";
    static final String COMMENT_REGEX = "^#.*$";
    static final String ELEM_MULTILINE_RGX =  "^ *[A-Za-z0-9_-]{1,64}: *$";
    static final String ELEM_STR_RGX = "^ *[A-Za-z0-9_-]{1,64}: +\".{1,512}\"$";
    static final String ELEM_LIST_RGX = "^ *[A-Za-z0-9_-]{1,64}: +\\[.{1,512}\\]$";
    static final String ELEM_ENTRY_RGX = "^ *- *.{1,512}$";
    static final String ELEM_BASE_RGX = "^ *[A-Za-z0-9_-]{1,64}: +[\\[\"].{1,512}[\\]\"]$";

    String key;
    Vector<YamlElement> elements;

    public YamlElement(String key, Vector<YamlElement> elements) {
        this.key = key;
        this.elements = elements;
    }

    public YamlElement(Vector<String> lines, int begin, int end, int indent) throws InvalidPropertiesFormatException {
        this.key = "";
        this.elements = new Vector<YamlElement>();
        String line;
        for(int i = begin; i < end; i++) {
            line = lines.get(i);
            System.out.println("Analyzing line \"" + line + "\".");
            if(line.matches(ELEM_MULTILINE_RGX)) {
                if(lines.get(i+1).matches(ELEM_ENTRY_RGX)) {
                    System.out.println("Found multiline list head.");
                    this.elements.add(new YamlList(lines, i, end));
                } else {
                    System.out.println("Found regular element.");
                    this.key = parseKey(line);
                    this.elements = parseElements(lines, i + 1, end, indent + 2);
                }
                return;
            } else {
                if(!line.matches(WHITESPACE_REGEX) && !line.matches(COMMENT_REGEX)) {
                    throw new InvalidPropertiesFormatException("Bad line (" + i + ").");
                }
            }
        }
        if(this.key == null) {
            throw new InvalidPropertiesFormatException("a");
        }
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
        for (int i = begin; i < end; i++) {
            line = lines.get(i);
            System.out.println("Analyzing line \"" + line + "\".");
            if (line.matches(ELEM_MULTILINE_RGX)) {
                if (lines.get(i + 1).matches(ELEM_ENTRY_RGX)) {
                    if (parseIndent(line) == indent) {
                        System.out.println("Found multi-line list element.");
                        new_elements.add(new YamlList(lines, i, end));
                    } else {
                        break;
                    }
                } else {
                    if (parseIndent(line) == indent) {
                        System.out.println("Found regular element.");
                        new_elements.add(new YamlElement(lines, i, end, indent + 2));
                    }
                    break;
                }
            } else if (line.matches(ELEM_BASE_RGX)) {
                if (line.matches(ELEM_STR_RGX)) {
                    System.out.println("Found string element.");
                    new_elements.add(new YamlString(line));
                } else if (line.matches(ELEM_LIST_RGX)) {
                    System.out.println("Found single-line list element.");
                    new_elements.add(new YamlList(line));
                } else {
                    throw new InvalidPropertiesFormatException("Bad line!");
                }
            } else {
                if (!line.matches(WHITESPACE_REGEX) && !line.matches(COMMENT_REGEX)) {
                    throw new InvalidPropertiesFormatException("Bad line (" + i + ").");
                }
            }
        }
        return new_elements;
    }

    @Override
    public String toString() {
        return "YamlElement: Key-\"" + this.key + "\" Element{\n" + this.element.toString() + "\n}";
    }
}
