/**
 * LucidJSON v0.0.1 - Experimental
 */

package org.lavajuno.lucidjson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Provides functionality for serializing/deserializing JSON to/from Strings and files.
 */
@SuppressWarnings("unused")
public class Json {
    /**
     * Deserializes JSON object from a String.
     * @param text Input string
     * @return Deserialized JSON object
     * @throws ParseException If parsing fails
     */
    public static JsonObject read(String text) throws ParseException {
        return new JsonObject(text.replace("\n", ""));
    }

    /**
     * Deserializes a JSON object from a list of lines (Strings).
     * @param lines Input lines
     * @return Deserialized JSON object
     * @throws ParseException If parsing fails
     */
    public static JsonObject read(List<String> lines) throws ParseException {
        StringBuilder sb = new StringBuilder();
        for(String i : lines) { sb.append(i); }
        return read(sb.toString());
    }

    /**
     * Deserializes a JSON object from a file.
     * @param file_path Path to the input file
     * @return Deserialized JSON object
     * @throws IOException If reading the file fails
     * @throws ParseException If parsing fails
     */
    public static JsonObject readFile(String file_path) throws IOException, ParseException {
        Scanner file = new Scanner(new FileInputStream(file_path));
        StringBuilder lines = new StringBuilder();
        while(file.hasNextLine()) { lines.append(file.nextLine()); }
        file.close();
        return read(lines.toString());
    }

    /**
     * Serializes a JSON object to a String.
     * @param e Input JSON object
     * @return String containing the serialized JSON object
     */
    public static String write(JsonObject e) { return e.toString(0); }

    /**
     * Serializes a JSON object to a file.
     * @param e Input JSON object
     * @param file_path Path to the target file
     * @throws IOException If writing to the file fails
     */
    public static void writeFile(JsonObject e, String file_path) throws IOException {
        PrintWriter file = new PrintWriter(new FileOutputStream(file_path));
        file.print(e.toString(0));
        file.close();
    }
}
