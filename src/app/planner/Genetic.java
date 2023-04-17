package app.planner;

import app.model.*;
import app.model.Package;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Genetic {
    static final int GENERATIONS = 100;
    static final int POPULATION = 100;
    static final int PARENTS = POPULATION*50/100;
    static final double MUTATE_CHANCE = 20;
    static final double IGNORE_CHANCE = 10;
    public boolean notBlocked(Environment env, Node from, Node to) {
        boolean valid = true;
        for(Blockage b : env.blockages) {
            //Basicamente, busca que no esten en el arreglo de blockeos
            //Es doble sentido
            //Si no esta, falso, se niega a verdadero
            //Si esta, verdadero, se niega a falso y se rompe la iteracion
            valid = !((b.from.equals(from) && b.to.equals(to) || (b.from.equals(to) && b.to.equals(from))));
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
            }
        }
        return valid;
    }
    public int rollValidMove(Environment env, Node location, Node to){
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
            valid = checkValid(env, node) && notBlocked(env, location, node) && (node.distance(to) < location.distance(to) || rand.nextInt(10000) < IGNORE_CHANCE*100);
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
    public int pickVehicle(ArrayList<Vehicle> vehicles, Package pack){
        int i = 0, j = -1;
        int best = 240, newBest;
        if(vehicles.size() > 1){
            for(Vehicle v : vehicles) {
                newBest = v.location.distance(pack.location);
                if(best > newBest && (v.state == 0 || v.state == 2) && v.carry > 0){
                    best = newBest;
                    j = i;
                }
                i++;
            }
        }else {
            j=0;
        }
        return j;
    }
    public ArrayList<Solution> initPopulation(Environment env, ArrayList<Vehicle> vehicles, Package pack){
        ArrayList<Solution> population = new ArrayList<>();
        Solution solution;
        int i, v;
        v = pickVehicle(vehicles, pack);
        for(i=0; i < POPULATION; i++){
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
        ArrayList<Solution> newPopulation = new ArrayList<>();
        newPopulation.addAll(population);
        Solution newSolution;
        Node node;
        Random rand = new Random();
        int i;
        for(Solution s : newPopulation){
            if(rand.nextInt(10000) < 100 * MUTATE_CHANCE){
                newSolution = new Solution();
                i = rollValidIndex(s.chroms.size());
                newSolution.chroms.addAll(s.chroms.subList(0, i));
                node = new Node(newSolution.chroms.get(i-1).to);
                newSolution.chroms.addAll(getNewSolution(env, node, pack.location).chroms);
                newSolution.vehicle = s.vehicle;
            }
        }
        return newPopulation;
    }
    public ArrayList<Solution> getParents (ArrayList<Solution> population){
        Collections.sort(population);
        ArrayList<Solution> parents = new ArrayList<>();
        parents.addAll(population.subList(0, PARENTS));
        return parents;
    }
    public Solution getBestRoute(Environment env, ArrayList<Vehicle> vehicles, Package pack) {
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
