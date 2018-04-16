package chatbot.engine.math;

public class DualKey<K1, K2> {
    private K1 k1;
    private K2 k2;

    public DualKey(K1 k1, K2 k2) {
        this.k1 = k1;
        this.k2 = k2;
    }

    @Override
    public boolean equals(Object obj) {
        DualKey castObj = (DualKey) obj;
        return castObj.k1.equals(this.k1) && castObj.k2.equals(this.k2);
    }

    @Override
    public int hashCode() {
        int result = k1.hashCode();
        result = 31 * result + k2.hashCode();
        return result;
    }
}