package app.worker;

import app.model.*;
import app.model.Package;
import app.planner.Genetic;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

public class Worker {
    static final int DIA = 1440;
    Genetic gen = new Genetic();
    ArrayList<Integer> times = new ArrayList<>();
    public ArrayList<Integer> checkPackage(ArrayList<Package> packages, int minutes){
        ArrayList<Integer> list = new ArrayList<>();
        int i = 0;
        for(Package p : packages){
            if(p.time <= minutes && p.unassigned > 0){
                list.add(i);
            }
            i++;
        }
        return list;
    }
    public int findPackage(ArrayList<Package> packages, int idCustomer){
        int index = 0;
        for(Package p : packages){
            if(p.idCustomer == idCustomer){
                break;
            }
            index++;
        }
        if(index == packages.size()){
            index = 0;
        }
        return index;
    }
    public void moveVehicles(Environment env, ArrayList<Vehicle> vehicles, ArrayList<Package> packages){
        Genetic genetic = new Genetic();
        ArrayList<Vehicle> newVehicles;
        String state = "";
        Package p;
        int ipack;
        for(Vehicle v : vehicles){
            if(v.state == 1 || v.state == 2) {
                if(v.type.equals("Auto") && v.moved){
                    v.moved = false;
                    continue;
                }
                v.moved = true;
                if(v.step < v.plan.chroms.size()){
                    switch (v.state){
                        case 1: state = "ENTREGANDO #" +v.pack.idCustomer ; break;
                        case 2: state = "REGRESANDO"; break;
                    }
                    v.location.x = v.plan.chroms.get(v.step).to.x;
                    v.location.y = v.plan.chroms.get(v.step).to.y;
                    System.out.println(v.type + " #" + v.id + ": (" + v.plan.chroms.get(v.step).from.x + ", " + v.plan.chroms.get(v.step).from.y + ") se fue a (" + v.location.x + ", " + v.location.y + ") - " + state);
                    v.step++;
                    if(v.step == v.plan.chroms.size()){
                        ipack = findPackage(packages, v.pack.idCustomer);
                        packages.get(ipack).fullfilled += v.carry;
                        if(packages.get(ipack).fullfilled >= v.pack.demand && v.state == 1){
                            times.add(packages.get(ipack).deadline - env.time);
                            System.out.println("Pedido #" + packages.get(ipack).idCustomer + " completado");
                            packages.remove(ipack);
                        }
                        v.carry = 0;
                        System.out.println(v.type + " #" + v.id + ": Llegó a su destino");
                        if(v.state == 1){
                            p = new Package();
                            p.location.x = 45;
                            p.location.y = 30;
                            v.pack = p;
                            v.iPack = 0;
                            newVehicles = new ArrayList<>();
                            newVehicles.add(v);
                            v.state = 2;
                            v.plan = genetic.getBestRoute(env, newVehicles, p, env.time);
                            v.step = 0;
                            v.moved = false;
                        }else {
                            v.plan = null;
                            v.pack = null;
                            v.iPack = 0;
                            v.state = 0;
                            v.moved = false;
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Package> importPackages() throws FileNotFoundException {
        File inputDirectory;
        ArrayList<Package> packages = new ArrayList<>();
        inputDirectory = new File(System.getProperty("user.dir") + "/input/packages");
        String[] inputFiles = inputDirectory.list((dir, name) -> new File(dir, name).isFile());
        int j = 0;
        for (int i = 0; i < inputFiles.length; i++) {
            File file = new File(inputDirectory + "/" + inputFiles[i]);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String orderStr = scan.nextLine();
                String[] order = orderStr.split(",");
                int posX = Integer.parseInt(order[1]);
                int posY = Integer.parseInt(order[2]);
                int demand = Integer.parseInt(order[3]);
                String readyTime = order[0];
                String[] dateSections = readyTime.split(":");
                int startDate = Integer.parseInt(dateSections[0])*60 + Integer.parseInt(dateSections[1]);
                int dueDate = startDate + Integer.parseInt(order[5])*60;
                int idCustomer = Integer.parseInt(order[4]);

                Package pack = new Package();
                pack.time = startDate;
                pack.deadline = dueDate;
                pack.location.x = posX;
                pack.location.y = posY;
                pack.demand = demand;
                pack.unassigned = demand;
                pack.fullfilled = 0;
                pack.idCustomer = j;
                pack.originalTime = startDate;
                packages.add(pack);
                j++;
            }
            scan.close();
        }
        return packages;
    }
    public ArrayList<Vehicle> importVehicles() throws FileNotFoundException {
        File inputDirectory;
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        inputDirectory = new File(System.getProperty("user.dir") + "/input/vehicles");
        String[] inputFiles = inputDirectory.list((dir, name) -> new File(dir, name).isFile());
        for (int i = 0; i < inputFiles.length; i++) {
            File file = new File(inputDirectory + "/" + inputFiles[i]);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String vehicleStr = scan.nextLine();
                String[] v = vehicleStr.split(",");
                Vehicle vehicle = new Vehicle(v[3]);
                vehicle.id = Integer.parseInt(v[0]);
                vehicles.add(vehicle);
            }
            scan.close();
        }
        return vehicles;
    }

    public ArrayList<Blockage> importBlockages() throws FileNotFoundException {
        File inputDirectory;
        ArrayList<Blockage> blockages = new ArrayList<>();
        inputDirectory = new File(System.getProperty("user.dir") + "/input/blockages");
        String[] inputFiles = inputDirectory.list((dir, name) -> new File(dir, name).isFile());
        int k = 0;
        for (int i = 0; i < inputFiles.length; i++) {
            File file = new File(inputDirectory + "/" + inputFiles[i]);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String blockStr = scan.nextLine();
                String[] b = blockStr.split(",");
                String[] start = b[0].split("-")[0].split(":");
                String[] end = b[0].split("-")[1].split(":");
                int tStart = (Integer.parseInt(start[0]) - 1) * DIA + Integer.parseInt(start[1]) * 60 + Integer.parseInt(start[2]);
                int tEnd = (Integer.parseInt(end[0]) - 1) * DIA + Integer.parseInt(end[1]) * 60 + Integer.parseInt(end[2]);
                for (int j = 1; j < b.length-1; j+=2) {
                    Blockage blockage = new Blockage();
                    blockage.id = k;
                    blockage.start = tStart;
                    blockage.end = tEnd;
                    blockage.node.x = Integer.parseInt(b[j]);
                    blockage.node.y = Integer.parseInt(b[j+1]);
                    blockages.add(blockage);
                    k++;
                }
            }
            scan.close();
        }
        return blockages;
    }
    public ArrayList<Integer> importFaults() throws FileNotFoundException {
        File inputDirectory;
        ArrayList<Integer> faults = new ArrayList<>();
        inputDirectory = new File(System.getProperty("user.dir") + "/input/faults");
        String[] inputFiles = inputDirectory.list((dir, name) -> new File(dir, name).isFile());
        for (int i = 0; i < inputFiles.length; i++) {
            File file = new File(inputDirectory + "/" + inputFiles[i]);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String blockStr = scan.nextLine();
                String[] b = blockStr.split(",");
                String[] start = b[0].split("-")[0].split(":");
                String type = b[1];
                int tStart = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
                faults.add(tStart);
                faults.add(Integer.parseInt(type));
            }
            scan.close();
        }
        return faults;
    }
    public String timeString(int minutes) {
        return String.format("%02d", (minutes/60) % 24) + ":" + String.format("%02d", minutes % 60);
    }
    public void removeBlockage(Environment env, int id){
        for(int i=0; i<env.blockages.size(); i++){
            if(env.blockages.get(i).id == id){
                env.blockages.remove(i);
                break;
            }
        }
    }
    public void restartAll(Environment env, ArrayList<Package> packages, ArrayList<Vehicle> vehicles){
        ArrayList<Vehicle> newVehicles;
        for(Vehicle v : vehicles){
            if(v.state == 1 || v.state == 2){
                for(Chrom c : v.plan.chroms){
                    for(Blockage b : env.blockages){
                        if(c.from.equals(b.node)){
                            newVehicles = new ArrayList<>();
                            newVehicles.add(v);
                            v.plan = gen.getBestRoute(env, newVehicles, v.pack, env.time);
                            v.step = 0;
                            v.moved = false;
                        }
                    }
                }
            }
        }
    }
    public void checkBlockage(Environment env, ArrayList<Package> packages, ArrayList<Vehicle> vehicles){
        boolean update = false;
        int i;
        for(i = 0; i < env.blockList.size(); i++){
            if(env.blockList.get(i).start == env.time){
                System.out.println("Empezó el bloqueo " + env.blockList.get(i).id);
                env.blockages.add(env.blockList.get(i));
                update = true;
            }
            if(env.blockList.get(i).end == env.time){
                System.out.println("Terminó el bloqueo " + env.blockList.get(i).id);
                removeBlockage(env, env.blockList.get(i).id);
                env.blockList.remove(i);
                i--;
                update = true;
            }
        }
        if(update){
            System.out.println("Replanificando...");
            restartAll(env, packages, vehicles);
        }
    }
    public void killVehicle(Environment env, ArrayList<Package> packages, ArrayList<Vehicle> vehicles, int type){
        int ipack;
        for(Vehicle v : vehicles){
            if(v.state == 1){
                v.location.x = 45;
                v.location.y = 30;
                ipack = findPackage(packages, v.pack.idCustomer);
                if(packages.get(ipack).idCustomer != v.iPack){
                    ipack = ipack;
                }
                packages.get(ipack).unassigned += v.carry;
                packages.get(ipack).time = env.time;
                v.state = type;
                v.iPack = 0;
                v.pack = null;
                v.plan = null;
                v.step = 0;
                v.moved = false;
                System.out.println(v.type + " #" + v.id + " averiado, reasignando el Pedido #" + packages.get(ipack).idCustomer);
                break;
            }
        }
    }
    public void repairVehicles(ArrayList<Vehicle> vehicles){
        for(Vehicle v : vehicles){
            if(v.state == 3){
                v.state = 0;
            }
        }
    }
    public void buyVehicle(ArrayList<Vehicle> vehicles){
        for(Vehicle v: vehicles){
            switch (v.state){
                case 4: v.state = 5; break;
                case 5: v.state = 0; break;
            }
        }
    }
    public void wipeAll(ArrayList<Vehicle> vehicles){
        for(Vehicle v: vehicles){
            if(v.state != 1 && v.state != 2){
                v.plan = null;
                v.pack = null;
                v.step = 0;
                v.iPack = 0;
            }
        }
    }
    public void Simulate(){
        Environment env = new Environment();
        Genetic genetic = new Genetic();
        env.date = "15/04/23";
        env.time = 0;
        Node node;
        Solution solution;
        int i, j;
        for(i=0; i < 71; i++){
            for(j=0; j < 51; j++){
                node = new Node();
                node.x = i;
                node.y = j;
                env.nodes.add(node);
            }
        }

        ArrayList<Integer> list;
        ArrayList<Package> packages = new ArrayList<>();
        ArrayList<Package> init = new ArrayList<>();
        try {
            packages = importPackages();
            init = importPackages();
        } catch(Exception ex) {
            System.out.println("Error with file importing");
            System.exit(0);
        }
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        try {
            vehicles = importVehicles();
        } catch(Exception ex) {
            System.out.println("Error with file importing");
            System.exit(0);
        }
        ArrayList<Blockage> blockages;
        try {
            blockages = importBlockages();
            env.blockList = blockages;
        } catch(Exception ex) {
            System.out.println("Error with file importing");
            System.exit(0);
        }
        ArrayList<Integer> faults = new ArrayList<>();
        try {
            faults = importFaults();
        } catch(Exception ex) {
            System.out.println("Error with file importing");
            System.exit(0);
        }
        boolean first = true;
        //Esto esta simulando un dia
        for (i=0; i<DIA * 30; i++){
                env.time = i;
                if(i%DIA == 0 && !first){
                    wipeAll(vehicles);
                    buyVehicle(vehicles);
                    System.out.println("Es el dia " + i/DIA + 1);
                    for(Package p : init){
                        p.idCustomer += 150;
                        p.time += DIA;
                        p.deadline += DIA;
                        p.originalTime += DIA;
                        p.fullfilled = 0;
                        p.unassigned = p.demand;
                    }
                    for(int k = 0; k < faults.size() - 1; k += 2){
                        faults.set(k, faults.get(k) + DIA);
                    }
                    packages.addAll(init);
                }
                first = false;
                System.out.println("Son las " + timeString(i));
                checkBlockage(env, packages, vehicles);
                if(i % 480 == 0){
                    repairVehicles(vehicles);
                }
                for(int k = 0; k < faults.size() - 1; k += 2){
                    if(faults.get(k) == i){
                        killVehicle(env, packages, vehicles, faults.get(k + 1));
                    }
                }
                list = checkPackage(packages, i);
                for(int x=0; x < list.size(); x++) {
                    System.out.println("Se recibio un pedido");
                    solution = genetic.getBestRoute(env, vehicles, packages.get(list.get(x)), i);
                    if(solution.late){
                        System.out.println("Colapso Logístico");
                        return;
                    }
                    System.out.println("Se encontro una solucion de " + solution.chroms.size() + " pasos");
                    vehicles.get(solution.vehicle).plan = solution;
                    vehicles.get(solution.vehicle).state = 1;
                    vehicles.get(solution.vehicle).step = 0;
                    vehicles.get(solution.vehicle).pack = new Package(packages.get(list.get(x)));
                    vehicles.get(solution.vehicle).iPack = packages.get(x).idCustomer;
                    vehicles.get(solution.vehicle).turn = i / 480;
                    vehicles.get(solution.vehicle).carry = Integer.min(vehicles.get(solution.vehicle).capacity, packages.get(list.get(x)).unassigned);
                    packages.get(list.get(x)).unassigned -= vehicles.get(solution.vehicle).carry;
                    if(packages.get(list.get(x)).unassigned > 0){
                        list.add(list.get(x));
                    }
                }
                moveVehicles(env, vehicles, packages);
        }
        double sum = 0;
        for(Integer t : times){
            sum += t;
        }
        sum = sum/times.size();
        System.out.println("En promedio, el tiempo restante por pedido es: " + sum);
    }
}
