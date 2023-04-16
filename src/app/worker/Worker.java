package app.worker;

import app.model.*;
import app.model.Package;
import app.planner.Genetic;

import java.util.ArrayList;

public class Worker {
    public void Simulate(){
        Environment env = new Environment();
        Genetic genetic = new Genetic();
        env.date = "15/04/23";
        env.time = "00:00";
        Node node;
        Solution solution = new Solution();
        int i, j, k = 0, x = 0, y = 0, nx = 0, ny = 0;
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
        //Esto esta simulando un dia
        for (i=0; i<1; i++){
            for (j=0; j<30; j++){
                System.out.println("Son las " + i + ":" + j);
                //se tiene un pedido que se registro a esta hora
                if(i == 0 && j == 3){
                    Package pack = new Package();
                    pack.location.x = 8;
                    pack.location.y = -8;
                    solution = genetic.getBestRoute(env,null,pack);
                    System.out.println("Se recibio un paquete");
                    start = true;
                }
                if(start && k < solution.chroms.size()){
                    System.out.println("De (" + solution.chroms.get(k).from.x + ", " + solution.chroms.get(k).from.y + ") se fue a (" + solution.chroms.get(k).to.x + ", " + solution.chroms.get(k).to.y + ")");
                    k++;
                }
            }
        }
    }
}
