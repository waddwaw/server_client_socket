package com.example.arvin.myapplication.socket.test;

import com.example.arvin.myapplication.socket.ConnectManager;
import com.example.arvin.myapplication.socket.connect.IConnectPolicy;
import com.example.arvin.myapplication.socket.connect.INetConnectListener;
import com.example.arvin.myapplication.socket.connect.IRecvHandler;
import com.example.arvin.myapplication.socket.connect.ITransInfo;

/**
 * Created by arvin on 2017/4/14.
 */

public class TestSocket {

    public static void startSocketClient(IConnectPolicy policy, IRecvHandler iRecvHandler, ITransInfo iTransInfo, INetConnectListener listener) {
        ConnectManager.getInstance().addClientConnect(policy, iRecvHandler, iTransInfo, listener);
    }

}
