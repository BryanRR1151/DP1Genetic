package app.model;

public class Package {
    public Node location;
    public int idCustomer;
    public int time;
    public int demand;
    public int deadline;
    public String date;
    public Package(){
        this.location = new Node();
    }
}
