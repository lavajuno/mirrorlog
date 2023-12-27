package org.lavajuno.mirrorlog.yaml2;

import java.util.*;

public class YamlMap extends YamlElement {

    protected final TreeMap<String, YamlElement> ELEMENTS;

    protected YamlMap(List<String> lines, int begin, Integer end, int indent) throws InvalidPropertiesFormatException {
        this.ELEMENTS = new TreeMap<>();
        for(int i = begin; i < lines.size(); i++) {
            String line = lines.get(i);
            int line_indent = parseIndent(line);
            LINE_MATCHES line_match = matchLine(line);

            if(line_indent == indent) { /* If indent matches */
                System.out.println("Matched indent on Map.");
                String key;
                switch(line_match) {
                    case ELEMENT:
                        key = parseKey(line);
                        System.out.println("Parsing map element with key \"" + key + "\".");
                        System.out.println(parseElement(lines, i + 1, end, indent + 2).getClass());
                        ELEMENTS.put(key, parseElement(lines, i + 1, end, indent + 2));
                        break;
                    case PAIR:
                        key = parseKey(line);
                        System.out.println("Parsing map pair with key \"" + key + "\".");
                        ELEMENTS.put(key, new YamlValue(line.split(":", 2)[1].stripLeading()));
                        break;
                    case IGNORE:
                        break;
                    default:
                        /* If no match was found, we don't know what to do with the line */
                        System.err.println("vvv  YAML - Error on line:  vvv");
                        System.err.println(line);
                        System.err.println("^^^  ---------------------  ^^^");
                        System.err.println("(Line " + i + " of input.)");
                        throw new InvalidPropertiesFormatException("Error on line " + i + " of input.");
                }
            } else if(line_indent < indent && line_match != LINE_MATCHES.IGNORE) {
                break; /* End of indented block */
            }
            /* If the indent is greater, skip the line. */
        }
    }

    public YamlMap(List<String> lines) throws InvalidPropertiesFormatException {
        this(lines, 0, 0, 0);
    }

    public Map<String, YamlElement> getElements() { return ELEMENTS; }

    public Set<String> getKeys() { return ELEMENTS.keySet(); }

    public YamlElement getElement(String key) { return ELEMENTS.get(key); }


    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        sb.append("\n");
        for(String i : ELEMENTS.descendingKeySet()) {
            sb.append(indent_prefix).append(i).append(": ");
            sb.append(ELEMENTS.get(i).toString(indent + 2)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
