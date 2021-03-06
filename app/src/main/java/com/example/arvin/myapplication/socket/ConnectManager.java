package com.example.arvin.myapplication.socket;

import com.example.arvin.myapplication.socket.connect.TcpConnect;
import com.example.arvin.myapplication.socket.connect.UdpConnect;
import com.example.arvin.myapplication.socket.entity.IMessage;
import com.example.arvin.myapplication.socket.connect.TcpServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by arvin on 2017/4/13.
 */

public class ConnectManager {

    private static Map<Integer, IConnect> connectMaps = new ConcurrentHashMap<>();

    private static ConnectManager connectMng;

    public static ConnectManager getInstance() {
        if (connectMng == null) {
            synchronized (ConnectManager.class) {
                if (connectMng == null)
                    connectMng = new ConnectManager();
            }
        }
        return connectMng;
    }

    public ConnectManager() {

    }

    /**
     * ConnectManager 添加客户端类服务器 如连接云端的主服务器 默认启动服务
     *
     * @param policy
     */
    public void addClientConnect(final IConnectPolicy policy, final IRecvHandler iRecvHandler, ITransInfo iTransInfo, final INetConnectListener listener, IMessage heartBeatMsg) {
        iRecvHandler.m_transInfo = iTransInfo;
        final TcpConnect lanClient = new TcpConnect(policy, iRecvHandler, heartBeatMsg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                lanClient.init();
                lanClient.start();
                lanClient.addINetConnectListener(listener);
                connectMaps.put(policy.nServerID(), lanClient);
            }
        }).start();
    }

    /**
     * ConnectManager 添加本地Server服务 默认启动该服务
     *
     * @param serkey
     * @param port
     * @param iRecvHandler
     */
    public void addServerConnect(final int serkey, int port, IRecvHandler iRecvHandler, ITransInfo iTransInfo, final INetConnectListener listener) {
        iRecvHandler.m_transInfo = iTransInfo;
        final TcpServer connect = new TcpServer(port, iRecvHandler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect.init();
                connect.start();
                connect.addINetConnectListener(listener);
                connectMaps.put(serkey, connect);
            }
        }).start();
    }

    /**
     * ConnectManager 添加Udp连接
     *
     * @param iRecvHandler
     */
    public void addUdpConnect(final IConnectPolicy policy, final IRecvHandler iRecvHandler, ITransInfo iTransInfo,IMessage heartBeatMsg) {
        iRecvHandler.m_transInfo = iTransInfo;
        final UdpConnect connect = new UdpConnect(policy, iRecvHandler, heartBeatMsg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect.init();
                connect.start();
                connectMaps.put(policy.nServerID(), connect);
            }
        }).start();
    }

    public boolean send(int serviceKey, int clientID, IMessage msg) {
        IConnect server = connectMaps.get(serviceKey);
        if (server != null) {
            return server.send(clientID, msg);
        }
        return false;
    }

    public boolean syncSend(int serviceKey, long timeout, IMessage msg) {
        IConnect server = connectMaps.get(serviceKey);
        if (server != null) {
            return server.syncSend(serviceKey, timeout, msg);
        }
        return false;
    }

    public boolean sendCallback(int serviceKey, int clientID, IMessage msg, long timeout, CmdReqCallback callback) {
        IConnect server = connectMaps.get(serviceKey);
        if (server != null) {
            return server.sendCallback(clientID, msg, timeout, callback);
        }
        return false;
    }

    public boolean syncSendNext(int serviceKey) {
        IConnect server = connectMaps.get(serviceKey);
        if (server != null) {
            return server.syncSendNext();
        }
        return false;
    }

    public void closeServer(int serKey) {
        IConnect server = connectMaps.remove(serKey);
        if (server != null) {
            server.m_stop();
            server.uninit();
        }
    }

}
