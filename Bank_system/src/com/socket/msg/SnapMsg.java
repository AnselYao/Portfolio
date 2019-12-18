package com.socket.msg;

import com.SocketConstant;

import java.io.Serializable;

/**
 * Snap消息，可以根据设计需要添加时间戳等
 */
public class SnapMsg extends Message implements Serializable {
    // String msg;
    private static final long serialVersionUID = 84093284032001384L;
    int nodeId;
    String action;
    String info;

    public SnapMsg(String snapId) {
        this.nodeId = -1;
        this.action = "snapshoot";
        this.info = snapId;
    }

    // public SnapMsg(int nodeId) {
    //     this.msg = SocketConstant.MARKER_STRING;
    //     this.nodeId = nodeId;
    // }
}
