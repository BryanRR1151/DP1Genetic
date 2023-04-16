package app.planner;

import app.model.*;
import app.model.Package;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.function.Predicate;


public class Genetic {
    public boolean checkValid(Environment env, Node node){
        boolean valid = false;
        for(Node n : env.nodes){
            if(node.equals(n)){
                valid = true;
            }
        }
        return valid;
    }
    public int rollValid(Environment env, Node location){
        Random rand = new Random();
        Node node;
        boolean valid = false;
        int num = -1;
        while (!valid){
            num = rand.nextInt(4);
            node = new Node(location);
            switch (num) {
                case 0: { //Derecha
                    node.x++;
                    break;
                }
                case 1: { //Izquierda
                    node.x--;
                    break;
                }
                case 2: { //Arriba
                    node.y++;
                    break;
                }
                case 3: { //Abajo
                    node.y--;
                    break;
                }
            }
            valid = checkValid(env, node);
        }

        return num;
    }
    public ArrayList<Solution> initPopulation(Environment env, ArrayList<Vehicle> vehicles, Package pack){
        ArrayList<Solution> population = new ArrayList<>();
        Solution solution;
        Chrom chrom;
        Vehicle vehicle = new Vehicle();
        vehicle.location = new Node();
        int i, num;
        for(i=0; i < 100; i++){
            vehicle.location.x = 0;
            vehicle.location.y = 0;
            solution = new Solution();
            while (true){
                if(!vehicle.location.equals(pack.location)){
                    chrom = new Chrom();
                    chrom.from.x = vehicle.location.x;
                    chrom.from.y = vehicle.location.y;
                    chrom.to.x = chrom.from.x;
                    chrom.to.y = chrom.from.y;
                    num = rollValid(env, vehicle.location);
                    switch (num){
                        case 0:{ //Derecha
                            vehicle.location.x++;
                            chrom.to.x++;
                            break;
                        }
                        case 1: { //Izquierda
                            vehicle.location.x--;
                            chrom.to.x--;
                            break;
                        }
                        case 2: { //Arriba
                            vehicle.location.y++;
                            chrom.to.y++;
                            break;
                        }
                        case 3: { //Abajo
                            vehicle.location.y--;
                            chrom.to.y--;
                            break;
                        }
                    }
                    chrom.direction = num;
                    solution.chroms.add(chrom);
                }else {
                    population.add(solution);
                    break;
                }
            }
        }
        return population;
    }
    public Solution getFit(ArrayList<Solution> population) {
        Solution best = population.get(0);
        for(Solution s : population){
            if(best.chroms.size() > s.chroms.size()){
                best.chroms = new ArrayList<>();
                best.chroms.addAll(s.chroms);
            }
        }
        return best;
    }
    public ArrayList<Solution> cross(Solution father, Solution mother){
        ArrayList<Solution> children = new ArrayList<>();
        Solution child1 = new Solution();
        Solution child2 = new Solution();
        int i, j, fsize =father.chroms.size(), msize =mother.chroms.size();
        for(i=1; i < fsize - 1; i++){
            for(j=1; j < msize - 1; j++){
                if(father.chroms.get(i).to.equals(mother.chroms.get(j).to) && father.chroms.get(i).from.equals(mother.chroms.get(j).from)){
                        child1.chroms.addAll(father.chroms.subList(0, i));
                        child1.chroms.addAll(mother.chroms.subList(j, msize));
                        child2.chroms.addAll(mother.chroms.subList(0, j));
                        child2.chroms.addAll(father.chroms.subList(i, fsize));
                        children.add(child1);
                        children.add(child2);
                        return children;
                }
            }
        }
        return null;
    }
    public ArrayList<Solution> crossPopulation(ArrayList<Solution> population){
        ArrayList<Solution> newPopulation = new ArrayList<>();
        ArrayList<Solution> children;
        Random rand = new Random();
        int i, f, m;
        for(i=0; i<50; i++){
            while (true){
                f = rand.nextInt(75);
                while(true){
                    m = rand.nextInt(75);
                    if(m != f){
                        break;
                    }
                }
                children = cross(population.get(f), population.get(m));
                if(children != null){
                    newPopulation.add(children.get(0));
                    newPopulation.add(children.get(1));
                    break;
                }
            }
        }
        return newPopulation;
    }
    public ArrayList<Solution> mutate (ArrayList<Solution> population, Environment env, Package pack){
        ArrayList<Solution> newPopulation = new ArrayList<>();
        newPopulation.addAll(population);
        Solution newSolution;
        Node node;
        Chrom chrom;
        Random rand = new Random();
        int i = 0, num;
        for(Solution s : newPopulation){
            if(rand.nextInt(100) < 5){
                newSolution = new Solution();
                while(true){
                    i = rand.nextInt(s.chroms.size()-1);
                    if(i > 0){
                        break;
                    }
                }
                newSolution.chroms.addAll(s.chroms.subList(0, i));
                node = new Node(newSolution.chroms.get(i-1).to);
                while (true){
                    if(!node.equals(pack.location)){
                        chrom = new Chrom();
                        chrom.from.x = node.x;
                        chrom.from.y = node.y;
                        chrom.to.x = chrom.from.x;
                        chrom.to.y = chrom.from.y;
                        num = rollValid(env, node);
                        switch (num){
                            case 0:{ //Derecha
                                node.x++;
                                chrom.to.x++;
                                break;
                            }
                            case 1: { //Izquierda
                                node.x--;
                                chrom.to.x--;
                                break;
                            }
                            case 2: { //Arriba
                                node.y++;
                                chrom.to.y++;
                                break;
                            }
                            case 3: { //Abajo
                                node.y--;
                                chrom.to.y--;
                                break;
                            }
                        }
                        chrom.direction = num;
                        newSolution.chroms.add(chrom);
                    }else {
                        break;
                    }
                }
            }
        }
        return newPopulation;
    }
    public ArrayList<Solution> getParents (ArrayList<Solution> population){
        Collections.sort(population);
        ArrayList<Solution> parents = new ArrayList<>();
        parents.addAll(population.subList(0, 75));
        return parents;
    }
    public Solution getBestRoute(Environment env, ArrayList<Vehicle> vehicles, Package pack) {
        ArrayList<Solution> population = initPopulation(env, vehicles, pack);
        Solution best = getFit(population);
        ArrayList<Solution> parents = getParents(population);
        ArrayList<Solution> newPopulation;
        Solution newBest;
        int i;
        for(i=0; i < 100; i++){
            newPopulation = crossPopulation(parents);
            newPopulation = mutate(newPopulation, env, pack);
            newBest = getFit(newPopulation);
            if(best.chroms.size() > newBest.chroms.size()){
                best = newBest;
                population = newPopulation;
            }
        }
        return best;
    }
}
