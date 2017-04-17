package com.example.arvin.myapplication.socket.impl;


import com.example.arvin.myapplication.socket.connect.CmdReqCallback;

import java.util.Date;

/**
 * Created by arvin on 2016/12/30.
 */
public class TransactionInfo {

    private long sequenceId;
    private long createTime = new Date().getTime();
    private long timeOut;
    private CmdReqCallback callback;

    public long getCreateTime() {
        return createTime;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut * 1000;
    }

    public CmdReqCallback getCallback() {
        return callback;
    }

    public void setCallback(CmdReqCallback callback) {
        this.callback = callback;
    }
}
