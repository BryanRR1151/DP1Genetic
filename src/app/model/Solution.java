package app.model;

import java.util.ArrayList;

public class Solution implements Comparable<Solution>{
    public ArrayList<Chrom> chroms;
    public Solution() {
        this.chroms = new ArrayList<>();
    }
    @Override
    public int compareTo(Solution solution) {
        //  For Ascending order
        return this.chroms.size() - solution.chroms.size();

        // For Descending order do like this
        // return compareage-this.studentage;
        }
}
