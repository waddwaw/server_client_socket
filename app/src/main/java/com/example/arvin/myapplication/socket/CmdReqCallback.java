package com.example.arvin.myapplication.socket;

import com.example.arvin.myapplication.socket.entity.IMessage;

/**
 * Created by arvin on 2016/12/30.
 */
public interface CmdReqCallback<T extends IMessage> {
    void callback(T msg);
    void timeOut();
}
