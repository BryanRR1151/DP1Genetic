package app.model;

import static java.lang.Math.abs;

public class Node {
    public int x;
    public int y;
    public boolean state;
    public boolean destination;
    public Node(){

    }
    public Node(Node n){
        this.x = n.x;
        this.y = n.y;
        this.state = n.state;
        this.destination = n.destination;
    }
    public int distance(Node n) {
        int d = abs(this.x - n.x) + abs(this.y - n.y);
        return d;
    }
    @Override
    public boolean equals(Object obj) {
        boolean eq = false;
        Node n = (Node) obj;
        if(this.x == n.x && this.y == n.y){
            eq = true;
        }
        return eq;
    }
}
