package chatbot.engine;

import chatbot.engine.nlp.SentenceV1;
import chatbot.engine.nlp.SentenceV2;
import chatbot.engine.nlp.TFIDF;
import chatbot.utils.Logger;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Memory {

    private final String path = "data/memory.csv";
    private final String pathV2 = "data/memoryV2.csv";
    private ArrayList<SentenceV1> memory;
    private ArrayList<SentenceV2> memoryV2;

    public Memory() {
        memory = new ArrayList<>();
        memoryV2 = new ArrayList<>();
        retrieveMemory();
    }

    public void reset() {
        Logger.println("Reset memory...");
        memory.clear();
        memoryV2.clear();
    }

    public void add(SentenceV2 sentenceV2, boolean isQuestion) {
        if (!isQuestion && !memoryV2.contains(sentenceV2)) {
            memoryV2.add(sentenceV2);
        }
    }

    public void add(SentenceV1 sentenceV1, boolean isQuestion) {
        // classify if its a question or information, if repeated question, can reply something funny.
        if (!isQuestion) {
            if (!memory.contains(sentenceV1))
                memory.add(sentenceV1);
        }
    }

    public SentenceV1[] findTFIDF(String[] filteredTarget, boolean stemming) {
        return TFIDF.calculate(memory, filteredTarget, stemming);
    }

    public SentenceV2[] findTFIDFV2(String[] filteredTarget, boolean stemming) {
        return TFIDF.calculateV2(memoryV2, filteredTarget, stemming);
    }

    public void saveMemory() {
        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(path));
            for (SentenceV1 s : memory) {
                f.write(s.toString() + "\n");
            }
            f.close();

            f = new BufferedWriter(new FileWriter(pathV2));
            for (SentenceV2 s : memoryV2) {
                f.write(s.toString() + "\n");
            }
            f.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot write file due to " + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void retrieveMemory() {
        try {
            if (!new File(path).exists()) {
                Logger.println("First time running will not load memory");
                return;
            }

            BufferedReader f = new BufferedReader(new FileReader(path));
            String s;

            while ((s = f.readLine()) != null) {
                memory.add(SentenceV1.parseString(s));
            }

            f = new BufferedReader(new FileReader(pathV2));

            while ((s = f.readLine()) != null) {
                memoryV2.add(SentenceV2.parseString(s));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot read file due to " + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
