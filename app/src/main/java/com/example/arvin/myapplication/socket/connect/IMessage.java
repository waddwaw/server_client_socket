package com.example.arvin.myapplication.socket.connect;

/**
 * 消息实体接口 所有消息必须实现此接口
 * Created by arvin on 2017/1/4.
 */
public interface IMessage {

    int getSequenceId();

    /**
     * 根据需要实现
     * @param seq
     */
    void setSequenceId(int seq);

    /**
     * 必须需实现 最后要发送的消息对象调用的方法
     * @return
     */
    byte[] getMessage();

    /**
     * 获取当前消息的级别
     * @return
     */
    int getLevel();

    /**
     * 获取发送消息的等待时间
     * @return
     */
    int getWaitTime();
}
