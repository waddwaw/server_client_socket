package com.example.arvin.myapplication.socket.impl;

import com.example.arvin.myapplication.socket.connect.CmdReqCallback;
import com.example.arvin.myapplication.socket.connect.IMessage;
import com.example.arvin.myapplication.socket.connect.ITransInfo;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Transaction extends Thread implements ITransInfo<Object> {

    private Map<Integer, TransactionInfo> m_transInfoMap;
    private Lock lock;
    private boolean m_bStart;
    private int serverId;

    public Transaction() {
        this.setName("Transaction");
        m_transInfoMap = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
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
        int m_nSequence = SequenceGenerator.generateSequenceId();
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setTimeOut(timeout);
        transactionInfo.setCallback(callback);
        transactionInfo.setSequenceId(m_nSequence);
        lock.lock();
        try {
            m_transInfoMap.put(m_nSequence, transactionInfo);
        } finally {
            lock.unlock();
        }
        return m_nSequence;
    }

    @Override
    public boolean commitTrans(int seq, Object msg) {
        if (seq < 0) {
            return false;
        }
        lock.lock();
        TransactionInfo transinfo;
        try {
            transinfo = m_transInfoMap.remove(seq);
        } finally {
            lock.unlock();
        }
        if (null != transinfo && null != transinfo.getCallback()) {
            transinfo.getCallback().callback((IMessage) msg);
            return true;
        }
        return false;
    }

    public void run() {
        while (true) {
            if (!m_bStart) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            for (Map.Entry<Integer, TransactionInfo> entry : m_transInfoMap.entrySet()) {
                if ((entry.getValue().getCreateTime() + entry.getValue().getTimeOut()) < new Date().getTime()) {
                    entry.getValue().getCallback().timeOut();
                    m_transInfoMap.remove(entry.getKey());
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
} 
