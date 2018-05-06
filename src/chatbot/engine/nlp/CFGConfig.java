package chatbot.engine.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CFGConfig {

    private HashMap<String, ArrayList<String>> cfgMap;

    public CFGConfig() {
        cfgMap = new HashMap<>();
        setup();
    }

    private void setup() {
        cfgMap.put("S", generateList("VP"));
        cfgMap.put("S", generateList("AUX", "NP", "VP"));
        cfgMap.put("S", generateList("NP", "VP"));
        cfgMap.put("S", generateList("NP", "VERB"));
        cfgMap.put("VP", generateList("VP", "PP"));
        cfgMap.put("VP", generateList("VERB", "NP"));
        cfgMap.put("VP", generateList("VERB", "PP"));
        cfgMap.put("VP", generateList("VERB", "NOUN"));
        cfgMap.put("VP", generateList("VERB", "VP"));
        cfgMap.put("VP", generateList("VERB"));
        cfgMap.put("NP", generateList("DET", "NOUN"));
        cfgMap.put("NP", generateList("NP", "NOUN"));
        cfgMap.put("NP", generateList("NOUN"));
        cfgMap.put("PP", generateList("IN", "NP"));
    }

    private ArrayList<String> generateList(String... s) {
        return (ArrayList<String>) Arrays.asList(s);
    }

}
