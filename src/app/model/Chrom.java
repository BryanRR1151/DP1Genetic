package app.model;

import java.util.ArrayList;

public class Chrom {
    public Node from;
    public Node to;
    public int direction;
    public Chrom(){
        this.from = new Node();
        this.to = new Node();
    }
    public Chrom(Chrom c){
        this.from = new Node(c.from);
        this.to = new Node(c.to);
        this.direction = c.direction;
    }
}
