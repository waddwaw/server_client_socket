package com.example.arvin.myapplication.socket.connect;

import com.example.arvin.myapplication.ConstDef;
import com.example.arvin.myapplication.socket.CmdReqCallback;
import com.example.arvin.myapplication.socket.IConnMng;
import com.example.arvin.myapplication.socket.IConnect;
import com.example.arvin.myapplication.socket.IConnectPolicy;
import com.example.arvin.myapplication.socket.IHeartBeatCallBack;
import com.example.arvin.myapplication.socket.INetConnectListener;
import com.example.arvin.myapplication.socket.IRecvHandler;
import com.example.arvin.myapplication.socket.ServerHeartBeatService;
import com.example.arvin.myapplication.socket.entity.IMessage;
import com.example.arvin.myapplication.socket.thread.UDPRecvThread;
import com.example.arvin.myapplication.socket.thread.UDPSendThread;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by arvin on 2017/4/19.
 */

public class UdpConnect implements IConnect, IConnMng, IHeartBeatCallBack {

    IConnectPolicy policy;
    IRecvHandler iRecvHandler;
    UDPRecvThread udpRecvThread;
    UDPSendThread udpSendThread;
    DatagramSocket m_UDPSock;
    ServerHeartBeatService heartBeatService;

    public UdpConnect(IConnectPolicy policy, IRecvHandler iRecvHandler, IMessage heartBeatMsg) {
        this.policy = policy;
        this.iRecvHandler = iRecvHandler;
        heartBeatService  = new ServerHeartBeatService(heartBeatMsg, UdpConnect.this, ConstDef.SERVER_HEARTBEAT_EXPIRE_TIME_IN_SECONDS, false);
        this.iRecvHandler.heartBeatService = heartBeatService;
    }

    @Override
    public void init() {
        udpSendThread = new UDPSendThread(this, policy.nServerID(), policy.getServerHost().get(0).serverHost, (short) policy.getServerHost().get(0).serverPort);
        udpRecvThread = new UDPRecvThread(this, policy.nServerID());
        udpRecvThread.setRecvHandler(iRecvHandler);
        heartBeatService.addClient(policy.nServerID());
    }

    @Override
    public void start() {
        try {
            if (policy.getServerHost().get(0).serverPort != 0) {      //指定绑定端口
                m_UDPSock = new DatagramSocket(policy.getServerHost().get(0).serverPort);
            } else {
                m_UDPSock = new DatagramSocket();
            }
            udpSendThread.Start(m_UDPSock);
            udpRecvThread.Start(m_UDPSock);
            this.iRecvHandler.heartBeatService.startBeating();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void m_stop() {
        udpSendThread.Stop();
        udpRecvThread.Stop();
        this.iRecvHandler.heartBeatService.stopBeating();
    }

    @Override
    public void uninit() {

    }

    @Override
    public void restart() {

    }

    @Override
    public boolean send(int clientID, IMessage msg) {
        if (udpSendThread == null) {
            return false;
        }
        udpSendThread.sendTo(msg.getMessage());
        return true;
    }

    @Override
    public boolean sendCallback(int clientID, IMessage msg, long timeout, CmdReqCallback callback) {
        try {
            throw new NoSuchMethodException("不支持的操作 --> An unsupported operation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean syncSend(int serverKey, long timeout, IMessage msg) {
        try {
            throw new NoSuchMethodException("不支持的操作 --> An unsupported operation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean syncSendNext() {
        try {
            throw new NoSuchMethodException("不支持的操作 --> An unsupported operation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addINetConnectListener(INetConnectListener listener) {
        try {
            throw new NoSuchMethodException("不支持的操作 --> An unsupported operation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void closeSocket(int nServerID) {

    }

    @Override
    public void remoteDidFailedToBeat(int clientId) {

    }
}
