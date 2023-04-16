package app.model;

import javax.swing.*;

public class Vehicle {
    public static int quantity1;
    public static int quantity2;
    public int id;
    public double speed;
    public String name;
    public double cost;
    public Node location;
    public Solution plan;
    public int carry;
    public Vehicle(){
        this.location = new Node();
        this.plan = new Solution();
    }
}
