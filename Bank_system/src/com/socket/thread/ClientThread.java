package com.socket.thread;

import com.protocol.ChandyLamport;
import com.socket.ProjectMain;
import com.socket.msg.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * 接收套接字并根据收到对象类型进行处理
 */
public class ClientThread extends Thread {
    Socket cSocket;
    ProjectMain mainObj;
    ChandyLamport chandy;

    public ClientThread(Socket socket, ProjectMain mainObj, ChandyLamport chandy) {
        this.cSocket = socket;
        this.mainObj = mainObj;
        this.chandy = chandy;
    }

    public void run() {
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(cSocket.getInputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while(true){
            try {
                //mainObj.cnt++;
                Message msg;
                msg = (Message) Objects.requireNonNull(ois).readObject();
                // Synchronizing mainObj so that multiple threads access mainObj in a synchronized way
                synchronized(mainObj) {

                    //If message is a marker message then process has to turn red if its blue and send messages along all its
                    //channels
                    if (msg instanceof SnapMsg) {
                        chandy.OperateSnapping(mainObj, -1);
                    }
                    else if (msg instanceof MarkerMsg) {
                        chandy.OperateMarker(mainObj, msg);
                    }
                    else if (msg instanceof ApplicationMsg){
                        
                        chandy.RecordMsg(mainObj, msg);
                        if (mainObj.isSnapping()) {
                            mainObj.hangMessage((ApplicationMsg)msg);
                        }
                        else {
                            mainObj.processApplicationMessage((ApplicationMsg)msg);
                        }
                    }
                    // if (mainObj.cnt == 70){
                    //     chandy.OperateSnapping(mainObj, -1);
                    // }
                }
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}