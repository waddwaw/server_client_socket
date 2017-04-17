package com.example.arvin.myapplication.socket.connect;

/**
 * Created by arvin on 2016/12/30.
 */
public interface CmdReqCallback<T extends IMessage> {
    void callback(T msg);
    void timeOut();
}
