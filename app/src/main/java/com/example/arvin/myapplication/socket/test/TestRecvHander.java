package com.example.arvin.myapplication.socket.test;

import android.util.Log;

import com.example.arvin.myapplication.socket.entity.IMessage;
import com.example.arvin.myapplication.socket.IRecvHandler;

/**
 * Created by arvin on 2017/4/14.
 */

public class TestRecvHander extends IRecvHandler<TestMsg>{


    @Override
    public TestMsg handleRecvByteMsg(int nServerID, byte[] recvMsg, int rcvSize) {
        byte[] buf = new byte[rcvSize];
        System.arraycopy(recvMsg, 0, buf, 0, rcvSize);
        Log.d("data" , "id:" + nServerID + "byte[]:" + new String(buf) + "rcvSize:" + rcvSize);
        return new TestMsg(new String(buf));
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
    public void handleMsg(int nServerID, TestMsg message) {
        //处理实际消息实体
        Log.d("data", message.toString());
    }
}
