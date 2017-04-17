package com.example.arvin.myapplication.socket.server;

import com.example.arvin.myapplication.ConstDef;
import com.example.arvin.myapplication.socket.MsgRecvThread;
import com.example.arvin.myapplication.socket.MsgSendThread;
import com.example.arvin.myapplication.socket.connect.CmdReqCallback;
import com.example.arvin.myapplication.socket.connect.IConnMng;
import com.example.arvin.myapplication.socket.connect.IConnect;
import com.example.arvin.myapplication.socket.connect.IHeartBeatCallBack;
import com.example.arvin.myapplication.socket.connect.IMessage;
import com.example.arvin.myapplication.socket.connect.INetConnectListener;
import com.example.arvin.myapplication.socket.connect.IRecvHandler;
import com.example.arvin.myapplication.socket.connect.ITransInfo;
import com.example.arvin.myapplication.socket.impl.ServerHeartBeatService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by arvin on 2017/4/13.
 */

public class ServerConnect extends Thread implements IConnect, IConnMng, IHeartBeatCallBack {

    private int serverPort;
    private Semaphore semaphore = new Semaphore(1);
    private boolean isClose = false;
    Map<Integer, ServerConn> m_connMap = new ConcurrentHashMap<>();
    private int maxConnID = 1000;
    IRecvHandler recvHandler;
    ITransInfo iTransInfo;
    ServerHeartBeatService heartBeatService;

    private ArrayList<INetConnectListener> listeners = new ArrayList<INetConnectListener>();

    class ServerConn {
        Socket m_clnSock;
        MsgSendThread m_Send;
        MsgRecvThread m_Recv;
        boolean m_bConnected;
        int m_nSendSeq;
    }

    public ServerConnect(int serverPort, IRecvHandler recvHandler) {
        this.serverPort = serverPort;
        this.recvHandler = recvHandler;
        heartBeatService = new ServerHeartBeatService(this.recvHandler, ServerConnect.this, ConstDef.SERVER_HEARTBEAT_EXPIRE_TIME_IN_SECONDS, false);
    }

    @Override
    public void remoteDidFailedToBeat(int remoteId) {
        m_connMap.remove(remoteId);
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {
        super.start();
        heartBeatService.startBeating();
    }

    @Override
    public void m_stop() {
        isClose = true;
        heartBeatService.stopBeating();
        try {
            semaphore.acquire();
            semaphore.release();
        } catch (InterruptedException e) {
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

        //// TODO: 2017/4/14  test

        for (Map.Entry<Integer, ServerConn> conn : m_connMap.entrySet()) {
            conn.getValue().m_Send.send(msg.getMessage(), msg.getLevel(), msg.getWaitTime());
        }

        ServerConn conn = m_connMap.get(clientID);
        if (conn == null) {
            return false;
        }

        return conn.m_Send.send(msg.getMessage(), msg.getLevel(), msg.getWaitTime());
    }

    @Override
    public boolean sendCallback(int clientID, IMessage msg, long timeout, CmdReqCallback callback) {
        ServerConn conn = m_connMap.get(clientID);
        if (conn == null) {
            return false;
        }
        int seq = iTransInfo.beginTrans(timeout, callback);
        msg.setSequenceId(seq);

        return conn.m_Send.send(msg.getMessage(), msg.getLevel(), msg.getWaitTime());
    }

    @Override
    public boolean syncSend(int serverID, long timeout, IMessage msg) {
        return false;
    }

    @Override
    public boolean syncSendNext() {
        return false;
    }

    @Override
    public void addINetConnectListener(INetConnectListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void closeSocket(int nServerID) {
        heartBeatService.removeClient(nServerID);
        m_connMap.remove(nServerID);
        for (INetConnectListener alisterner : listeners) {
            alisterner.connectStatusChange(maxConnID, false);
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSock = new ServerSocket(serverPort);

            while (true) {
                try {
                    semaphore.acquire();
                    if (isClose) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                Socket clntSocket = serverSock.accept();

                ServerConn conn = new ServerConn();
                conn.m_clnSock = clntSocket;
                conn.m_Recv = new MsgRecvThread(this, maxConnID);
                conn.m_Send = new MsgSendThread(this, maxConnID);
                conn.m_Send.Start(clntSocket.getOutputStream());
                conn.m_Recv.Start(clntSocket.getInputStream());
                conn.m_Recv.setRecvHandler(recvHandler);
                conn.m_bConnected = true;

                for (INetConnectListener alisterner : listeners) {
                    alisterner.connectStatusChange(maxConnID, true);
                }

                m_connMap.put(maxConnID, conn);
                heartBeatService.addClient(maxConnID);
                maxConnID++;

                semaphore.release();
            }
        } catch (IOException e) {
            for (INetConnectListener alisterner : listeners) {
                alisterner.failedToConnect(maxConnID, e);
            }
            e.printStackTrace();
        }
    }

}
