package chatbot.engine;

import chatbot.engine.nlp.EquivalentSentence;
import chatbot.engine.nlp.Question;
import chatbot.engine.nlp.Sentence;
import chatbot.engine.nlp.TFIDF;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Memory {

    private final String path = "data/memory.csv";
    private ArrayList<Sentence> memory;
    private ArrayList<Question> askedQuestions;

    public Memory() {
        memory = new ArrayList<>();
        askedQuestions = new ArrayList<>();
    }

    public void add(Sentence sentence, boolean isQuestion) {
        // classify if its a question or information, if repeated question, can reply something funny.
        if (!isQuestion) {
            if (!memory.contains(sentence))
                memory.add(sentence);
        }
    }

    public Sentence[] findTFIDF(String[] filteredTarget) {
        return TFIDF.calculate(memory, filteredTarget);
    }

    public void saveMemory() {
        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(path));
            for (Sentence s : memory) {
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
                System.out.println("First time running will not load memory");
                return;
            }

            BufferedReader f = new BufferedReader(new FileReader(path));
            String s;

            while ((s = f.readLine()) != null) {
                memory.add(Sentence.parseString(s));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot read file due to " + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
