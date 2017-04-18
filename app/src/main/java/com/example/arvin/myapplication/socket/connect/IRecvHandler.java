package com.example.arvin.myapplication.socket.connect;

import com.example.arvin.myapplication.socket.impl.ServerHeartBeatService;

/**
 * Created by arvin .
 *
 */
public abstract class IRecvHandler<T extends IMessage> {

    public ITransInfo m_transInfo;
    public ServerHeartBeatService heartBeatService;

    /**
     * 解析二进制消息 并转化成 IMessage 实体
     * @param nServerID
     * @param recvMsg
     * @param rcvSize
     * @return
     */
	public abstract T handleRecvByteMsg(int nServerID, byte[] recvMsg, int rcvSize);

    /**
     * 消息处理失败进行回调
     * @param nServerID
     */
    public abstract void clearRecvMsg(int nServerID);

    /**
     * 处理心跳消息 验证此消息是否为心跳包 返回boolean
     * @param nServerID
     * @param message
     * @return
     */
    public abstract boolean handlerHeartBeat(int nServerID, IMessage message);

    /**
     * 业务逻辑包
     * @param t
     */
    public abstract void handleMsg(int nServerID, T t);

    /**
     * 获取发送心跳的 Msg
     * @return
     */
    public abstract T getHeartBeatMsg();

    /**
     * 优先执行是否有事务存在 如果有则进行提交
     * 其次检查是否是心跳包
     * 最后交给业务层进行处理
     * @param nServerID
     * @param recvMsg
     * @param rcvSize
     */
    public void handlerMsg(int nServerID, byte[] recvMsg, int rcvSize) {
        T t = handleRecvByteMsg(nServerID, recvMsg, rcvSize);
        boolean isCommitTrans = m_transInfo.commitTrans(t.getSequenceId(), t);
        if (isCommitTrans) {
            return;
        }
        boolean isHearBeat = handlerHeartBeat(nServerID, t);
        if (isHearBeat) {
            return;
        }
        handleMsg(nServerID, t);
    }


}
