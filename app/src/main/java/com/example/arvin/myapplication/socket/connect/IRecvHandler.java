package com.example.arvin.myapplication.socket.connect;

import com.example.arvin.myapplication.socket.impl.ServerHeartBeatService;

/**
 * Created by arvin .
 *
 */
public abstract class IRecvHandler<T extends IMessage> {

    public ITransInfo m_transInfo;
    public ServerHeartBeatService heartBeatService;

	public abstract T handleRecvMsg(int nServerID, byte[] recvMsg, int rcvSize);

    /**
     * 消息处理失败进行回调
     * @param nServerID
     */
    public abstract void clearRecvMsg(int nServerID);

    /**
     * 处理心跳消息
     * @param nServerID
     * @param message
     * @return
     */
    public abstract void handlerHeartBeat(int nServerID, IMessage message);

    /**
     * 获取发送心跳的 Msg
     * @return
     */
    public abstract T getHeartBeatMsg();

}
