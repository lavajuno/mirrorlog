package org.lavajuno.mirrorlog.yaml;


import java.util.Vector;

/**
 * YamlList is a YamlElement that contains a list of Strings.
 * A YamlList never has children (YamlList.ELEMENTS will always be null).
 */
//@SuppressWarnings("unused")
public class YamlList extends YamlElement {
    public final Vector<String> CONTENTS;

    /**
     * Constructs a YamlList.
     * @param lines Lines of YAML to parse
     * @param begin Index to start parsing at
     * @param end Index to stop parsing at
     */
    YamlList(Vector<String> lines, int begin, int end) {
        super(YamlElement.parseKey(lines.get(begin)), null);
        this.CONTENTS = parseList(lines, begin + 1, end);
    }

    /**
     * Parses this YamlList's items
     * @param lines Lines of YAML to parse
     * @param begin Index to start parsing at
     * @param end Index to stop parsing at
     * @return This YamlList's items
     */
    private static Vector<String> parseList(Vector<String> lines, int begin, int end) {
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

    @Override
    public String toString() {
        return "YamlList -- Key: \"" + this.KEY + "\", Contents: \"" + this.CONTENTS.toString() + "\"\n";
    }
}
