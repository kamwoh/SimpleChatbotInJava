package chatbot.inputs.dataset;

public interface Dataset {

    public void shuffle();

    public Double[][] getX(int start, int end);

    public Double[][] getY(int start, int end);

    public int size();

    public int getInputSize();

    public int getClassSize();

}
