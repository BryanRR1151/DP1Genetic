package app.model;
import java.util.ArrayList;

public class Environment {
    public ArrayList<Node> nodes;
    public ArrayList<Blockage> blockages;
    public String date;
    public String time;
    public Environment() {
        this.nodes = new ArrayList<>();
        this.blockages = new ArrayList<>();
    }
}
