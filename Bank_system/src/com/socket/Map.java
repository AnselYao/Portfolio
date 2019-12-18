package com.socket;

import com.SocketConstant;
import java.io.Serializable;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class Map implements Serializable{
    private int numOfNodes;

    // 需要传递给ProjectMain的参数
    private ArrayList<Node> nodesInSystem = new ArrayList<>();
    private int currentNode;
    private int[][] adjMatrix;

    private Get get = new Get();

    /**
     * 
     */
    Map() {
        // String name = "0";
        // String ip = "10.203.212.166";
        // int port = 8899;

        // Reg reger = new Reg(name, ip, port);
        // reger.testjson();
        // this.currentNode = reger.curId;
        this.currentNode = 0;

        // do {
        //     try {
        //         Thread.sleep(1000);
        //         this.nodesInSystem = this.get.GetNodes(SocketConstant.strUrl);
        //     }
        //     catch (Exception e) {
        //         //TODO: handle exception
        //     }
        // } while (this.get.stateCode != 200);
        Node node0 = new Node(0, "10.203.212.166", 8899);
        Node node1 = new Node(1, "10.203.165.109", 8899);
        // // Node node1 = new Node(1, "192.168.43.173", 8899);
        // // Node node3 = new Node(2, "192.168.43.51", 8899);
        // // Node node4 = new Node(3, "192.168.43.226", 8899);
        // Node node5 = new Node(1, "192.168.43.158", 8899);
        // Node node6 = new Node(2, "192.168.43.52", 8899);
        this.nodesInSystem.add(node0);
        this.nodesInSystem.add(node1);
        // // this.nodesInSystem.add(node3);
        // // this.nodesInSystem.add(node4);
        // this.nodesInSystem.add(node5);
        // this.nodesInSystem.add(node6);

        this.numOfNodes = this.nodesInSystem.size();
        // for (int i = 0; i < nodesInSystem.size(); ++i) {
        //     Node tmp = nodesInSystem.get(i);
        //     if (tmp.host == myIP) {
        //         this.currentNode = i;
        //     }
        // }
    }

    /**
     * 
     * @return
     */
    public ProjectMain getMain() {
        ProjectMain main = new ProjectMain();

        main.nodesInSystem = this.nodesInSystem;
        main.currentNode = currentNode;
        main.adjMatrix = new int[numOfNodes][numOfNodes];

        this.adjMatrix = new int[numOfNodes][numOfNodes];
        for (int i = 0; i < numOfNodes; ++i) {
            for (int j = 0; j < numOfNodes; ++j) {
                if (j == i) {
                    this.adjMatrix[i][j] = 0;
                }
                else {
                    this.adjMatrix[i][j] = 1;
                }
            }
        }

        for(int i = 0; i < numOfNodes; i++){
            System.arraycopy(this.adjMatrix[i], 0, main.adjMatrix[i], 0, numOfNodes);
        }

        return main;
    }

}
