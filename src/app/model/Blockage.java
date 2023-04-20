package app.model;

public class Blockage {
    public int id;
    public Node from;
    public Node to;
    public int start;
    public int end;
    public Blockage() {
        this.from = new Node();
        this.to = new Node();
    }
}
