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

    public YamlElement(Vector<String> lines, int begin, int end) throws InvalidPropertiesFormatException {
        this.key = "";
        this.elements = new Vector<YamlElement>();
        String line;
        for(int i = begin; i < end; i++) {
            line = lines.get(i);
            System.out.println("Analyzing line \"" + line + "\".");
            if(line.matches(ELEM_MULTILINE_RGX)) {
                if(lines.get(i+1).matches(ELEM_ENTRY_RGX)) {
                    this.key = parseKey(line);
                    this.elements.add(new YamlElement(lines, i, end));
                } else {
                    System.out.println("Found multiline element.");
                    this.key = parseKey(line);
                    this.element = new YamlElement(lines, i + 1, end);
                }
                return;
            } else if(line.matches(ELEM_BASE_RGX)) {
                if(line.matches(ELEM_STR_RGX)) {
                    System.out.println("Found string element.");
                    this.key = parseKey(line);
                    this.element = new YamlString(line);
                    return;
                } else if(line.matches(ELEM_LIST_RGX)) {
                    System.out.println("Found list element.");
                    this.key = parseKey(line);
                    this.element = new YamlList(line);
                    return;
                } else {
                    throw new InvalidPropertiesFormatException("Bad line!");
                }
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
        this(lines, 0, lines.size());
    }

    public String key() {
        return this.key;
    }

    public YamlElement element() {
        return this.element;
    }

    static String parseKey(String s) { return s.stripLeading().split(":")[0]; }

    @Override
    public String toString() {
        return "YamlElement: Key-\"" + this.key + "\" Element{\n" + this.element.toString() + "\n}";
    }
}
