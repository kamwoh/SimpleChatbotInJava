package chatbot.inputs;

import chatbot.engine.Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Input {
    public static Double[] createInput(String word, ArrayList<String> words) {
        String[] inputWords = Engine.tokenize(word);
        Double[] bag = new Double[words.size()];
        // stem words
        inputWords = Engine.stem(inputWords);
//        for (int j = 0; j < inputWords.length; j++) {
//            inputWords[j] = Engine.stem(inputWords[j].toLowerCase());
//        }
        // set input vector
        List<String> inputWordsList = Arrays.asList(inputWords);

        for (int j = 0; j < words.size(); j++) {
            if (inputWordsList.contains(words.get(j)))
                bag[j] = 1.;
            else
                bag[j] = 0.;
        }

        return bag;
    }
}
