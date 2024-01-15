package org.lavajuno.lucidjson;

import org.lavajuno.lucidjson.util.Pair;

import java.text.ParseException;
import java.util.Vector;

/**
 * Abstract representation of a single JSON entity.
 * Instances of JsonEntity can be objects, arrays, strings, numbers, or literals.
 * JsonEntity provides functionality for validating and parsing JSON for its inheritors.
 */
@SuppressWarnings("unused")
public abstract class JsonEntity {
    protected static final String PAIR_RGX = "^\".+\" *: *.+$";
    protected static final String OBJECT_RGX = "^\\{.*}$";
    protected static final String ARRAY_RGX = "^\\[.*]$";
    protected static final String STRING_RGX = "^ *\".*\"$";
    protected static final String NUMBER_RGX = "^-?[0-9]+(\\.[0-9]+)?([Ee][0-9]+)?$";
    protected static final String LITERAL_RGX = "^(true|false|null)$";

    /**
     * Possible matches for a segment of text
     */
    protected enum Matches {
        OBJECT, ARRAY, STRING, NUMBER, LITERAL, NONE
    }

    /**
     * Matches the input text to a predefined category.
     * @param text Text to match
     * @return The category that the text is matched to. Can be NONE.
     */
    protected static Matches match(String text) {
        if(text.matches(OBJECT_RGX))  { return Matches.OBJECT;  }
        if(text.matches(ARRAY_RGX))   { return Matches.ARRAY;   }
        if(text.matches(STRING_RGX))  { return Matches.STRING;  }
        if(text.matches(NUMBER_RGX))  { return Matches.NUMBER;  }
        if(text.matches(LITERAL_RGX)) { return Matches.LITERAL; }
        return Matches.NONE;
    }

    /**
     * Scans a line and finds the index of the character that closes
     * an entity in a scoped structure. ( ex. open->{<- { { } } { } close->}<- )
     * @param line Line to scan
     * @param open Opening character
     * @param close Closing character
     * @param start Index to start scanning at
     * @return Index of the closing character. -1 if no character is found.
     */
    protected static int findClosing(String line, char open, char close, int start) {
        int scope = 0;
        boolean enclosed = false;
        for(int i = start + 1; i < line.length(); i++) {
            if(line.charAt(i) == '"' && line.charAt(i - 1) != '\\') {
                enclosed = !enclosed; // double quotes, not escaped
            } else if(!enclosed && line.charAt(i) == open && line.charAt(i - 1) != '\\') {
                scope++; // not enclosed, opening character, not escaped
            } else if(!enclosed && line.charAt(i) == close && line.charAt(i - 1) != '\\') {
                // not enclosed, closing character, not escaped
                if(scope == 0) { return i; }
                scope--;
            }
        }
        return -1; // fail
    }

    /**
     * Scans the input text and finds the index of the next instance of a
     * character that is not enclosed by a pair of another given character.
     * @param text Text to scan
     * @param c Character to find
     * @param start Index to start scanning at
     * @return Index of the found character. -1 if not character is found.
     */
    protected static int findNext(String text, char c, int start) {
        boolean enclosed = text.charAt(start) == '\"';
        for(int i = start + 1; i < text.length(); i++) {
            if(text.charAt(i) == '"' && text.charAt(i - 1) != '\\') {
                enclosed = !enclosed; // double quotes, not escaped
            } else if(!enclosed && text.charAt(i) == c && text.charAt(i - 1) != '\\') {
                return i; // not enclosed, target character, not escaped
            }
        }
        return -1; // fail
    }

    /**
     * Scans the input text and finds the index of the next instance of a
     * delimiter character that is not enclosed by an object or array.
     * @param text Text to scan
     * @param c Delimiter character to find
     * @param start Index to start scanning at
     * @return Index of the found character. -1 if not character is found.
     */
    protected static int findDelimiter(String text, char c, int start) {
        boolean enclosed = false;
        int i = start;
        for(; i < text.length(); i++) {
            if(text.charAt(i) == '"' && text.charAt(i - 1) != '\\') {

                enclosed = !enclosed; // double quotes, not escaped
            } else if(!enclosed && text.charAt(i) == '{') {
                i = findClosing(text, '{', '}', i); // ignore commas in objects
            } else if(!enclosed && text.charAt(i) == '[') {
                i = findClosing(text, '[', ']', i); // ignore commas in arrays
            } else if(!enclosed && text.charAt(i) == c) {
                return i; // found comma
            }
        }
        return -1; // fail
    }

