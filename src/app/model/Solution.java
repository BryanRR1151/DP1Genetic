package app.model;

import java.util.ArrayList;

public class Solution implements Comparable<Solution>{
    public ArrayList<Chrom> chroms;
    public int vehicle;
    public Solution() {
        this.chroms = new ArrayList<>();
    }
    public double fitness() {
        return chroms.size();
    }
    public boolean better(Solution sol) {
        boolean better = false;
        if (this.fitness() < sol.fitness()){
            better = true;
        }
        return better;
    }
    @Override
    public int compareTo(Solution solution) {
        //  Ascendente
        return (int)(this.fitness() - solution.fitness());
        //  Descendente
        // return (int)(solution.fitness() - this.fitness());
    }
}
