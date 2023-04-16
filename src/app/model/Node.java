package app.model;

public class Node {
    public int x;
    public int y;
    public boolean state;
    public Node(){

    }
    public Node(Node n){
        this.x = n.x;
        this.y = n.y;
        this.state = n.state;
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
