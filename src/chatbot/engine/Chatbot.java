package chatbot.engine;

import chatbot.engine.nlp.*;
import chatbot.engine.nn.model.POSNeuralNetwork;
import chatbot.engine.nn.model.TextClassificationNeuralNetwork;

public class Chatbot {
    private Memory memory;

    private POSNeuralNetwork posNeuralNetwork;
    private TextClassificationNeuralNetwork tcNeuralNetwork;

    public Chatbot() {
        memory = new Memory();
        posNeuralNetwork = POSNeuralNetwork.loadPosWeight32(1);
    }

    public String getReply(String message) {
        String[] tokenized = Engine.tokenize(message);
        POS[] posTags = posNeuralNetwork.predictPOS(tokenized);
        String[] importantWords = Engine.removeUnimportantWords(posTags);
        boolean isQuestion = Engine.isQuestion(tokenized);

        for (POS posTag : posTags) {
            System.out.println(posTag.getTerm() + " -> " + posTag.getTag() + " -> " + posTag.getProb());
        }

        Phrase[] adjectiveNoun = InformationAnalyzer.findAdjectiveNoun(posTags);
        Phrase[] whenWhere = InformationAnalyzer.findWhenAndWhere(posTags);
        Phrase[] subjects = InformationAnalyzer.findSubject(posTags, isQuestion);

        for (Phrase where : adjectiveNoun) {
            System.out.println("adjectiveNoun: " + where);
        }

        for (Phrase where : whenWhere) {
            System.out.println("whenWhere: " + where);
        }

        for (Phrase where : subjects) {
            System.out.println("subjects: " + where);
        }


        Sentence msgSentence = new Sentence(message);
        msgSentence.setSubjects(subjects);
        msgSentence.setLocations(whenWhere);
        msgSentence.setAdjectiveNouns(adjectiveNoun);

        memory.add(msgSentence, isQuestion);

        if (isQuestion) {
            Sentence[] tfidfResults = memory.findTFIDF(importantWords);

            for (Sentence t : tfidfResults) {
                System.out.println("found -> " + t);
            }

            if (tfidfResults.length == 0) {
                // todo: randomize more answer
                return "Sorry I don't understand what you mean, please tell me about that!";
            } else {
                for (int i = 0; i < tfidfResults.length; i++) {
                    Sentence result = tfidfResults[i];

                    if (message.toLowerCase().contains("when") || message.toLowerCase().contains("where")) {
                        Phrase[] locations = result.getLocations();

                        if (locations.length == 0)
                            return result.getSentence();

                        for (int j = 0; j < locations.length; j++) {
                            Phrase location = locations[j];
                            for (int k = 0; k < importantWords.length; k++) {
                                if (!location.getPhrase().toLowerCase().contains(importantWords[k].toLowerCase()))
                                    return location.getPhrase();
                            }
                        }
                    }

                    Phrase[] adjectiveNouns = result.getAdjectiveNouns();

                    if (adjectiveNouns.length == 0)
                        return result.getSentence();

                    Phrase[] resultSubjects = result.getSubjects();

                    boolean sameSubject = false;
                    if (resultSubjects.length != 0) {
                        for (int j = 0; j < subjects.length; j++) {
                            if (sameSubject)
                                break;

                            for (int k = 0; k < resultSubjects.length; k++) {
                                String s1 = subjects[j].getPhrase().toLowerCase();
                                String s2 = resultSubjects[k].getPhrase().toLowerCase();
                                // cannot use contains, maybe have error, use string similarity is better.
                                // 80% similarity is enough.
                                double similarity = StringSimilarity.similarity(s1, s2);
                                System.out.println("Similarity s1: " + s1 + " s2:" + s2 + " :" + similarity);
                                if (similarity >= 0.8) {
                                    sameSubject = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!sameSubject && i == tfidfResults.length - 1)
                        // todo: randomize answer
                        return "Seems like my memory doesn't have this information, could you mind to tell me more?";


                    if (message.toLowerCase().contains("who") || message.toLowerCase().contains("what")) {
                        System.out.println("result subject length -> " + resultSubjects.length);

                        for (int j = 0; j < resultSubjects.length; j++) {
                            Phrase subject = adjectiveNouns[j];
                            for (int k = 0; k < importantWords.length; k++) {
                                System.out.println(subject.getPhrase() + " <------ ");
                                if (!subject.getPhrase().toLowerCase().contains(importantWords[k].toLowerCase()))
                                    return subject.getPhrase();
                            }
                        }
                    }

                    for (int j = 0; j < resultSubjects.length; j++) {
                        Phrase combinedSubject = resultSubjects[j];
                        for (int k = 0; k < importantWords.length; k++) {
                            if (resultSubjects[j].getPhrase().toLowerCase().contains(importantWords[k].toLowerCase()))
                                return combinedSubject.getPhrase();
                        }
                    }
                }

                return tfidfResults[0].getSentence();
            }
        } else {
            // todo: randomize affirmative answer
            return "I see!";
        }
    }
}
