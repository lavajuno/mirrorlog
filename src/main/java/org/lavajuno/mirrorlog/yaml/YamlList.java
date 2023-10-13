package org.lavajuno.mirrorlog.yaml;


import java.util.Vector;

//@SuppressWarnings("unused")
public class YamlList extends YamlElement {
    Vector<String> contents;

    public YamlList(Vector<String> lines, int begin, int end) {
        super(YamlElement.parseKey(lines.get(begin)), null);
        this.contents = parseList(lines, begin + 1, end);
    }

    static Vector<String> parseList(Vector<String> lines, int begin, int end) {
        Vector<String> values = new Vector<>();
        String line;
        String value;
        /* Iterate over each line */
        for(int i = begin; i < end; i++) {
            line = lines.get(i); /* The current line */
            /* If the line is a valid list entry, record it */
            if(line.matches(YamlElement.LIST_ENTRY_RGX)) {
                value = line.split("- ")[1];
                String head = value.substring(0, 1);
                /* Trim quotes if they exist */
                if(head.equals("\"") || head.equals("'")) {
                    values.add(value.substring(1, value.length() - 1));
                } else {
                    values.add(value);
                }
            } else {
                /* Stop reading and return once we reach the end of the list */
                break;
            }
        }
        return values;
    }

    public Vector<String> contents() {
        return this.contents;
    }

    @Override
    public String toString() {
        return "YamlList -- Key: \"" + this.key + "\", Contents: \"" + this.contents.toString() + "\"\n";
    }
}
