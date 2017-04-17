package com.example.arvin.myapplication.socket.test;

import com.example.arvin.myapplication.socket.connect.IMessage;

/**
 * Created by arvin on 2017/4/14.
 */

public class TestMsg implements IMessage{

    int seq;
    String data;

    public TestMsg(String data){
        this.data = data;
    }

    @Override
    public int getSequenceId() {
        return seq;
    }

    @Override
    public void setSequenceId(int seq) {
        this.seq = seq;
    }

    @Override
    public byte[] getMessage() {
        return data.getBytes();
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getWaitTime() {
        return 0;
    }

}
