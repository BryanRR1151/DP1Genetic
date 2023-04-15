package app.planner;

import app.model.*;
import app.model.Package;
import java.util.ArrayList;


public class Genetic {
    public ArrayList<Solution> initPoblation(Environment env, ArrayList<Vehicle> vehicles, Package pack){
        ArrayList<Solution> poblation = new ArrayList<Solution>();
        Solution solution;
        Chrom chrom;
        Vehicle vehicle = new Vehicle();
        vehicle.location = new Node();
        int i;
        for(i=0; i < 10; i++){
            vehicle.location.x = 0;
            vehicle.location.y = 0;
            solution = new Solution();
            solution.chroms = new ArrayList<Chrom>();
            while (true){
                chrom = new Chrom();
                chrom.from = new Node();
                chrom.from.x = vehicle.location.x;
                chrom.from.y = vehicle.location.y;
                chrom.to = new Node();
                chrom.to.x = chrom.from.x;
                chrom.to.y = chrom.from.y;
                if(vehicle.location.x != pack.location.x){
                    if(vehicle.location.x < pack.location.x){
                        vehicle.location.x++;
                        chrom.to.x++;
                    }else {
                        vehicle.location.x--;
                        chrom.to.x--;
                    }
                }else if (vehicle.location.y != pack.location.y){
                    if(vehicle.location.y < pack.location.y){
                        vehicle.location.y++;
                        chrom.to.y++;
                    }else {
                        vehicle.location.y--;
                        chrom.to.y--;
                    }
                }else {
                    poblation.add(solution);
                    break;
                }
                solution.chroms.add(chrom);
            }
            poblation.add(solution);
        }
        return poblation;
    }
    public Solution getFit(ArrayList<Solution> poblation) {
        boolean first = true;
        Solution best = new Solution();
        for(Solution s : poblation){
            if(first || best.chroms.size() > s.chroms.size()){
                first = false;
                best.chroms = new ArrayList<Chrom>();
                best.chroms.addAll(s.chroms);
            }
        }
        return best;
    }
    public Solution getBestRoute(Environment env, ArrayList<Vehicle> vehicles, Package pack) {
        ArrayList<Solution> poblation = initPoblation(env, vehicles, pack);
        Solution best = getFit(poblation);
        return best;
    }
}
