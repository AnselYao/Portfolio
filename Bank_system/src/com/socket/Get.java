// package app;
package com.socket;

import com.SocketConstant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import java.net.URL;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.PrintWriter;

public class Get {
    int numOfNode;
    int stateCode;

    public Get() {
        numOfNode = 0;
    }

    public JsonObject GetJson() throws IOException {

        String strUrl = SocketConstant.strUrl ;

        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();

            this.stateCode = connection.getResponseCode();
            System.out.println(stateCode);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bReader.close();
            connection.disconnect();
            JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();
            jsonObject = gson.fromJson(stringBuilder.toString(), JsonObject.class);
            return jsonObject;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Node> GetNodes(String strUrl) throws Exception {
        ArrayList<Node> nodes = new ArrayList<Node>();

        JsonObject json;
        json = GetJson();
        JsonArray row = json.getAsJsonArray("nodes");
        for (int i = 0; i < row.size(); i++) {
            JsonElement target = row.get(i);
            JsonObject node = target.getAsJsonObject();
            // TODO: 节点中没有id信息，是否按照json中的顺序排列
            JsonElement ip = node.get("ip");
            JsonElement port = node.get("port");
            String myIP = ip.getAsString();//String.valueOf(ip);
            String strPort = port.getAsString();
            try {
                int myPort = Integer.parseInt(strPort);
                Node myNode = new Node(i, myIP, myPort);
                nodes.add(myNode);
                this.numOfNode += 1;
            } catch (Exception e) {
                //TODO: handle exception
                System.err.println(e);
            }
        }
        return nodes;
    }

    public int GetNumOfNode() {
        return this.numOfNode;
    }
}
//     public static void main(String[] args) throws Exception {
//         JsonObject mytest;
//         mytest=GetJson();
//         JsonArray row = mytest.getAsJsonArray("nodes");
//         for (int i = 0; i < row.size(); i++) {
//             JsonElement target = row.get(i);
//             JsonObject node = target.getAsJsonObject();
//             JsonElement ip=node.get("ip");
//             JsonElement port=node.get("port");
//             String ip1 = String.valueOf(ip);
//             String port1 = String.valueOf(port);
//             //获取sisid
// 			//String ip = String.valueOf(int_sisid);
//             //获取sitid
//             //String port = String.valueOf(int_sitid);
//             System.out.println(ip1+' '+port1);
//         }
//         //Gson a=new Gson();
//         //String url="http://106.54.141.210:8000/api/aaa/node";
//         //testapp(url,mytest);
//         System.out.println("end");
//     }
// }

