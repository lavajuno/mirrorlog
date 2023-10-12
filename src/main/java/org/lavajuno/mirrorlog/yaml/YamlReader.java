package org.lavajuno.mirrorlog.yaml;

import java.io.*;
import java.util.Vector;

@SuppressWarnings("unused")
public class YamlReader {

    //private YamlElement parseLines(Vector<String> lines) {
    //}

    private Vector<String> readLinesFromFile(String file_path) throws IOException {
        try {
            BufferedReader f = new BufferedReader(new FileReader(file_path));
            Vector<String> lines = new Vector<>();
            for(String line = f.readLine(); line != null; line = f.readLine()) {
                lines.add(line);
            }
            f.close();
            return lines;
        } catch(FileNotFoundException e) {
            System.err.println();
            throw new IOException("Configuration file \"" + file_path + "\" not found!");
        }
    }
}
