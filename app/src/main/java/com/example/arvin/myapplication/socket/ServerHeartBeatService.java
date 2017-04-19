package com.example.arvin.myapplication.socket;

import android.util.Log;

import com.example.arvin.myapplication.socket.entity.IMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Created by zengbin on 4/25/16.
 * Copyright © 2016 GuangTian. All rights reserved.
 */
public class ServerHeartBeatService extends Thread {
    private static class ClientHBInfo {
        private int m_nClientID;
        private long m_LastSendTime;

        private ClientHBInfo(int m_nClientID, long m_LastSendTime) {
            this.m_nClientID = m_nClientID;
            this.m_LastSendTime = m_LastSendTime;
        }
    }

    private Map<Integer, ClientHBInfo> m_clientHBMap;
    private IMessage heartBeatMsg;

    private long m_expireTimeInNanoSeconds;
    private boolean m_bStart;
    private IHeartBeatCallBack m_callBack;
    private boolean isClient;
    private long expireTimeInSeconds;

    public ServerHeartBeatService(IMessage heartBeatMsg, IHeartBeatCallBack callBack, long expireTimeInSeconds, boolean isClient) {
        this.setName("ServerHeartBeatService");
        this.heartBeatMsg = heartBeatMsg;
        this.m_callBack = callBack;
        this.isClient = isClient;
        this.expireTimeInSeconds = expireTimeInSeconds;
        this.m_expireTimeInNanoSeconds = TimeUnit.NANOSECONDS.convert(expireTimeInSeconds, TimeUnit.SECONDS);

        m_clientHBMap = new ConcurrentHashMap<>();
    }

    public void startBeating() {
        m_bStart = true;
        if (getState() == State.NEW) {
            this.start();
        }
    }

    public void stopBeating() {
        m_bStart = false;
    }

    public void addClient(int clientId) {
        ClientHBInfo hbInfo = new ClientHBInfo(clientId, System.nanoTime());
        m_clientHBMap.put(clientId, hbInfo);
    }

    public void removeClient(int clientId) {
        m_clientHBMap.remove(clientId);
    }


    public void handleHeartBeatReq(int clientId, IMessage message) {

        ClientHBInfo hbInfo = m_clientHBMap.get(clientId);
        if (hbInfo == null) {
//            iRecvHandler.handlerHeartBeat(clientId, null);
        } else {
            hbInfo.m_LastSendTime = System.nanoTime();
//            iRecvHandler.handlerHeartBeat(clientId, message);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!m_bStart) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            long currentTime = System.nanoTime();

            final ArrayList<Integer> lostClients = new ArrayList<>();
            for (Map.Entry<Integer, ClientHBInfo> entry : m_clientHBMap.entrySet()) {
                ClientHBInfo clientHBInfo = entry.getValue();
                if (currentTime - clientHBInfo.m_LastSendTime < m_expireTimeInNanoSeconds * 2) {
                    continue;
                }
                lostClients.add(clientHBInfo.m_nClientID);
            }


            for (Integer clientId : lostClients) {
                m_clientHBMap.remove(clientId);
                if (m_callBack != null) {
                    m_callBack.remoteDidFailedToBeat(clientId);
                }
            }

            if (isClient) {
                for (Map.Entry<Integer, ClientHBInfo> entry : m_clientHBMap.entrySet()) {
                    ClientHBInfo clientHBInfo = entry.getValue();
                    if (currentTime - clientHBInfo.m_LastSendTime > m_expireTimeInNanoSeconds - TimeUnit.NANOSECONDS.convert(expireTimeInSeconds - 20, TimeUnit.SECONDS)) {
                        if (heartBeatMsg == null) {
                            continue;
                        }

                        boolean sendOk = ConnectManager.getInstance().sendCallback(clientHBInfo.m_nClientID, -1, heartBeatMsg, 10L, new CmdReqCallback() {

                            @Override
                            public void callback(IMessage msg) {
                                m_clientHBMap.get(lostClients.get(0)).m_LastSendTime = System.nanoTime();
                            }

                            @Override
                            public void timeOut() {
                                Log.d("socket", "callback time out");
                            }
                        });

                        if (!sendOk) {
                            if (ConnectManager.getInstance().send(clientHBInfo.m_nClientID, -1, heartBeatMsg)) {
                                m_clientHBMap.get(lostClients.get(0)).m_LastSendTime = System.nanoTime();
                            }
                        }

                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