    /**
     * Scans the input and splits comma-delimited values of a JSON
     * object or array into a Vector of Strings.
     * @param text Text to scan
     * @return Elements of the JSON entity as a Vector of Strings.
     */
    protected static Vector<String> splitValues(String text) {
        Vector<String> values = new Vector<>();
        int e_start = 1; // ignore opening '[' / '{'
        int e_end = 0;
        while(true) {
            e_end = findDelimiter(text, ',', e_start);
            if(e_end == -1) {
                // last element (no trailing comma)
                values.add(text.substring(e_start, text.length() - 1)); // ignore closing ']' / '}'
                break;
            }
            values.add(text.substring(e_start, e_end));
            e_start = e_end + 1; // next element, skip comma
        }
        return values;
    }

    /**
     * Scans text containing a key-value pair and returns the key.
     * @param text Text to scan
     * @param end Index of the end of the key
     * @return Key found in the pair
     * @throws ParseException If the input does not contain a key.
     */
    protected static String parseKey(String text, int end) throws ParseException {
        String key_raw = text.substring(0, end).strip();
        if(!key_raw.matches(STRING_RGX)) {
            printError(text.substring(0, end), "Expected a string.");
            throw new ParseException("Expected a string.", 0);
        }
        return key_raw.substring(1, key_raw.length() - 1);
    }

    /**
     * Prints a parse error to stderr.
     * @param text The text that caused the parse error
     * @param explanation Why the parse error happened
     */
    protected static void printError(String text, String explanation) {
        System.err.println("vvv  JSON - Parse error on input:  vvv");
        System.err.println(text);
        System.err.println("^^^  ----------------------------  ^^^");
        System.err.println(explanation + "\n"); /* extra newline */
    }

    /**
     * Constructs a single JsonEntity by parsing the input.
     * @param text Text to parse
     * @return JsonEntity created from the input.
     * @throws ParseException If the input does not match any type of entity
     */
    protected static JsonEntity parseEntity(String text) throws ParseException {
        JsonEntity entity = switch(match(text)) {
            case OBJECT -> new JsonObject(text);
            case ARRAY -> new JsonArray(text);
            case STRING -> new JsonString(text);
            case NUMBER -> new JsonNumber(text);
            case LITERAL -> new JsonLiteral(text);
            case NONE -> null;
        };
        if(entity == null) {
            printError(text, "Expected an entity.");
            throw new ParseException("Expected an entity.", 0);
        }
        return entity;
    }

    /**
     * Constructs a key-value pair (String : JsonEntity) by parsing the input.
     * @param text Text to parse
     * @return Key-value pair created from the input.
     * @throws ParseException If the input does not match a pair containing a String and JsonEntity
     */
    protected static Pair<String, JsonEntity> parsePair(String text) throws ParseException {
        if(text.matches(PAIR_RGX)) {
            int split_index = findNext(text, ':', 0);
            if(split_index == -1) {
                printError(text, "Expected a key-value pair.");
                throw new ParseException("Expected a key-value pair.", 0);
            }
            String key = parseKey(text, split_index);
            JsonEntity value = parseEntity(text.substring(split_index + 1).strip());
            return new Pair<>(key, value);
        }
        printError(text, "Expected a key-value pair. (Not matched)");
        throw new ParseException("Expected a key-value pair. (Not matched)", 0);
    }

    /**
     * Serializes this JsonEntity to a String with newlines and indentation.
     * @param indent Indent of this JsonEntity
     * @return This JsonEntity as a String
     */
    protected abstract String toString(int indent);

    /**
     * Serializes this JsonEntity to a String with optional formatting.
     * @param pretty Whether to use newlines and indents in the output
     * @return This JsonEntity as a String
     */
    public String toString(boolean pretty) {
        return pretty ? this.toString(0) : this.toString();
    }

    /**
     * Serializes this JsonEntity to a String without any formatting.
     * @return This JsonEntity as a String
     */
    @Override
    public abstract String toString();
}
