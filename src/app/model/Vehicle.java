package app.model;

public class Vehicle {
    public double speed;
    public double cost;
    public int capacity;
    public int id;
    public String type;
    public Node location;
    public Solution plan;
    public int step;
    // 0 - Libre
    // 1 - Ocupado
    // 2 - Regresando
    // 3 - Deshabilitado
    public int state;
    public int carry;
    public Package pack;
    public Vehicle(String type){
        this.location = new Node();
        this.location.x = 0;
        this.location.y = 0;
        this.state = 0;
        this.step = 0;
        this.type = type;
        if(type.equals("Auto")){
            this.carry = 25;
            this.capacity = 25;
            this.cost = 20;
            this.speed = 30;
        }else {
            this.carry = 4;
            this.capacity = 4;
            this.cost = 100;
            this.speed = 60;
        }
    }

    public void refill(){
        this.carry = this.capacity;
    }
}
