package chatbot.engine;

import chatbot.engine.nlp.*;
import chatbot.engine.nn.model.POSNeuralNetwork;
import chatbot.engine.nn.model.TextClassificationNeuralNetwork;

import java.util.Arrays;

public class Chatbot {
    private Memory memory;

    private POSNeuralNetwork posNeuralNetwork;
    private TextClassificationNeuralNetwork tcNeuralNetwork;

    public Chatbot() {
        memory = new Memory();
        posNeuralNetwork = POSNeuralNetwork.loadPosWeight32(1);
    }

    public void save() {
        memory.saveMemory();
    }

    public String getReply(String message) {
        String[] tokenized = Engine.tokenize(message);
        POS[] posTags = posNeuralNetwork.predictPOS(tokenized);
        String[] importantWords = Engine.removeUnimportantWords(posTags);
        boolean isYesNoQuestion = Engine.isYesNoQuestion(tokenized);
        boolean isQuestion = isYesNoQuestion || Engine.isQuestion(tokenized);

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
        if (isYesNoQuestion) {
            // todo:
            return "Yes";
        } else if (isQuestion) {
            return getAnswerV2(msgSentence, importantWords);
        } else {
            // todo: randomize affirmative answer
            return "I see!";
        }
    }

    public String getAnswerV2(Sentence sentence, String[] importantWords) {
        // 1. search tfidf (sorted)
        // 2. for each tfidf result sentence, search tf in the sentence, return highest tf
        String message = sentence.getSentence().toLowerCase();
        Sentence[] tfidfResults = memory.findTFIDF(importantWords);

        if (tfidfResults.length == 0) {
            // todo: randomize more answer
            return "Sorry I don't understand what you mean, please tell me about that!";
        } else {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < tfidfResults.length; i++) {
                Sentence t = tfidfResults[i];

                System.out.println("found -> " + t);

                boolean[] takeResult = TFIDF.findTF(t, importantWords);

                System.out.println("take result -> " + Arrays.toString(takeResult));

                if (i != 0 && i < tfidfResults.length - 1)
                    sb.append(", ");
                else if (i != 0)
                    sb.append(" and ");

                // when where
                if (message.contains("when") || message.contains("where")) {
                    if (takeResult[2]) {
                        Phrase[] locations = t.getLocations();
                        for (int j = 0; j < locations.length; j++) {
                            if (j != 0 && j < locations.length - 1)
                                sb.append(", ");
                            else if (j != 0)
                                sb.append(" and ");

                            sb.append(locations[j].getPhrase());
                        }
                    }
                } else {
                    System.out.println("before takeResult[0] -> " + sb.toString());

                    if (takeResult[0]) {
                        Phrase[] subjects = t.getSubjects();
                        for (int j = 0; j < subjects.length; j++) {
                            System.out.println("j -> " + j);
                            if (j != 0 && j < subjects.length - 1)
                                sb.append(", ");
                            else if (j != 0)
                                sb.append(" and ");

                            sb.append(subjects[j].getPhrase());
                        }
                    }

                    System.out.println("combining1 ->" + sb.toString());

                    if (takeResult[1]) {
                        if (takeResult[0])
                            sb.append(" or it can be ");

                        Phrase[] adjectiveNouns = t.getAdjectiveNouns();
                        for (int j = 0; j < adjectiveNouns.length; j++) {
                            if (j != 0 && j < adjectiveNouns.length - 1)
                                sb.append(", ");
                            else if (j != 0)
                                sb.append(" and ");

                            sb.append(adjectiveNouns[j].getPhrase());
                        }
                    }

                    System.out.println("combining2 ->" + sb.toString());
                }
            }

            return sb.toString();
        }
    }

    public String getAnswer(String message, String[] importantWords, Phrase[] questionSubjects, Phrase[] questionAdjNouns) {


        // 1. find tfidf
        // 2. for each tfidf result, identify if the question is asking about location
        // 3. if the location phrase is not the important word, then return the location
        // 4. for e.g. The river is by the school, because if i am asking, where is the school, i should return something
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
                Phrase[] combinedSubjects = Phrase.combine(adjectiveNouns, resultSubjects);
                Phrase[] searchSubjects = Phrase.combine(questionSubjects, questionAdjNouns);

//                boolean sameSubject = false;
//                if (combinedSubjects.length != 0) {
//                    for (int j = 0; j < searchSubjects.length; j++) {
//                        if (sameSubject)
//                            break;
//
//                        for (int k = 0; k < combinedSubjects.length; k++) {
//                            String s1 = searchSubjects[j].getPhrase().toLowerCase();
//                            String s2 = combinedSubjects[k].getPhrase().toLowerCase();
//                            // cannot use contains, maybe have error, use string similarity is better.
//                            // 80% similarity is enough.
//                            double similarity = StringSimilarity.similarity(s1, s2);
//                            System.out.println("Similarity s1: " + s1 + " s2:" + s2 + " :" + similarity);
//                            if (similarity >= 0.8) {
//                                sameSubject = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                if (!sameSubject && i == tfidfResults.length - 1)
//                    // todo: randomize answer
//                    return "Seems like my memory doesn't have this information, could you mind to tell me more?";


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
    }
}
