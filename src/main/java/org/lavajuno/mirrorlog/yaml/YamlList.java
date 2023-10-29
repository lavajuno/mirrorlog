package org.lavajuno.mirrorlog.yaml;

import java.util.List;
import java.util.Vector;

/**
 * YamlList is a YamlElement that contains a list of Strings.
 * A YamlList never has children (YamlList.getElements() will always be null).
 */
@SuppressWarnings("unused")
public class YamlList extends YamlElement {
    /**
     * The contents of this YamlList
     */
    private final Vector<String> CONTENTS;

    /**
     * Constructs a YamlList.
     * @param lines Lines of YAML to parse
     * @param begin Index to start parsing at
     * @param end Index to stop parsing at
     */
    YamlList(String key, List<String> lines, int begin, int end) {
        /* Construct superclass with only key */
        super(key);
        /* Parse lines and populate list items */
        CONTENTS = new Vector<>();
        String line;
        String value;
        /* Iterate over each line */
        for(int i = begin; i < end; i++) {
            line = lines.get(i);
            /* If the line is a valid list entry, record it */
            if(line.matches(YamlElement.LIST_ENTRY_RGX)) {
                value = line.split("- +", 2)[1];
                String head = value.substring(0, 1);
                String tail = value.substring(value.length() - 1);
                /* Trim quotes if they exist */
                if((head.equals("\"") && tail.equals("\"")) ||
                        (head.equals("'") && tail.equals("'"))) {
                    CONTENTS.add(value.substring(1, value.length() - 1));
                } else {
                    CONTENTS.add(value);
                }
            }
            /* If the line is NOT a valid list entry */
            else {
                /* If the line is not a list entry or blank/comment,
                    then we have reached the end of the list. */
                if(!line.matches(IGNORE_RGX)) { break; }
                /* Skip blank/comment lines */
            }
        }
    }

    /**
     * This YamlList's contents.
     * @return This YamlList's contents.
     */
    public List<String> getContents() { return CONTENTS; }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        String indent_prefix = " ".repeat(indent);
        sb.append(indent_prefix).append(this.KEY).append(":").append("\n");
        for(String i : this.CONTENTS) {
            sb.append(indent_prefix).append("- ").append(i).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() { return this.toString(0); }
}
