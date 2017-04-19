package com.example.arvin.myapplication.socket.entity;


import com.example.arvin.myapplication.ConstDef;

/**
 * Created by Ryan on 2016/4/15.
 */
public abstract class IWBPByteMsg {
    public abstract byte[] getBytes();

    public abstract void setSequence(int seq);

    public abstract void setProtocolVer(byte version);

    public abstract short getCmdID();

    public abstract WBPLLHead getWBPLLHead();

    public int getLevel() {
        return ConstDef.LEVEL_NORMAL;
    }

    public int getWaitTime() {
        return 0;
    }
}
