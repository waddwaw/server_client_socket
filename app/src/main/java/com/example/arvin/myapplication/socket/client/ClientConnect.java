package com.example.arvin.myapplication.socket.client;

import android.util.Log;

import com.example.arvin.myapplication.ConstDef;
import com.example.arvin.myapplication.socket.MsgRecvThread;
import com.example.arvin.myapplication.socket.MsgSendThread;
import com.example.arvin.myapplication.socket.connect.CmdReqCallback;
import com.example.arvin.myapplication.socket.connect.IConnMng;
import com.example.arvin.myapplication.socket.connect.IConnect;
import com.example.arvin.myapplication.socket.connect.IConnectPolicy;
import com.example.arvin.myapplication.socket.connect.IHeartBeatCallBack;
import com.example.arvin.myapplication.socket.connect.IMessage;
import com.example.arvin.myapplication.socket.connect.INetConnectListener;
import com.example.arvin.myapplication.socket.connect.IRecvHandler;
import com.example.arvin.myapplication.socket.connect.ITransInfo;
import com.example.arvin.myapplication.socket.impl.ServerHeartBeatService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvin on 2017/4/13.
 */

public class ClientConnect implements IConnect,IConnMng,IHeartBeatCallBack {

    IConnectPolicy policy;
    IRecvHandler iRecvHandler;
    MsgRecvThread msgRecvThread;
    MsgSendThread msgSendThread;
    Socket sock;
    ITransInfo transInfo;
    INetConnectListener listener;
    ServerHeartBeatService heartBeatService;

    public ClientConnect(IConnectPolicy policy, IRecvHandler iRecvHandler,IMessage heartBeatMsg) {
        this.policy = policy;
        this.iRecvHandler = iRecvHandler;
        this.transInfo = iRecvHandler.m_transInfo;
        heartBeatService  = new ServerHeartBeatService(heartBeatMsg, ClientConnect.this, ConstDef.SERVER_HEARTBEAT_EXPIRE_TIME_IN_SECONDS, true);
        this.iRecvHandler.heartBeatService = heartBeatService;
    }

    @Override
    public void init() {
        try {
            sock = new Socket();
            String ip = policy.getServerHost().get(0).serverHost;
            int port = policy.getServerHost().get(0).serverPort;

            sock.connect(new InetSocketAddress(ip, port), policy.minInterval());

            Log.i("socket", "TCP connect remote IP:" + ip + " port:" + port + " local IP:" + sock.getLocalAddress());
            if (null == sock.getInputStream() ||
                    null == sock.getOutputStream()) {
                sock.close();
                throw new SocketException("Empty I/O Stream");
            }

            msgRecvThread = new MsgRecvThread(this, policy.nServerID());
            msgSendThread = new MsgSendThread(this, policy.nServerID());
            msgRecvThread.setRecvHandler(iRecvHandler);

            heartBeatService.addClient(policy.nServerID());
            if (listener != null) {
                listener.connectStatusChange(policy.nServerID(), true);
            }

        } catch (IOException e) {
            Log.i("socket", "Try to connect Error " + policy.nServerID() +
                    " " + policy.getServerHost().get(0).serverHost + "(" + policy.getServerHost().get(0).serverPort + ")" + e.getMessage());
            if (listener != null) {
                listener.failedToConnect(policy.nServerID(), e);
            }
            try {
                throw e;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        if (sock == null) {
            return;
        }
        heartBeatService.startBeating();
        try {
            msgRecvThread.Start(sock.getInputStream());
            msgSendThread.Start(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        transInfo.m_start();

    }

    @Override
    public void m_stop() {
        if (sock == null) {
            return;
        }
        heartBeatService.stopBeating();
        msgRecvThread.Stop();
        msgSendThread.Stop();

        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uninit() {

    }

    @Override
    public void restart() {

    }

    @Override
    public boolean send(int clientID, IMessage msg) {

        return msgSendThread.send(msg.getMessage(), msg.getLevel(), msg.getWaitTime());
    }

    @Override
    public boolean sendCallback(int clientID, IMessage msg, long timeout, CmdReqCallback callback) {

        int seq = transInfo.beginTrans(timeout, callback);
        msg.setSequenceId(seq);
        return msgSendThread.send(msg.getMessage(), msg.getLevel(), msg.getWaitTime());
    }

    @Override
    public boolean syncSend(int serverID, long timeout, IMessage msg) {
        transInfo.setServiceId(serverID);
        int seq = transInfo.beginTrans(timeout, null);
        msg.setSequenceId(seq);
        return msgSendThread.syncSend(msg.getMessage(), msg.getLevel(), msg.getWaitTime());
    }

    @Override
    public boolean syncSendNext() {

        msgSendThread.syncSendNext();
        return true;
    }

    @Override
    public void addINetConnectListener(INetConnectListener listener) {
        this.listener = listener;
    }

    @Override
    public void closeSocket(int nServerID) {
        heartBeatService.removeClient(nServerID);
        if (listener != null) {
            listener.connectStatusChange(nServerID, false);
        }
        try {
            if (sock != null) {
                sock.close();
            }
            sock = null;
            msgRecvThread.close();
            msgRecvThread.Stop();
            msgSendThread.close();
            msgSendThread.Stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remoteDidFailedToBeat(int remoteId) {
        closeSocket(remoteId);
    }
}
