package com.socket.thread;

import com.protocol.ChandyLamport;
import com.socket.ProjectMain;

/**
 * 快照协议进程，通过调用ChandyLamport类中方法实现快照
 */
public class ChandyLamportThread extends Thread{

    ProjectMain mainObj;
    ChandyLamport chandy;

    public ChandyLamportThread(ProjectMain mainObj, ChandyLamport chandy){
        this.mainObj = mainObj;
        this.chandy = chandy;
    }
    public void run(){
        //TODO 1.1 Chandy-Lamport协议线程
    }
}