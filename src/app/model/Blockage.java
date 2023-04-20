package app.model;

public class Blockage {
    public int id;
    public Node node;
    public int start;
    public int end;
    public Blockage() {
        this.node = new Node();
    }
}
