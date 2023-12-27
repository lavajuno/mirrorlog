package org.lavajuno.mirrorlog.yaml2;

import java.util.InvalidPropertiesFormatException;
import java.util.Vector;
import java.util.List;

/**
 * Represents an ordered list of elements.
 */
public class YamlList extends YamlElement {

    protected final Vector<YamlElement> ELEMENTS;

    protected YamlList(List<String> lines, int begin, Integer end, int indent) throws InvalidPropertiesFormatException {
        this.ELEMENTS = new Vector<>();
        for(int i = begin; i < lines.size(); i++) {
            String line = lines.get(i);
            int line_indent = parseIndent(line);
            LINE_MATCHES line_match = matchLine(line);

            if(line_indent == indent) { /* If indent matches */
                switch(line_match) {
                    case LIST_ELEMENT:
                        System.out.println("Parsing list element.");
                        ELEMENTS.add(parseElement(lines, i + 1, end, indent + 2));
                        break;
                    case LIST_PAIR:
                        System.out.println("Parsing list pair.");
                        ELEMENTS.add(new YamlPair(line.split("-", 2)[0].stripLeading()));
                        break;
                    case LIST_VALUE:
                        System.out.println("Parsing list value.");
                        ELEMENTS.add(new YamlValue(line.split("-", 2)[0].stripLeading()));
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
            }
        }
    }

    public List<YamlElement> getElements() { return ELEMENTS; }

    public YamlElement getElement(int index) { return ELEMENTS.get(index); }

    public int getSize() { return ELEMENTS.size(); }

    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        for(YamlElement i : this.ELEMENTS) {
            sb.append(i.toString(indent + 2));
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
