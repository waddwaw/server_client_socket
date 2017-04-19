package com.example.arvin.myapplication.socket;


import com.example.arvin.myapplication.socket.entity.IMessage;

/**
 * 链接层初始化 接口
 * Created by arvin on 2017/4/13.
 */
public interface IConnect {

    /**
     * 链接服务初始化
     */
    void init();

    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void m_stop();

    /**
     * 释放资源
     */
    void uninit();

    /**
     * 重新启动服务
     */
    void restart();

    /**
     * @param clientID 如需知道需要指定发给谁 可以传-1 和无效的chlientID 可能发生消息失败 针对本身是server有效
     *   ************   如果自身是 Client 则不会使用本参数
     * @param msg
     * @return
     */
    boolean send(int clientID, IMessage msg);

    /**
     * @param clientID 如需知道需要指定发给谁 可以传-1 和无效的chlientID 可能发生消息失败 针对本身是server有效
     *                  如果自身是 Client 则不会使用本参数
     * @param msg
     * @param callback
     * @param timeout
     * @return
     */
    boolean sendCallback(int clientID, IMessage msg, long timeout, CmdReqCallback callback);

    /**
     * 异步发送
     *
     * @param serverKey 当前发送消息对象的链接类型 如 1云端 2本地局域网
     * @param msg
     * @param timeout 异步方式异步超时时间
     * @return
     */
    boolean syncSend(int serverKey, long timeout, IMessage msg);

    /**
     * 上一条异步消息发送成功 发送下一条
     */
    boolean syncSendNext();

    /**
     *设置网络连接状态监听
     */
    void addINetConnectListener(INetConnectListener listener);

}
