package com.example.arvin.myapplication.socket.test;

import android.util.Log;

import com.example.arvin.myapplication.socket.connect.IMessage;
import com.example.arvin.myapplication.socket.connect.IRecvHandler;

/**
 * Created by arvin on 2017/4/14.
 */

public class TestRecvHander extends IRecvHandler{


    @Override
    public IMessage handleRecvByteMsg(int nServerID, byte[] recvMsg, int rcvSize) {
        Log.d("data" , "id:" + nServerID + "byte[]:" + new String(recvMsg) + "rcvSize:" + rcvSize);
        return null;
    }

    @Override
    public void clearRecvMsg(int nServerID) {
        Log.d("data" , "clearRecvMsg nServerID:" + nServerID );
    }

    @Override
    public boolean handlerHeartBeat(int nServerID, IMessage message) {

        return false;
    }

    @Override
    public void handleMsg(int nServerID, IMessage message) {
        //处理实际消息实体
    }

    @Override
    public IMessage getHeartBeatMsg() {
        TestMsg msg = new TestMsg("xin");
        return msg;
    }
}
