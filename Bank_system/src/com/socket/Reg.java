package com.socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.SocketConstant;
import java.util.regex.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.InputStream;

public class Reg {
    String name;
    String ip;
    int port;
    int curId;

    public Reg(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public void testjson()// String args[]
    {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", this.name);
            obj.addProperty("ip", this.ip);
            obj.addProperty("port", this.port);

            System.out.println(obj);
            // 创建url资源
            URL url = new URL(SocketConstant.strUrl);
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
            byte[] data = (obj.toString()).getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));

            // 设置文件类型:
            conn.setRequestProperty("contentType", "application/json");

            // 开始连接请求
            conn.connect();
            OutputStream out = conn.getOutputStream();
            // 写入请求的字符串
            out.write((obj.toString()).getBytes());
            out.flush();
            out.close();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
            if (conn.getResponseCode() == 200) {
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in = conn.getInputStream();
                String a = null;
                try {
                    byte[] data1 = new byte[in.available()];
                    in.read(data1);
                    // 转成字符串
                    a = new String(data1);
                    System.out.println(a);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                // TODO: transform a to json
                String pattern = "\\d*";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(a);
                if (m.find()) {
                    this.curId = Integer.parseInt(m.group(0));
                    System.out.println("Current ID:" + m.group(0));
                }
                else {
                    System.out.println("No ID");
                }
            } else {
                System.out.println("no++");
            }

        } catch (Exception e) {

        }

    }

    // public static void main(String[] args) throws Exception {

    // testjson();

    // }

}
