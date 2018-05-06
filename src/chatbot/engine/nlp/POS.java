package chatbot.engine.nlp;

import java.util.ArrayList;

public class POS {

    public final static String PUNC = ".";
    public final static String ADJ = "ADJ";
    public final static String ADP = "ADP";
    public final static String ADV = "ADV";
    public final static String CONJ = "CONJ";
    public final static String DET = "DET";
    public final static String NOUN = "NOUN";
    public final static String NUM = "NUM";
    public final static String PRON = "PRON";
    public final static String PRT = "PRT";
    public final static String VERB = "VERB";
    public final static String X = "X";

    public final static String CC = "CC";
    public final static String CD = "CD";
    public final static String DT = "DT";
    public final static String EX = "EX";
    public final static String FW = "FW";
    public final static String IN = "IN";
    public final static String JJ = "JJ";
    public final static String JJR = "JJR";
    public final static String JJS = "JJS";
    public final static String LS = "LS";
    public final static String MD = "MD";
    public final static String NN = "NN";
    public final static String NNP = "NNP";
    public final static String NNPS = "NNPS";
    public final static String NNS = "NNS";
    public final static String PDT = "PDT";
    public final static String POS = "POS";
    public final static String PRP = "PRP";
    public final static String PRP$ = "PRP$";
    public final static String RB = "RB";
    public final static String RBR = "RBR";
    public final static String RBS = "RBS";
    public final static String RP = "RP";
    public final static String SYM = "SYM";
    public final static String TO = "TO";
    public final static String UH = "UH";
    public final static String VB = "VB";
    public final static String VBD = "VBD";
    public final static String VBG = "VBG";
    public final static String VBN = "VBN";
    public final static String VBP = "VBP";
    public final static String VBZ = "VBZ";
    public final static String WDT = "WDT";
    public final static String WP = "WP";
    public final static String WP$ = "WP$";
    public final static String WRB = "WRB";
    public final static String OTHER = "``$'',-LRB--NONE--RRB-.:";

    public static ArrayList<String> specialVerb = new ArrayList<>();

    static {
        specialVerb.add("is");
        specialVerb.add("are");
    }

    private String term;
    private String tag;
    private double prob;

    public POS(String term, String tag) {
        this.term = term;
        this.tag = tag;
        this.prob = 1.0;
    }

    public POS(String term, String tag, double prob) {
        this.term = term;
        this.tag = tag;
        this.prob = prob;

//        if (this.prob < 0.6)
//            this.tag = NOUN;
    }

    public int getTermLength() {
        return this.term.split(" ").length;
    }

    public String getTerm() {
        return term;
    }

    public String getTag() {
        return tag;
    }

    public double getProb() {
        return prob;
    }

    public boolean isPunc() {
        return tag.equalsIgnoreCase(PUNC);
    }

    public boolean isAdj() {
        return tag.equalsIgnoreCase(ADJ);
    }

    public boolean isAdp() {
        return tag.equalsIgnoreCase(ADP);
    }

    public boolean isAdv() {
        return tag.equalsIgnoreCase(ADV);
    }

    public boolean isConj() {
        return tag.equalsIgnoreCase(CONJ);
    }

    public boolean isDet() {
        return tag.equalsIgnoreCase(DET);
    }

    public boolean isNoun() {
        return tag.equalsIgnoreCase(NOUN);
    }

    public boolean isNum() {
        return tag.equalsIgnoreCase(NUM);
    }

    public boolean isPron() {
        return tag.equalsIgnoreCase(PRON);
    }

    public boolean isPrt() {
        return tag.equalsIgnoreCase(PRT);
    }

    public boolean isVerb() {
        return tag.equalsIgnoreCase(VERB);
    }

    public boolean isUnknown() {
        return tag.equalsIgnoreCase(X);
    }

    public boolean isCc() {
        return tag.equalsIgnoreCase(CC);
    }

    public boolean isCd() {
        return tag.equalsIgnoreCase(CD);
    }

    public boolean isDt() {
        return tag.equalsIgnoreCase(DT);
    }

    public boolean isEx() {
        return tag.equalsIgnoreCase(EX);
    }

    public boolean isFw() {
        return tag.equalsIgnoreCase(FW);
    }

    public boolean isIn() {
        return tag.equalsIgnoreCase(IN);
    }

    public boolean isJj() {
        return tag.equalsIgnoreCase(JJ);
    }

    public boolean isJjr() {
        return tag.equalsIgnoreCase(JJR);
    }

    public boolean isJjs() {
        return tag.equalsIgnoreCase(JJS);
    }

    public boolean isLs() {
        return tag.equalsIgnoreCase(LS);
    }

    public boolean isMd() {
        return tag.equalsIgnoreCase(MD);
    }

    public boolean isNn() {
        return tag.equalsIgnoreCase(NN);
    }

    public boolean isNnp() {
        return tag.equalsIgnoreCase(NNP);
    }

    public boolean isNnps() {
        return tag.equalsIgnoreCase(NNPS);
    }

    public boolean isNns() {
        return tag.equalsIgnoreCase(NNS);
    }

    public boolean isPdt() {
        return tag.equalsIgnoreCase(PDT);
    }

    public boolean isPos() {
        return tag.equalsIgnoreCase(POS);
    }

    public boolean isPrp() {
        return tag.equalsIgnoreCase(PRP);
    }

    public boolean isPrp$() {
        return tag.equalsIgnoreCase(PRP$);
    }

    public boolean isRb() {
        return tag.equalsIgnoreCase(RB);
    }

    public boolean isRbr() {
        return tag.equalsIgnoreCase(RBR);
    }

    public boolean isRbs() {
        return tag.equalsIgnoreCase(RBS);
    }

    public boolean isRp() {
        return tag.equalsIgnoreCase(RP);
    }

    public boolean isSym() {
        return tag.equalsIgnoreCase(SYM);
    }

    public boolean isTo() {
        return tag.equalsIgnoreCase(TO);
    }

    public boolean isUh() {
        return tag.equalsIgnoreCase(UH);
    }

    public boolean isVb() {
        return tag.equalsIgnoreCase(VB);
    }

    public boolean isVbd() {
        return tag.equalsIgnoreCase(VBD);
    }

    public boolean isVbg() {
        return tag.equalsIgnoreCase(VBG);
    }

    public boolean isVbn() {
        return tag.equalsIgnoreCase(VBN);
    }

    public boolean isVbp() {
        return tag.equalsIgnoreCase(VBP);
    }

    public boolean isVbz() {
        return tag.equalsIgnoreCase(VBZ);
    }

    public boolean isWdt() {
        return tag.equalsIgnoreCase(WDT);
    }

    public boolean isWp() {
        return tag.equalsIgnoreCase(WP);
    }

    public boolean isWp$() {
        return tag.equalsIgnoreCase(WP$);
    }

    public boolean isWrb() {
        return tag.equalsIgnoreCase(WRB);
    }

    public boolean isOther() {
        return OTHER.contains(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        POS pos = (POS) o;

        if (!term.equals(pos.term)) return false;
        return tag.equals(pos.tag);
    }

    @Override
    public int hashCode() {
        int result = term.hashCode();
        result = 31 * result + tag.hashCode();
        return result;
    }

    public String toString() {
        return term + "_" + tag + "_" + prob;
    }

    public static POS parseString(String s) {
        String[] splitByUnderscore = s.split("_");
        return new POS(splitByUnderscore[0], splitByUnderscore[1], Double.parseDouble(splitByUnderscore[2]));
    }
}
