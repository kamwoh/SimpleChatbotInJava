package chatbot.engine.nlp;

public class StringSimilarity {
    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    /**
     * Source
     * 1. https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
     * 2. http://rosettacode.org/wiki/Levenshtein_distance#Java
     */
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static void main(String[] args) {
        System.out.println("Result abcdefg & gfedcba: " + similarity("abcdefg", "gfedcba"));
        System.out.println("Result ran & run: " + similarity("ran", "run"));
        System.out.println("Result width & wdth: " + similarity("width", "wdth"));
        System.out.println("Result sing & song: " + similarity("sing", "song"));
        System.out.println("Result Chiayi & Yukang: " + similarity("Chiayi", "Yukang"));
        System.out.println("Result compare & compare: " + similarity("compare", "compare"));
    }

}
