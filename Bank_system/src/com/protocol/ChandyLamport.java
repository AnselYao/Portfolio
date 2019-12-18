package com.protocol;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.socket.ProjectMain;
import com.socket.msg.ApplicationMsg;
import com.socket.msg.MarkerMsg;
import com.socket.msg.Message;
import com.SocketConstant;
import java.net.URL;
import java.net.HttpURLConnection;

import java.io.*;

public class ChandyLamport {
    int a, b, c, nodeCnt, curNode;
    boolean isFirstMarker = true;
    boolean haveAllFinished = true;
    boolean[] haveChannelFinished;
    List<ApplicationMsg> applicationMsgList = new ArrayList<ApplicationMsg>();
    int[][] channelRecord;

    public void SetChannelState(boolean state) {
        for (int i = 0; i < this.haveChannelFinished.length; ++i) {
            this.haveChannelFinished[i] = state;
        }
    }

    public ChandyLamport(int nodeCnt, int curNode) {
        a = 0;
        b = 0;
        c = 0;
        this.nodeCnt = nodeCnt;
        this.curNode = curNode;
        haveChannelFinished = new boolean[nodeCnt];
        SetChannelState(true);
        channelRecord = new int[nodeCnt][3];
        for (int i = 0; i < nodeCnt; ++i) {
            for (int j = 0; j < 3; ++j) {
                channelRecord[i][j] = 0;
            }
        }
    }

    // public void MakeJson() {
    //     String temp = "";
    //     int snapID = 0;

    //     try {
    //         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("test.json"));

    //         out.write('{');
    //             // TODO: get snapId
    //             temp = String.format("\"snapID\": \"%d\", \n", snapID);
    //             out.write(temp.getBytes()); out.flush();
    //             // TODO: get localId
    //             temp = String.format("\"localId\": %d, \n", snapID);
    //             out.write(temp.getBytes()); out.flush();
    //             temp = "\"snapContent\": {\n";
    //             out.write(temp.getBytes()); out.flush();
    //                 temp = "\"local\": {\n";
    //                 out.write(temp.getBytes()); out.flush();
    //                 temp = String.format("\"A\":%d,\"B\":%d,\"C\":%d", a,b,c);
    //                 out.write(temp.getBytes()); out.flush();
    //                 out.write('}'); out.flush();

    //                 temp = ",\n\"channelMsg\": [";
    //                 out.write(temp.getBytes()); out.flush();
    //                 for (int i = 0; i < this.nodeCnt; ++i) {
    //                     if (i != 0) out.write(',');
    //                     temp = String.format("\n{\"fromNode\": %d,", i);
    //                     out.write(temp.getBytes()); out.flush();
    //                     temp = String.format(
    //                         "\"content\": {\"A\": %d,\"B\": %d,\"C\": %d}}",
    //                         channelRecord[i][0],
    //                         channelRecord[i][1],
    //                         channelRecord[i][2]
    //                         );
    //                     out.write(temp.getBytes()); out.flush();
    //                 }
    //                 out.write(']');
    //             out.write('}');
    //         out.write('}');
    //         out.flush();
    //         out.close();
    //         System.out.println("OK");
    //     } catch (Exception e) {
    //         System.err.println(e);
    //         // TODO: handle exception
    //     }
    // }

    /**
     * 
     */
    public JsonObject MakeNewJson() {
        int snapId = 0;
        int localId = curNode;
        JsonObject myJson = new JsonObject();
        JsonObject subJson = new JsonObject();
        JsonObject local = new JsonObject();
        JsonArray channelMsg = new JsonArray();
        
        local.addProperty("A", a);
        local.addProperty("B", b);
        local.addProperty("C", c);
        
        for (int i = 0; i < nodeCnt; ++i) {
            JsonObject subsubJson = new JsonObject();
            JsonObject content = new JsonObject();
            content.addProperty("A", channelRecord[i][0]);
            content.addProperty("B", channelRecord[i][1]);
            content.addProperty("C", channelRecord[i][2]);
            subsubJson.addProperty("fromNode", i);
            subsubJson.add("content", content);
        }
        
        subJson.add("local", local);
        subJson.add("channelMsg", channelMsg);
        
        myJson.addProperty("snapId", snapId);
        myJson.addProperty("localId", localId);
        myJson.add("snapContent", subJson);

        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("snap.json"));

            String temp = myJson.toString();
            out.write(temp.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        return myJson;
    }

