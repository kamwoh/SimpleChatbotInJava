package chatbot.engine.nlp;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculatorParser {
    public static String calculate(String userInput) {
        Object result = "";

        if (userInput.matches(".*\\d+.*")) {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            try {
                String expression = userInput;
                result = engine.eval(expression);
            } catch (ScriptException se) {
                se.printStackTrace();
            }

        }

        if (String.valueOf(result).length() != 0)
            return String.valueOf(result);
        else
            return null;
    }
}
