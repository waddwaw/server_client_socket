package com.example.arvin.myapplication.socket.connect;


import com.example.arvin.myapplication.socket.impl.WBPAPLHead;
import com.example.arvin.myapplication.socket.impl.WBPLLHead;

/**
 * Created by RyanLee on 2015/2/14.
 * 说明：处理网络端接收到的数据请求，包括驱动端和云端的数据
 */
public class ILogicHandler {
    /**
     * 说明：处理双通道WiFi请求
     * 参数：recvMsg 接收到的字符串消息
     * 返回值：无
     * */
    public void handleWBPReq(int serverID, WBPAPLHead aplHead, WBPLLHead wbpllHead, byte[] recvMsg, int pos, int length){}
}
