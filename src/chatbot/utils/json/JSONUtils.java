package chatbot.utils.json;

import chatbot.utils.json.parser.JSONParser;
import chatbot.utils.json.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONUtils {
    public static Object parseJsonFile(String fileName) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(new FileReader(fileName));
    }
}
