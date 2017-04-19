package com.example.arvin.myapplication.socket;

import android.util.Log;

import com.example.arvin.myapplication.socket.ConnectManager;
import com.example.arvin.myapplication.socket.CmdReqCallback;
import com.example.arvin.myapplication.socket.ITransInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ryan on 2016/4/15.
 */
public class SyncSendTransaction extends Thread implements ITransInfo {
    public class SyncSendTransInfo {
        public long m_lTime;
        public int m_timeout;
        public int m_serverID;
    }

    Map<Byte, SyncSendTransInfo> m_transInfoMap;
    byte m_nSequence;
    boolean m_bStart;
    int serverId;

    public SyncSendTransaction() {
        this.setName("SyncSendTransaction");
        m_transInfoMap = new ConcurrentHashMap<>();
        m_nSequence = 1;
        m_bStart = false;
    }

    @Override
    public void m_start() {
        m_bStart = true;
        if (State.NEW == getState()) {
            this.start();
        }
    }

    @Override
    public void m_stop() {
        m_bStart = false;
    }

    @Override
    public void setServiceId(int id) {
        this.serverId = id;
    }

    @Override
    public int beginTrans(long timeout, CmdReqCallback callback) {
        m_nSequence++;
        SyncSendTransInfo transinfo = new SyncSendTransInfo();
        transinfo.m_lTime = new Date().getTime();
        transinfo.m_timeout = (int) timeout;
        transinfo.m_serverID = serverId;


        m_transInfoMap.put(m_nSequence, transinfo);
        Log.d("socket", "SyncSendTransaction beginTrans  sequence " + m_nSequence);

        return m_nSequence;
    }

    @Override
    public boolean commitTrans(int seq, Object msg) {

        SyncSendTransInfo transinfo;

        transinfo = m_transInfoMap.remove(seq);

        if (null != transinfo) {
            //Logger.i(LogDef.LOG_MSG, "SyncSendTransaction commites sequence success" + nSeqence);
            ConnectManager.getInstance().syncSendNext(transinfo.m_serverID);
            return true;
        } else {
            Log.d("socket", "SyncSendTransaction commites sequence faile" + seq);
        }
        return false;
    }

    public void run() {
        while (true) {
            try {
                if (!m_bStart) {
                    sleep(300);
                    break;
                }

                SyncSendTransInfo transinfo = null;
                List<Integer> syncServers = new ArrayList<>();

                if (!m_transInfoMap.isEmpty()) {
                    long lTime = System.currentTimeMillis();
                    Iterator<Map.Entry<Byte, SyncSendTransInfo>> it = m_transInfoMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Byte, SyncSendTransInfo> entry = it.next();
                        transinfo = entry.getValue();
                        if ((lTime - transinfo.m_lTime) / 1000 > transinfo.m_timeout) {
                            it.remove();
                            Log.d("socket", "SyncSendTransaction timeout  sequence " + entry.getKey());
                            syncServers.add(transinfo.m_serverID);
                        }
                    }
                }

                for (Integer aServer : syncServers) {
                    ConnectManager.getInstance().syncSendNext(aServer);
                }
                if (null == transinfo) {
                    sleep(300);
                }
            } catch (Exception e) {
            }
        }
    }
}
