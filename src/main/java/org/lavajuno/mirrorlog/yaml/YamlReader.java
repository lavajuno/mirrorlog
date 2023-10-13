package org.lavajuno.mirrorlog.yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Vector;

/**
 * YamlReader parses YAML from a file into YamlElement(s).
 * Please consider that the parser is very small and only supports basic features of YAML.
 * Limitations:
 *   1- Lists must be in multiple-line form, and can only contain values.
 *     This will work:
 *       MyList:
 *       - "MyElement1"
 *       - MyElement2
 *     This will NOT work:
 *       MyList:
 *       - MyElement1:
 *         MyElement2: MyValue1
 *       - MyElement2: MyValue2
 *   2- Values must be a single line, spanning & folding will not work.
 *   However, escape characters will be preserved, so you can make newlines this way.
 *     This will work:
 *       MyElement1: MyValue, MyValue2
 *       MyElement2: "MyValue\nMyValue2"
 *     This will NOT work:
 *       MyElement1: |
 *         MyValue1
 *         MyValue2
 *   3- Comments must be on their own line.
 *     This will work:
 *       # This is what MyElement does.
 *       MyElement:
 *     This will NOT work:
 *       MyElement: # This is what MyElement does.
 */
@SuppressWarnings("unused")
public class YamlReader {

    /**
     * Parses YAML from a file into YamlElement(s).
     * It will always return a YamlElement with the key "root" that contains everything defined in the file.
     * @param file_path File path to read
     * @return YamlElement containing everything defined in the file
     * @throws IOException If reading from the file or constructing YAML elements fails
     */
    public static YamlElement readFromFile(String file_path) throws IOException {
        return new YamlElement(readLinesFromFile(file_path));
    }

    /**
     * Reads a vector of lines from a file
     * @param file_path File path to read
     * @return Lines in the file
     * @throws IOException If reading from the file fails
     */
    private static Vector<String> readLinesFromFile(String file_path) throws IOException {
        try {
            BufferedReader f = new BufferedReader(new FileReader(file_path));
            Vector<String> lines = new Vector<>();
            for(String line = f.readLine(); line != null; line = f.readLine()) {
                lines.add(line);
            }
            f.close();
            return lines;
        } catch(FileNotFoundException e) {
            System.err.println("File \"" + file_path + "\" could not be read. (Not Found)");
            throw new IOException("File \"" + file_path + "\" could not be read. (Not Found)");
        } catch(IOException e) {
            System.err.println("File \"" + file_path + "\" could not be read. (IOException)");
            throw(e);
        }
    }
}
