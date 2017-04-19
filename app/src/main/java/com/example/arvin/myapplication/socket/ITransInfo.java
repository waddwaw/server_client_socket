package com.example.arvin.myapplication.socket;

/**
 * Created by arvin on 2017/1/4.
 */
public interface ITransInfo<T> {
    /**
     * 开始事务
     * @param timeout
     * @param callback
     * @return
     */
    int beginTrans(long timeout, CmdReqCallback callback);

    /**
     * 处理事务
     * @param seq
     * @param msg
     * @return
     */
    boolean commitTrans(int seq, T msg);

    /**
     * 启动事务
     */
    void m_start();

    /**
     * 停止事务
     */
    void m_stop();

    /**
     * 设置当前处理事务的serviceID or serverKey
     * @param id
     */
    void setServiceId(int id);

}
