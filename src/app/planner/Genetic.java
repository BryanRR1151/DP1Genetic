package app.planner;

import app.model.*;
import app.model.Package;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Genetic {
    static final int GENERATIONS = 100;
    static final int POPULATION = 100;
    static final int PARENTS = POPULATION*50/100;
    static final double MUTATE_CHANCE = 20;
    static final double IGNORE_CHANCE = 10;
    static ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
    static Node destination;
    public boolean notBlocked(Environment env, Node from, Node to) {
        boolean valid = true;
        for(Blockage b : env.blockages) {
            //Basicamente, busca que no esten en el arreglo de blockeos
            //Es doble sentido
            //Si no esta, falso, se niega a verdadero
            //Si esta, verdadero, se niega a falso y se rompe la iteracion
            valid = !(b.node.equals(to)) || (to.equals(destination));
            if(!valid){
                break;
            }
        }
        return valid;
    }
    public boolean checkValid(Environment env, Node node){
        boolean valid = false;
        for(Node n : env.nodes){
            if(node.equals(n)){
                valid = true;
                break;
            }
        }
        return valid;
    }
    public int rollValidMove(Environment env, Node location, Node to){
        Random rand = new Random();
        Node node;
        ArrayList<Integer> newList = new ArrayList<>(list);
        boolean valid = false;
        int num = -1, i;
        while (!valid){
            i = rand.nextInt(newList.size());
            num = newList.get(i);
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
            valid = checkValid(env, node) && notBlocked(env, location, node) && (node.distance(to) < location.distance(to) || rand.nextInt(10000) < IGNORE_CHANCE*100);
            if(!valid){
                newList.remove(i);
                if(newList.size() == 0){
                    newList = new ArrayList<>(list);
                }
            }
        }
        return num;
    }
    public Solution getNewSolution(Environment env, Node from, Node to){
        Solution solution = new Solution();
        Node localFrom = new Node(from);
        Chrom chrom;
        int num;
        while (true) {
            if(!localFrom.equals(to)) {
                chrom = new Chrom();
                chrom.from = new Node(localFrom);
                chrom.to = new Node(localFrom);
                num = rollValidMove(env, localFrom, to);
                switch (num){
                    case 0:{ //Derecha
                        localFrom.x++;
                        chrom.to.x++;
                        break;
                    }
                    case 1: { //Izquierda
                        localFrom.x--;
                        chrom.to.x--;
                        break;
                    }
                    case 2: { //Arriba
                        localFrom.y++;
                        chrom.to.y++;
                        break;
                    }
                    case 3: { //Abajo
                        localFrom.y--;
                        chrom.to.y--;
                        break;
                    }
                }
                chrom.direction = num;
                solution.chroms.add(chrom);
            }else {
                break;
            }
        }
        return solution;
    }
    public int pickVehicle(ArrayList<Vehicle> vehicles){
        Random rand = new Random();
        ArrayList<Vehicle> newVehicles = new ArrayList<>(vehicles);
        int i;
        if(vehicles.size() > 1){
            while (true){
                i = rand.nextInt(newVehicles.size());
                if(newVehicles.get(i).state == 0 || (newVehicles.get(i).state == 2 && newVehicles.get(i).carry > 0)){
                    i = newVehicles.get(i).id - 1;
                    break;
                }else {
                    newVehicles.remove(i);
                }
            }
        }else {
            i=0;
        }
        return i;
    }
    public ArrayList<Solution> initPopulation(Environment env, ArrayList<Vehicle> vehicles, Package pack){
        ArrayList<Solution> population = new ArrayList<>();
        Solution solution;
        int i, v;
        for(i=0; i < POPULATION; i++){
            v = pickVehicle(vehicles);
            solution = getNewSolution(env, vehicles.get(v).location, pack.location);
            solution.vehicle = v;
            population.add(solution);
        }
        return population;
    }
    public Solution getFit(ArrayList<Solution> population) {
        Solution best = population.get(0);
        for(Solution s : population){
            if(s.better(best)){
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
        if(fsize > 2 && msize > 2){
            for(i=1; i < fsize - 1; i++){
                for(j=1; j < msize - 1; j++){
                    if(father.chroms.get(i).to.equals(mother.chroms.get(j).to) && father.chroms.get(i).from.equals(mother.chroms.get(j).from)){
                        child1.chroms.addAll(father.chroms.subList(0, i));
                        child1.chroms.addAll(mother.chroms.subList(j, msize));
                        child1.vehicle = father.vehicle;
                        child2.chroms.addAll(mother.chroms.subList(0, j));
                        child2.chroms.addAll(father.chroms.subList(i, fsize));
                        child2.vehicle = father.vehicle;
                        children.add(child1);
                        children.add(child2);
                        return children;
                    }
                }
            }
        }else {
            children.add(father);
            children.add(mother);
            return children;
        }
        return null;
    }
    public int rollValidMother(int f) {
        Random rand = new Random();
        int m;
        while (true) {
            m = rand.nextInt(PARENTS);
            if (m != f) {
                break;
            }
        }
        return m;
    }
    public ArrayList<Solution> crossPopulation(ArrayList<Solution> population){
        ArrayList<Solution> newPopulation = new ArrayList<>();
        ArrayList<Solution> children;
        Random rand = new Random();
        int i, f, m;
        for(i = 0 ; i < POPULATION/2; i++){
            while (true){
                f = rand.nextInt(PARENTS);
                m = rollValidMother(f);
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
    public int rollValidIndex(int cap){
        Random rand = new Random();
        int i;
        while (true) {
            i = rand.nextInt(cap - 1);
            if(i > 0){
                break;
            }
        }
        return i;
    }
    public ArrayList<Solution> mutate (ArrayList<Solution> population, Environment env, Package pack){
        ArrayList<Solution> newPopulation = new ArrayList<>(population);
        destination = pack.location;
        Solution newSolution;
        Node node;
        Random rand = new Random();
        int i;
        for(Solution s : newPopulation){
            if(rand.nextInt(10000) < 100 * MUTATE_CHANCE){
                if(s.chroms.size() > 2){
                    newSolution = new Solution();
                    i = rollValidIndex(s.chroms.size());
                    newSolution.chroms.addAll(s.chroms.subList(0, i));
                    node = new Node(newSolution.chroms.get(i-1).to);
                    newSolution.chroms.addAll(getNewSolution(env, node, pack.location).chroms);
                    newSolution.vehicle = s.vehicle;
                }
            }
        }
        return newPopulation;
    }
    public ArrayList<Solution> getParents (ArrayList<Solution> population){
        Collections.sort(population);
        ArrayList<Solution> parents = new ArrayList<>(population.subList(0, PARENTS));
        return parents;
    }
    public Solution getBestRoute(Environment env, ArrayList<Vehicle> vehicles, Package pack, int minute) {
        Solution.vehicles = vehicles;
        Solution.pack = pack;
        Solution.currentTime = minute;
        ArrayList<Solution> parents;
        ArrayList<Solution> newPopulation;
        Solution newBest;
        ArrayList<Solution> population = initPopulation(env, vehicles, pack);
        Solution best = getFit(population);
        int i;
        for(i =0 ; i < GENERATIONS; i++){
            parents = getParents(population);
            newPopulation = crossPopulation(parents);
            newPopulation = mutate(newPopulation, env, pack);
            newBest = getFit(newPopulation);
            if(newBest.better(best)){
                best = newBest;
                population = newPopulation;
            }
        }
        return best;
    }
}
