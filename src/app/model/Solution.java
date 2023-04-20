package app.model;

import java.util.ArrayList;

public class Solution implements Comparable<Solution>{
    static final double W_COST = 0.5;
    static final int W_LATE = Integer.MAX_VALUE;
    static final double W_CYOVER = 1000;
    static final double W_EXCESS = 10;
    public static ArrayList<Vehicle> vehicles;
    public static Package pack;
    public static int currentTime;
    public ArrayList<Chrom> chroms;
    public int vehicle;
    public double fitness;
    public boolean late = false;
    public Solution() {
        this.chroms = new ArrayList<>();
    }
    public double fitness() {
        Vehicle v = vehicles.get(this.vehicle);
        double cost = v.cost * this.chroms.size();
        double time = this.chroms.size() / v.speed / 60;
        int late = 0;
        int overtime = 0;
        if((currentTime + time)/480 != vehicles.get(this.vehicle).turn){
            overtime = (int)(currentTime + time) % 480;
        }
        if(pack.deadline < currentTime + time){
            late = 1;
            this.late = true;
        }
        int carryOver = Integer.max(pack.demand - v.carry, 0);
        int excess = Integer.max(v.carry - pack.demand, 0);
        this.fitness = cost * W_COST + late * W_LATE + carryOver * W_CYOVER + excess * W_EXCESS + overtime * W_COST * 5;
        return this.fitness;
    }
    public boolean better(Solution sol) {
        return this.fitness() < sol.fitness();
    }
    @Override
    public int compareTo(Solution solution) {
        //  Ascendente
        return (int)(this.fitness() - solution.fitness());
        //  Descendente
        // return (int)(solution.fitness() - this.fitness());
    }
}
