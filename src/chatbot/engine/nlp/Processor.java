package chatbot.engine.nlp;

import chatbot.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class Processor {

    public static boolean checkContextValue(SentenceV2 result, String[] importantWords) {
        Logger.println("Checking: " + result.getSentence());

        int count = 0;

        for (String word : importantWords) {
            int index = result.indexPosOf(word, true);
            if (index != -1) {
                count++;
            }
        }

        double percentageImportantWords = (double) count / (double) importantWords.length;
        double percentageSentence = (double) count / (double) result.getPosTags().length;
        double context = (percentageImportantWords + percentageSentence) / 2;

        Logger.println("Context: " + context);

        return context < 0.55;
    }

    public static String[] processWhy(SentenceV2[] tfidfResults, SentenceV2 question, String[] importantWords) {
        ArrayList<String> answers = new ArrayList<>();
        String message = question.getSentence().toLowerCase();

        for (SentenceV2 result : tfidfResults) {
            // just assume they have one "why" stuff
            if (checkContextValue(result, importantWords))
                continue;

            int indexOfBecause = result.indexOf("because", true);
            int indexOfDueTo = result.indexOf("due to", true);
            int indexOfAs = result.indexOf("as", true);
            String substring = "";

            if (indexOfBecause != -1) {
                substring = result.getSentence().substring(indexOfBecause);
            } else if (indexOfDueTo != -1) {
                substring = result.getSentence().substring(indexOfDueTo);
            } else if (indexOfAs != -1) {
                substring = result.getSentence().substring(indexOfAs);
            }

            // after found substring of "why" stuff
            // the sentence shouldn't contain the question words
            for (String s : importantWords) {
                if (substring.contains(s)) {
                    substring = "";
                    break;
                }
            }

            if (substring.length() != 0)
                answers.add(substring);
        }

        return answers.toArray(new String[answers.size()]);
    }

    public static String[] processWhere(SentenceV2[] tfidfResults, SentenceV2 question, String[] importantWords) {
        ArrayList<String> answers = new ArrayList<>();
        POS[] posTagOfImportantWords = new POS[importantWords.length];
        int indexAdp = -1;

        for (int i = 0; i < importantWords.length; i++) {
            POS pos = question.posOf(importantWords[i], true);

            if (pos == null)
                Logger.println("Warning: " + importantWords[i] + " pos is null");

            if (indexAdp == -1 && pos != null && pos.isAdp()) // assume simple structure first...
                indexAdp = i;

            posTagOfImportantWords[i] = pos;
        }

        Logger.println("indexAdp: " + indexAdp);

        if (indexAdp != -1) {
            // case one: question contains adposition tag (given direction)
            POS adpPOS = posTagOfImportantWords[indexAdp];

            Logger.println("adpPOS: " + adpPOS.toString());

            processWhereHelper(tfidfResults, answers, adpPOS.getTerm(), importantWords);
        } else {
            // case two: question does not contains adposition tag (not given direction), rare case but just in case
            // so in this case, I guess the direction
            String[] towardWords = GrammarLibrary.towardWords;
            for (String towardWord : towardWords) {
                if (processWhereHelper(tfidfResults, answers, towardWord, importantWords))
                    break;
            }
        }


        return answers.toArray(new String[answers.size()]);
    }

    private static boolean processWhereHelper(SentenceV2[] tfidfResults, ArrayList<String> answers, String adpWord, String[] importantWords) {
        boolean added = false;

        for (SentenceV2 result : tfidfResults) {
            if (checkContextValue(result, importantWords))
                continue;

            int indexOfAdp = result.indexPosOf(adpWord, true);
            Logger.println("indexOfAdp in sentence: " + indexOfAdp);
            StringBuilder sb = new StringBuilder();
            if (indexOfAdp != -1) {
                int i = indexOfAdp + 1;
                boolean nounFound = false;
                ArrayList<String> toBeAdded = new ArrayList<>();
                while (i < result.getPosTags().length) {
                    // adp verb [noun noun noun] verb
                    POS resultWordPOS = result.getPosTags()[i++];
                    Logger.println("Checking word: " + resultWordPOS.toString());
                    if (!nounFound && resultWordPOS.isVerb()) {
                        toBeAdded.add(resultWordPOS.getTerm());
                    } else if (resultWordPOS.isAdj() || resultWordPOS.isNoun() || resultWordPOS.isDet() || resultWordPOS.isAdv()) {
                        nounFound = true;
                        toBeAdded.add(resultWordPOS.getTerm());
                    } else {
                        if (nounFound) {
                            Logger.println("Ended loop at: " + resultWordPOS.toString());
                            break;
                        }
                        if (resultWordPOS.isAdp()) {
                            Logger.println("Found a adposition: " + resultWordPOS.toString());
                            break;
                        }
                    }
                }

                for (int j = 0; j < toBeAdded.size(); j++) {
                    sb.append(toBeAdded.get(j));
                    if (j < toBeAdded.size() - 1)
                        sb.append(" ");
                }
            }

            if (sb.toString().length() != 0) {
                added = true;
                answers.add(sb.toString()); // "noun noun noun"
            }
        }

        return added;
    }

    public static String[] processWho(SentenceV2[] tfidfResults, SentenceV2 question, String[] importantWords) {
        // case one: question is asking verb: Who [verb]?
        // find the noun before the verb, if it is a pronoun (he/she/they/it), split the sentence by conjunction, then search Noun from index 0
        // to search conjunction, if no conjunction in the sentence, try to search in GrammarLibrary

        // case two: question is asking adjective/noun: Who is [adjective/noun]
        // after "is/are" is a noun/adjective and same term with question, then return adjective/noun before "is/are"

        ArrayList<String> answers = new ArrayList<>();
        POS[] posTagOfImportantWords = new POS[importantWords.length];
        int indexVerb = -1;
        int indexAdjNoun = -1;

        for (int i = 0; i < importantWords.length; i++) {
            POS pos = question.posOf(importantWords[i], true);

            if (pos == null)
                Logger.println("Warning: " + importantWords[i] + " pos is null");

            if (indexVerb == -1 && pos != null && pos.isVerb()) // assume simple structure first...
                indexVerb = i;

            if (indexAdjNoun == -1 && pos != null && (pos.isAdj() || pos.isNoun() || pos.isPron())) // assume simple structure first...
                indexAdjNoun = i;

            posTagOfImportantWords[i] = pos;
        }

        Logger.println("indexVerb: " + indexVerb);
        Logger.println("indexAdjNoun: " + indexAdjNoun);

        if (indexVerb != -1) {
            POS verbPOS = posTagOfImportantWords[indexVerb];
            processWhoHelperForVerb(tfidfResults, answers, verbPOS.getTerm(), question, importantWords);
        } else if (indexAdjNoun != -1) {
            processWhoHelperForAdjNoun(tfidfResults, answers, importantWords, indexAdjNoun);
        } else {
            Logger.println("Warning: indexVerb and indexAdjNoun not found in question: " + question.getSentence());
        }
        return answers.toArray(new String[answers.size()]);
    }

    private static void processWhoHelperForVerb(SentenceV2[] tfidfResults, ArrayList<String> answers, String verbWord, SentenceV2 question, String[] importantWords) {
        for (SentenceV2 result : tfidfResults) {
            Logger.println("Checking: " + result.getSentence());

            if (checkContextValue(result, importantWords))
                continue;

            int indexOfVerb = result.indexPosOf(verbWord, true);
            Logger.println("indexOfVerb in sentence: " + indexOfVerb);
            StringBuilder sb = new StringBuilder();
            if (indexOfVerb != -1) {
                int i = indexOfVerb - 1;
                int pronounIndex = -1;
                boolean questionContainsNot = StringUtils.containsWord(question.getSentence().toLowerCase(), "not");
                boolean containsNot = StringUtils.containsWord(result.getSentence().toLowerCase(), "not");
                boolean nounFound = false;
                boolean pronounFound = false;

                ArrayList<String> toBeReversed = new ArrayList<>();
                while (i >= 0) {
                    // verb [noun noun] verb
                    POS resultWordPOS = result.getPosTags()[i--];
                    if (resultWordPOS.isPron())
                        pronounFound = true;

                    if (!pronounFound) {
                        if (resultWordPOS.isAdj() || resultWordPOS.isNoun() || resultWordPOS.isDet() || resultWordPOS.isConj()) {
                            nounFound = true;
                            toBeReversed.add(resultWordPOS.getTerm());
                        } else {
                            if (nounFound) {
                                Logger.println("Ended loop at: " + resultWordPOS.toString());
                                break;
                            }
                        }
                    } else {
                        pronounIndex = i + 1; // +1 because i-- at line 195
                        Logger.println("Found a pronoun: " + resultWordPOS.getTerm());
                        break;
                    }
                }

                if (!pronounFound) {
                    if (containsNot == questionContainsNot) {
                        for (int j = toBeReversed.size() - 1; j >= 0; j--) {
                            sb.append(toBeReversed.get(j));
                            if (j != 0)
                                sb.append(" ");
                        }

                        Logger.println("Reversed string: " + sb.toString());
                    } else {
                        Logger.println("Context not similar: result: " + containsNot + " question: " + questionContainsNot);
                    }
                } else {
                    // check for I/You
                    POS foundPronoun = result.getPosTags()[pronounIndex];
                    String[] specialPronoun = {"I", "You"};
                    int specialPronounIndex = -1;

                    for (int j = 0; j < specialPronoun.length; j++) {
                        String pron = specialPronoun[j];
                        if (foundPronoun.getTerm().equalsIgnoreCase(pron)) {
                            specialPronounIndex = j;
                            break;
                        }
                    }

                    if (specialPronounIndex == -1) {
                        for (int j = 0; j < pronounIndex; j++) { // do not include pronounIndex
                            POS resultWordPOS = result.getPosTags()[j];
                            if (resultWordPOS.isAdj() || resultWordPOS.isNoun()) {
                                sb.append(resultWordPOS.getTerm());
                            } else {
                                break;
                            }

                            if (j < pronounIndex - 1)
                                sb.append(" ");
                        }
                    } else {
                        if (specialPronounIndex == 0) { // I
                            sb.append("You");
                        } else { // You
                            sb.append("Me");
                        }
                    }

                    Logger.println("Pronoun result: " + sb.toString());
                }
            }

            if (sb.toString().length() != 0) {
                answers.add(sb.toString()); // "noun noun noun"
            }
        }
    }

    private static void processWhoHelperForAdjNoun(SentenceV2[] tfidfResults, ArrayList<String> answers, String[] importantWords, int indexAdjNoun) {
        for (SentenceV2 result : tfidfResults) {
            if (checkContextValue(result, importantWords))
                continue;

            int indexOfAdjNoun = result.indexPosOf(importantWords[indexAdjNoun], true);
            Logger.println("indexOfAdjNoun in sentence: " + indexOfAdjNoun);
            StringBuilder sb = new StringBuilder();
            if (indexOfAdjNoun != -1) {
                int indexOfIsAreWasWere = -1;
                String[] isAreWasWere = {"is", "are", "was", "were"};
                for (String anIsAreWasWere : isAreWasWere) {
                    indexOfIsAreWasWere = result.indexPosOf(anIsAreWasWere, true);
                    if (indexOfIsAreWasWere != -1)
                        break;
                }
                Logger.println("anIsAreWasWere: " + indexOfIsAreWasWere);
                if (indexOfIsAreWasWere != -1) {
                    ArrayList<String> before = new ArrayList<>();
                    ArrayList<String> after = new ArrayList<>();
                    int beforeCount = 0;
                    int afterCount = 0;
                    // before
                    for (String keyword : importantWords) {
                        for (int j = 0; j < indexOfIsAreWasWere; j++) {
                            POS currentPOS = result.getPosTags()[j];

                            if (StringSimilarity.similarity(currentPOS.getTerm().toLowerCase(), keyword.toLowerCase()) >= 0.75) {
                                beforeCount++;
                            }

                            if (currentPOS.isNoun() || currentPOS.isAdj() || currentPOS.isPron() || currentPOS.isDet() || currentPOS.isConj()) {
                                before.add(currentPOS.getTerm());
                            } else {
                                break;
                            }
                        }

                        // after
                        for (int j = indexOfIsAreWasWere + 1; j < result.getPosTags().length; j++) {
                            POS currentPOS = result.getPosTags()[j];

                            if (StringSimilarity.similarity(currentPOS.getTerm().toLowerCase(), keyword.toLowerCase()) >= 0.75) {
                                afterCount++;
                            }

                            if (currentPOS.isNoun() || currentPOS.isAdj() || currentPOS.isPron() || currentPOS.isConj()) {
                                after.add(currentPOS.getTerm());
                            } else {
                                break;
                            }
                        }
                    }

                    Logger.println("beforeCount: " + beforeCount);
                    Logger.println("afterCount: " + afterCount);

                    ArrayList<String> distinct1 = new ArrayList<>();

                    for (String s : before) {
                        if (!distinct1.contains(s)) {
                            distinct1.add(s);
                        }
                    }

                    before.clear();
                    before.addAll(distinct1);

                    ArrayList<String> distinct2 = new ArrayList<>();

                    for (String s : after) {
                        if (!distinct2.contains(s)) {
                            distinct2.add(s);
                        }
                    }

                    after.clear();
                    after.addAll(distinct2);

                    Logger.println("before: " + before.toString());
                    Logger.println("after: " + after.toString());

                    if (beforeCount < afterCount) { // before sentence overlap less than after sentence, then take before
                        for (int j = 0; j < before.size(); j++) {
                            sb.append(before.get(j));
                            if (j < before.size() - 1)
                                sb.append(" ");
                        }
                    } else {
                        for (int j = 0; j < after.size(); j++) {
                            sb.append(after.get(j));
                            if (j < after.size() - 1)
                                sb.append(" ");
                        }
                    }

                    Logger.println("Result: " + sb.toString());
                }
            }

            if (sb.toString().length() != 0) {
                answers.add(sb.toString()); // "noun noun noun"
            }
        }
    }

    public static String[] processWhat(SentenceV2[] tfidfResults, SentenceV2 question, String[] importantWords) {
        // case one: what did/do/does [subject (noun/adj)] [predicate (verb)]
        // find the adjnoun that behind the subject and predicate
        ArrayList<String> answers = new ArrayList<>();

        for (SentenceV2 result : tfidfResults) {
            if (checkContextValue(result, importantWords))
                continue;

            Logger.println("Processing for \"what\": " + result.getSentence());
            int indexOfPredicate = -1;

            for (int i = importantWords.length - 1; i >= 0; i--) {
                String word = importantWords[i];
                indexOfPredicate = Math.max(result.indexPosOf(word, true), indexOfPredicate);
                Logger.println("Finding " + i + "th word of question " + word + " at index: " + indexOfPredicate);
                if (indexOfPredicate != -1) { // found predicate
                    Logger.println("Found the index of predicate");
                    break;
                }
            }

            Logger.println("Index of predicate: " + indexOfPredicate);

            if (indexOfPredicate != -1) { // just in case not found
                ArrayList<String> inference = new ArrayList<>();
                boolean nounFound = false;
                for (int i = indexOfPredicate + 1; i < result.getPosTags().length; i++) {
                    POS currentPOS = result.getPosTags()[i];
                    if (!nounFound && currentPOS.isVerb()) { // if found verb, add it, but only when nouns are not found
                        if (!GrammarLibrary.isAux(currentPOS.getTerm()))
                            inference.add(currentPOS.getTerm());
                    } else if (currentPOS.isDet() || currentPOS.isNoun() || currentPOS.isAdj() || currentPOS.isAdv()) {
                        nounFound = true;
                        inference.add(currentPOS.getTerm());
                    } else {
                        if (nounFound)
                            break;
                    }
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < inference.size(); i++) {
                    sb.append(inference.get(i));
                    if (i < inference.size() - 1)
                        sb.append(" ");
                }

                if (sb.toString().length() != 0)
                    answers.add(sb.toString());
            }
        }

        return answers.toArray(new String[answers.size()]);
    }

    public static String[] processWhen(SentenceV2[] tfidfResults, SentenceV2 question, String[] importantWords) {
        // case one: time (at)
        // case two: date (on)
        // case three: day (on)
        // case four: month (in)
        ArrayList<String> answers = new ArrayList<>();

        for (SentenceV2 result : tfidfResults) {
            if (checkContextValue(result, importantWords))
                continue;

            int indexOfAt = result.indexPosOf("at", true);
            int indexOfOn = result.indexPosOf("on", true);
            int indexOfIn = result.indexPosOf("in", true);
            int[] indices = {indexOfAt, indexOfOn, indexOfIn};

            for (int i : indices) {
                Logger.println("index i: " + i);
                if (i != -1) {
                    i = i + 1;
                    ArrayList<String> inference = new ArrayList<>();
                    for (int j = i; j < result.getPosTags().length; j++) {
                        POS currentPOS = result.getPosTags()[j];
                        if (currentPOS.isNum() || currentPOS.isNoun() || currentPOS.isDet()) {
                            inference.add(currentPOS.getTerm());
                        } else {
                            break;
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    ArrayList<String> importantWordsInList = new ArrayList<>();
                    Collections.addAll(importantWordsInList, importantWords);

                    int count = 0;
                    int total = i - Math.max(0, i - 5);
                    for (int j = Math.max(0, i - 5); j < i; j++) {
                        if (importantWordsInList.contains(result.getPosTags()[j].getTerm())) {
                            count++;
                        }
                    }

                    double percentage = (double) count / (double) total;
                    Logger.println(String.format("context matches: %.2f", percentage));
                    if (percentage >= 0.6) {
                        for (int j = 0; j < inference.size(); j++) {
                            sb.append(inference.get(j));
                            if (j < inference.size() - 1)
                                sb.append(" ");
                        }

                        if (sb.toString().length() != 0)
                            answers.add(sb.toString());
                    }
                }
            }
        }

        return answers.toArray(new String[answers.size()]);
    }

    public static String[] processYesNo(SentenceV2[] tfidfResults, SentenceV2 question, String[] importantWords) {
        // if context high percentage - return yes else no
        // if context high percentage, but with a not, return no
        String[] answers = new String[]{"No"};
        for (SentenceV2 result : tfidfResults) {
            String sentence = result.getSentence();
            Logger.println("Processing sentence for YesNo: " + sentence);
            boolean containNot = StringUtils.containsWord(sentence, "not");

            if (!checkContextValue(result, importantWords)) { // 50% context similar is enough
                if (containNot)
                    answers[0] = "No";
                else
                    answers[0] = "Yes";

                break;
            }
        }
        return answers;
    }


}
