package src.test;

import org.junit.jupiter.api.Test;
import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.yaml.YamlElement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
import java.util.Vector;

public class TestConfig {
    @Test
    public void testYamlRead() throws IOException {
        YamlElement root = new YamlElement(readLinesFromFile("test.yml"));
        System.out.println(root);
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
