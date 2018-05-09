package chatbot.engine.nlp;

public class GrammarLibrary {
    public static final String[] towardWords = {
            "to",
            "towards",
            "onto",
            "into",
            "beside",
            "at",
            "after",
            "on",
            "above",
            "below",
            "under",
            "against",
            "along",
            "by",
            "between",
            "inside",
            "over",
            "in"
    };

    public static final String[] conjunctions = ("while\n" +
            "as soon as\n" +
            "although\n" +
            "before\n" +
            "even if\n" +
            "because\n" +
            "no matter how\n" +
            "whether\n" +
            "wherever\n" +
            "when\n" +
            "until\n" +
            "after\n" +
            "as if\n" +
            "how\n" +
            "if\n" +
            "provided\n" +
            "in that\n" +
            "once\n" +
            "supposing\n" +
            "while\n" +
            "unless\n" +
            "in case\n" +
            "as far as\n" +
            "now that\n" +
            "as\n" +
            "so that\n" +
            "though\n" +
            "since").split("\n");

    public static final String[] aux = {
            "is",
            "are",
            "was",
            "were"
    };

    public static boolean isAux(String word) {
        for (String s : aux) {
            if (s.equalsIgnoreCase(word))
                return true;
        }
        return false;
    }

    public static boolean isTowardWord(String word) {
        for (String s : towardWords) {
            if (s.equalsIgnoreCase(word))
                return true;
        }
        return false;
    }
}
