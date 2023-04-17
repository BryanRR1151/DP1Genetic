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
    public ArrayList<Integer> checkPackage(ArrayList<Package> packages, int minutes){
        ArrayList<Integer> list = new ArrayList<>();
        int i = 0;
        for(Package p : packages){
            if(p.time == minutes){
                list.add(i);
            }
            i++;
        }
        return list;
    }
    public void moveVehicles(Environment env, ArrayList<Vehicle> vehicles){
        Genetic genetic = new Genetic();
        ArrayList<Vehicle> newVehicles;
        Node start;
        String state = "";
        for(Vehicle v : vehicles){
            if(v.state == 1 || v.state == 2) {
                if(v.step < v.plan.chroms.size()){
                    switch (v.state){
                        case 1: state = "ENTREGANDO"; break;
                        case 2: state = "REGRESANDO"; break;
                    }
                    v.location.x = v.plan.chroms.get(v.step).to.x;
                    v.location.y = v.plan.chroms.get(v.step).to.y;
                    System.out.println(v.type + " #" + v.id + ": (" + v.plan.chroms.get(v.step).from.x + ", " + v.plan.chroms.get(v.step).from.y + ") se fue a (" + v.location.x + ", " + v.location.y + ") - " + state);
                    v.step++;
                    if(v.step == v.plan.chroms.size()){
                        start = new Node();
                        System.out.println(v.type + " #" + v.id + ": Llegó a su destino");
                        start.x = 0;
                        start.y = 0;
                        Package p = new Package();
                        p.location = start;
                        if(v.state != 2){
                            newVehicles = new ArrayList<>();
                            newVehicles.add(v);
                            v.state = 2;
                            v.plan = genetic.getBestRoute(env, newVehicles, p);
                            v.step = 0;
                        }else {
                            v.state = 0;
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
                pack.idCustomer = idCustomer;
                packages.add(pack);
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
                vehicle.state = 0;
                vehicle.type = v[3];
                vehicle.carry = 50;
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
        for (int i = 0; i < inputFiles.length; i++) {
            File file = new File(inputDirectory + "/" + inputFiles[i]);
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String blockStr = scan.nextLine();
                String[] b = blockStr.split(",");
                Blockage blockage = new Blockage();
                for (int j = 1; j < b.length-2; j+=2) {
                    blockage.from.x = Integer.parseInt(b[j]);
                    blockage.from.y = Integer.parseInt(b[j+1]);
                    blockage.to.x = Integer.parseInt(b[j+2]);
                    blockage.to.y = Integer.parseInt(b[j+3]);
                }
                blockages.add(blockage);
            }
            scan.close();
        }
        return blockages;
    }
    public String timeString(int minutes) {
        String time = String.format("%02d", (minutes/60) % 24) + ":" + String.format("%02d", minutes % 60);
        return time;
    }
    public void Simulate(){
        Environment env = new Environment();
        Genetic genetic = new Genetic();
        Vehicle.capacity1 = 40;
        Vehicle.speed2 = 30;
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
        try {
            packages = importPackages();
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
            env.blockages = blockages;
        } catch(Exception ex) {
            System.out.println("Error with file importing");
            System.exit(0);
        }
        //Esto esta simulando un dia
        for (i=0; i<DIA * 1.07; i++){
                System.out.println("Son las " + timeString(i));
                list = checkPackage(packages, i);
                for(int x : list) {
                    System.out.println("Se recibio un pedido");
                    solution = genetic.getBestRoute(env, vehicles, packages.get(x));
                    System.out.println("Se encontro una solucion de " + solution.chroms.size() + " pasos");
                    vehicles.get(solution.vehicle).plan = solution;
                    vehicles.get(solution.vehicle).state = 1;
                    vehicles.get(solution.vehicle).step = 0;
                }
                moveVehicles(env, vehicles);
        }
    }
}
