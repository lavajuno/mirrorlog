package org.lavajuno.mirrorlog.yaml;

import java.util.Vector;

/**
 * YamlList is a YamlElement that contains a list of Strings.
 * A YamlList never has children (YamlList.ELEMENTS will always be null).
 */
//@SuppressWarnings("unused")
public class YamlList extends YamlElement {
    /**
     * The contents of this YamlList
     */
    public final Vector<String> CONTENTS;

    /**
     * Constructs a YamlList.
     * @param lines Lines of YAML to parse
     * @param begin Index to start parsing at
     * @param end Index to stop parsing at
     */
    YamlList(Vector<String> lines, int begin, int end) {
        super(YamlElement.parseKey(lines.get(begin)), null);
        /* Parse lines and populate list items */
        CONTENTS = new Vector<>();
        String line;
        String value;
        /* Iterate over each line */
        for(int i = begin + 1; i < end; i++) {
            line = lines.get(i);
            /* If the line is a valid list entry, record it */
            if(line.matches(YamlElement.LIST_ENTRY_RGX)) {
                value = line.split("- ", 2)[1];
                String head = value.substring(0, 1);
                /* Trim quotes if they exist */
                if(head.equals("\"") || head.equals("'")) {
                    CONTENTS.add(value.substring(1, value.length() - 1));
                } else {
                    CONTENTS.add(value);
                }
            } else { /* If the line is NOT a valid list entry */
                if(!line.matches(IGNORE_RGX)) {
                    /* If the line is not a list entry or blank/comment,
                       then we have reached the end of the list. */
                    break;
                }
                /* Skip blank/comment lines */
            }
        }
    }

    @Override
    public String toString() {
        return "YamlList - Key: \"" + this.KEY + "\", Contents: \"" + this.CONTENTS.toString() + "\"\n";
    }
}
