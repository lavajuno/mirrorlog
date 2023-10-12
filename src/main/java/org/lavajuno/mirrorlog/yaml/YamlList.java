package org.lavajuno.mirrorlog.yaml;


import java.util.Vector;

//@SuppressWarnings("unused")
public class YamlList extends YamlElement {
    Vector<String> contents;

    public YamlList(String key, Vector<String> contents) {
        super(key, null);
        this.contents = contents;
    }

    public YamlList(String line) {
        super(YamlElement.parseKey(line), null);
        this.contents = parseList(line);
    }

    public YamlList(Vector<String> lines, int begin, int end) {
        super(YamlElement.parseKey(lines.get(begin)), null);
        this.contents = parseList(lines, begin + 1);
    }

    static Vector<String> parseList(Vector<String> lines, int begin) {
        System.out.println("Creating a list.");
        Vector<String> values = new Vector<>();
        String line;
        for(String i : lines) {
            if(i.matches(YamlElement.LIST_ENTRY_RGX)) {
                line = i.split("- ")[1];
                String head = line.substring(0, 1);
                if(head.equals("\"") || head.equals("'")) {
                    values.add(line.substring(1, line.length() - 1));
                } else {
                    values.add(line);
                }
            } else {
                break;
            }
        }
        return values;
    }

    static Vector<String> parseList(String line) {
        return new Vector<String>();
    }

    public Vector<String> contents() {
        return this.contents();
    }

    @Override
    public String toString() {
        return "YamlList: Key-\"" + this.key + "\" Contents-\"" + this.contents.toString() + "\".\n";
    }
}
