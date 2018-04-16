package chatbot.engine;

import chatbot.engine.nlp.EquivalentSentence;
import chatbot.engine.nlp.Question;
import chatbot.engine.nlp.Sentence;
import chatbot.engine.nlp.TFIDF;

import java.util.ArrayList;

public class Memory {

    private ArrayList<Sentence> memory;
    private ArrayList<Question> askedQuestions;

    public Memory() {
        memory = new ArrayList<>();
        askedQuestions = new ArrayList<>();
    }

    public void add(Sentence sentence, boolean isQuestion) {
        // classify if its a question or information, if repeated question, can reply something funny.
        if (!isQuestion) {
            memory.add(sentence);
            Sentence eSentence = EquivalentSentence.getEquivalentSentence(sentence);
            if (eSentence != null)
                memory.add(eSentence);
        }
//        else
//            askedQuestions.add(new Question(sentence));
    }

    public Sentence[] findTFIDF(String[] filteredTarget) {
        return TFIDF.calculate(memory, filteredTarget);
    }

    public void saveMemory() {
        for (Sentence s: memory) {

        }
    }


}