    public int Upload(JsonObject json) {
        try {
            
            URL url = new URL(SocketConstant.uploadUrl);
            // 建立http连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
    
            conn.setDoInput(true);
    
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = (json.toString()).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
    
            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");
    
            // 开始连接请求
            conn.connect();
            OutputStream out = conn.getOutputStream();
            // 写入请求的字符串
            out.write((json.toString()).getBytes());
            out.flush();
            out.close();
    
            System.out.println(conn.getResponseCode());
            return conn.getResponseCode();
        } catch (Exception e) {
            //TODO: handle exception
            System.err.println(e);
            return -1;
        }
    }

    public boolean HaveAllFinished() {
        for (int i = 0; i < haveChannelFinished.length; ++i) {
            if (haveChannelFinished[i] == false) {
                // System.out.println(haveChannelFinished);
                return false;
            }
        }
        return true;
    }

    public void OperateSnapping(ProjectMain mainObj, int index) {
        isFirstMarker = false;
        haveAllFinished = false;
        SetChannelState(false);
        if (index != -1) {
            haveChannelFinished[index] = true;
        }
        // Snap();
        a = mainObj.GetCache().get("A");
        b = mainObj.GetCache().get("B");
        c = mainObj.GetCache().get("C");
        mainObj.test();
        // SendMarkers();
        int[] channelNeighbors = mainObj.GetNeighbors();
        for (int i = 0; i < channelNeighbors.length; ++i) {
            sendMarkerMessage(mainObj, channelNeighbors[i]);
            System.out.println(channelNeighbors[i]);
        }
    }

    public void OperateMarker(ProjectMain mainObj, Message msg) {
        int channelNo = ((MarkerMsg) msg).nodeId;
        System.out.println("Marker from"+channelNo);
        int index = mainObj.GetIndexByNodeID(channelNo);
        if (index != -1) {
            haveChannelFinished[index] = true;
        }
        if (isFirstMarker) {
            mainObj.setStateSnapping();
            OperateSnapping(mainObj, index);
            mainObj.setStateWorking();
        }
        else {
            if (HaveAllFinished()) {
                System.out.println("=========================================");
                System.out.println(a);
                System.out.println(b);
                System.out.println(c);
                if (applicationMsgList == null || applicationMsgList.size() == 0){
                    System.out.println("Nothing");
                }
                else
                {
                    for (int i = 0; i < applicationMsgList.size(); ++i)
                    {
                        System.out.println("-----------------------------------------");
                        ApplicationMsg appmsg = applicationMsgList.get(i);
                        int nodeIndex = appmsg.nodeId;
                        String mapStr = appmsg.getMsg();
                        int aa, bb, cc;
                        aa = ProjectMain.token2Map(mapStr).get("A");
                        bb = ProjectMain.token2Map(mapStr).get("B");
                        cc = ProjectMain.token2Map(mapStr).get("C");
                        channelRecord[nodeIndex][0] += aa;
                        channelRecord[nodeIndex][1] += bb;
                        channelRecord[nodeIndex][2] += bb;
                        System.out.println(aa);
                        System.out.println(bb);
                        System.out.println(cc);
                    }
                    System.out.println("=========================================");
                    // System.out.println(applicationMsgList.toString());
                }
                
                JsonObject myJson = MakeNewJson();
                int code = Upload(myJson);
                if (code == -1) System.out.println("Error");
                else System.out.println(code);
                haveAllFinished = true;
                isFirstMarker = true;
            }
            else {
                System.out.println("Not finished");
            }
        }
    }

    public void RecordMsg(ProjectMain mainObj, Message msg) {
        int channelNo = ((ApplicationMsg) msg).nodeId;
        int index = mainObj.GetIndexByNodeID(channelNo);
        if (haveChannelFinished[index] == false) {
            // TODO: record
            try {
                ApplicationMsg appMsg = (ApplicationMsg)msg;
                applicationMsgList.add(appMsg);
            } catch (Exception e) {
                //TODO: handle exception
                System.err.println(e);
            }
        }
    }

    // public static void operateSnapping(ProjectMain projectMain, int neighbour) {
    //     //TODO 2.1 快照操作
    // }

    public static void sendMarkerMessage(ProjectMain projectMain, int channelNo) {
        //TODO 2.0 发送MARKER的策略
        projectMain.sendMarkerMessage(channelNo);
    }

    /**
     * 完成快照操作并发送完MARKER后令状态归位
     */
    public static void returnToNormalState(ProjectMain projectMain) {
        projectMain.processHangedMessage();
        projectMain.setStateWorking();
    }
}
