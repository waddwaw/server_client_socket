package com.example.arvin.myapplication.socket.connect;

import com.example.arvin.myapplication.socket.CmdReqCallback;
import com.example.arvin.myapplication.socket.IConnMng;
import com.example.arvin.myapplication.socket.IConnect;
import com.example.arvin.myapplication.socket.IConnectPolicy;
import com.example.arvin.myapplication.socket.INetConnectListener;
import com.example.arvin.myapplication.socket.IRecvHandler;
import com.example.arvin.myapplication.socket.entity.IMessage;
import com.example.arvin.myapplication.socket.thread.UDPRecvThread;
import com.example.arvin.myapplication.socket.thread.UDPSendThread;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by arvin on 2017/4/19.
 */

public class UdpConnect implements IConnect, IConnMng {

    IConnectPolicy policy;
    IRecvHandler iRecvHandler;
    UDPRecvThread udpRecvThread;
    UDPSendThread udpSendThread;
    DatagramSocket m_UDPSock;

    public UdpConnect(IConnectPolicy policy, IRecvHandler iRecvHandler) {
        this.policy = policy;
        this.iRecvHandler = iRecvHandler;
    }

    @Override
    public void init() {
        udpSendThread = new UDPSendThread(this, policy.nServerID(), policy.getServerHost().get(0).serverHost, (short) policy.getServerHost().get(0).serverPort);
        udpRecvThread = new UDPRecvThread(this, policy.nServerID());
        udpRecvThread.setRecvHandler(iRecvHandler);
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
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void m_stop() {
        udpSendThread.Stop();
        udpRecvThread.Stop();
    }

    @Override
    public void uninit() {

    }

    @Override
    public void restart() {

    }

    @Override
    public boolean send(int clientID, IMessage msg) {
        try {
            throw new NoSuchMethodException("不支持的操作 --> An unsupported operation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
}
