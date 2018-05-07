package chatbot.engine;

import chatbot.engine.nlp.POS;
import chatbot.engine.nlp.Processor;
import chatbot.engine.nlp.SentenceV2;
import chatbot.engine.nlp.StringUtils;
import chatbot.engine.nn.model.POSNeuralNetwork;

import java.util.ArrayList;

public class Chatbot {
    private Memory memory;

    private POSNeuralNetwork posNeuralNetwork;

    public Chatbot() {
        memory = new Memory();
        posNeuralNetwork = POSNeuralNetwork.loadPosWeight16(3);
    }

    public void resetMemory() {
        memory.reset();
    }

    public void save() {
        memory.saveMemory();
    }

    public String getReplyV2(String message) {
        String[] tokenized = Engine.tokenize(message);
        POS[] posTags = posNeuralNetwork.predictPOS(tokenized);
        String[] importantWords = Engine.removeUnimportantWords(posTags);
        boolean isYesNoQuestion = Engine.isYesNoQuestion(tokenized);
        boolean isQuestion = isYesNoQuestion || Engine.isQuestion(tokenized);

        SentenceV2 msgSentence = new SentenceV2(message, posTags);
        memory.add(msgSentence, isQuestion);

        if (isQuestion) {
            String ans = getAnswerV3(msgSentence, importantWords, isYesNoQuestion);
            if (ans == null || ans.length() == 0)
                return "It seems like my database doesn't know about your question, can you please tell me more?";
            else
                return ans;
        } else {
            // todo: randomize affirmative answer
            return "I see!";
        }
    }

    public String getAnswerV3(SentenceV2 sentenceV2, String[] importantWords, boolean isYesNoQuestion) {
        String message = sentenceV2.getSentence().toLowerCase();
        SentenceV2[] tfidfResults = memory.findTFIDFV2(importantWords, false);

        if (tfidfResults.length == 0) {
            // todo: randomize more answer
            if (!isYesNoQuestion)
                return "Sorry I don't understand what you mean, please tell me about that!";
            else
                return "No";
        } else {
            StringBuilder sb = new StringBuilder();
            String[] answers = new String[]{};

            if (message.contains("why")) {
                answers = Processor.processWhy(tfidfResults, sentenceV2, importantWords);
            } else if (message.contains("where")) {
                answers = Processor.processWhere(tfidfResults, sentenceV2, importantWords);
            } else if (message.contains("who")) {
                answers = Processor.processWho(tfidfResults, sentenceV2, importantWords);
            } else if (message.contains("what")) {
                answers = Processor.processWhat(tfidfResults, sentenceV2, importantWords);
            } else if (message.contains("when")) {
                answers = Processor.processWhen(tfidfResults, sentenceV2, importantWords);
            } else if (message.contains("did") || message.contains("do") || message.contains("does")) {
                answers = Processor.processYesNo(tfidfResults, sentenceV2, importantWords);
            }

            ArrayList<String> distinct = new ArrayList<>();

            for (String answer : answers) {
                if (!distinct.contains(answer)) {
                    distinct.add(answer);
                }
            }

            answers = distinct.toArray(new String[distinct.size()]);

            for (int i = 0; i < answers.length; i++) {
                String answer = answers[i];

                if (StringUtils.containsWord(answer, "my"))
                    answer = StringUtils.replaceAll(answer, "my", "your");
                else if (StringUtils.containsWord(answer, "your"))
                    answer = StringUtils.replaceAll(answer, "your", "my");

                if (StringUtils.containsWord(answer, "and")) {
                    String[] splitBy = StringUtils.splitByKeyword(answer, "and");

                    for (int j = 0; j < splitBy.length; j++) {
                        String s = splitBy[j];
                        sb.append(s.trim());
                        if (j < answers.length - 1)
                            sb.append("\n");
                    }
                } else {
                    sb.append(answer.trim());
                }

                if (i < answers.length - 1)
                    sb.append("\n");
            }

            return sb.toString();
        }
    }


}
