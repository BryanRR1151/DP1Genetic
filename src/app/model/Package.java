package app.model;

public class Package {
    public Node location;
    public String time;
    public String deadline;
    public String date;
    public Package(){
        this.location = new Node();
    }
}
