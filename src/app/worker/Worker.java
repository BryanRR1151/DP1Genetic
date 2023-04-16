package app.worker;

import app.model.*;
import app.model.Package;
import app.planner.Genetic;
import java.util.ArrayList;

public class Worker {
    public ArrayList<Integer> checkPackage(ArrayList<Package> packages, int hour, int minute){
        ArrayList<Integer> list = new ArrayList<>();
        String time = String.format("%02d", hour) + ":" + String.format("%02d", minute);
        int i = 0;
        for(Package p : packages){
            if(p.time.equals(time)){
                list.add(i);
            }
            i++;
        }
        return list;
    }
    public void moveVehicles(ArrayList<Vehicle> vehicles){
        for(Vehicle v : vehicles){
            if(v.state == 1) {
                if(v.step < v.plan.chroms.size()){
                    System.out.println("De (" + v.plan.chroms.get(v.step).from.x + ", " + v.plan.chroms.get(v.step).from.y + ") se fue a (" + v.plan.chroms.get(v.step).to.x + ", " + v.plan.chroms.get(v.step).to.y + ")");
                    v.step++;
                    if(v.step == v.plan.chroms.size()){
                        v.state = 2;
                    }
                }
            }
        }
    }
    public void Simulate(){
        Environment env = new Environment();
        Genetic genetic = new Genetic();
        Vehicle.capacity1 = 40;
        Vehicle.speed2 = 30;
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        Vehicle v = new Vehicle("Carro");
        v.id = 1;
        v.type = "Moto";
        v.state = 0;
        vehicles.add(v);
        env.date = "15/04/23";
        env.time = "00:00";
        Node node;
        Solution solution = new Solution();
        int i, j, k = 0, p = 0;
        boolean start = false;
        for(i=-8; i < 9; i++){
            for(j=-8; j<9; j++){
                node = new Node();
                node.x = i;
                node.y = j;
                env.nodes.add(node);
            }
        }
        Blockage block = new Blockage();
        block.from.x = 0;
        block.from.y = 0;
        block.to.x = 1;
        block.to.y = 0;
        env.blockages.add(block);
        block = new Blockage();
        block.from.x = 0;
        block.from.y = 0;
        block.to.x = 0;
        block.to.y = 1;
        env.blockages.add(block);
        block = new Blockage();
        block.from.x = 0;
        block.from.y = 0;
        block.to.x = -1;
        block.to.y = 0;
        env.blockages.add(block);
        Package pack = new Package();
        pack.time = "00:03";
        pack.location.x = 8;
        pack.location.y = -8;
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Package> packages = new ArrayList<>();
        packages.add(pack);
        //Esto esta simulando un dia
        for (i=0; i<1; i++){
            for (j=0; j<30; j++){
                System.out.println("Son las " + String.format("%02d", i) + ":" + String.format("%02d", j));
                list = checkPackage(packages, i, j);
                for(int x : list) {
                    System.out.println("Se recibio un paquete");
                    solution = genetic.getBestRoute(env, vehicles, packages.get(x));
                    vehicles.get(solution.vehicle).plan = solution;
                    vehicles.get(solution.vehicle).state = 1;
                    vehicles.get(solution.vehicle).step = 0;
                }
                moveVehicles(vehicles);
            }
        }
    }
}
