package app.model;

public class Package {
    public Node location;
    public int idCustomer;
    public int time;
    public int demand;
    public int fullfilled;
    public int unassigned;
    public int deadline;
    public Package(){
        this.location = new Node();
        this.fullfilled = 0;
    }
    public Package(Package p){
        this.location = new Node(p.location);
        this.idCustomer = p.idCustomer;
        this.time = p.time;
        this.demand = p.demand;
        this.deadline = p.deadline;
        this.fullfilled = p.fullfilled;
        this.unassigned = p.unassigned;
    }
}
