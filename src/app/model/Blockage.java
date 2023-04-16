package app.model;

public class Blockage {
    public Node from;
    public Node to;
    public Blockage() {
        this.from = new Node();
        this.to = new Node();
    }
}
