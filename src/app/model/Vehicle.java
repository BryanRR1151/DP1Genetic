package app.model;

public class Vehicle {
    public static int quantity1;
    public static int quantity2;
    public static double speed1;
    public static double speed2;
    public static double cost1;
    public static double cost2;
    public static int capacity1;
    public static int capacity2;
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
    public Vehicle(String type){
        this.location = new Node();
        this.location.x = 0;
        this.location.y = 0;
        this.state = 0;
        this.step = 0;
        if(type == "Carro"){
            this.carry = capacity1;
        }else {
            this.carry = capacity2;
        }
    }
}
