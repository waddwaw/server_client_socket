package com.example.arvin.myapplication.socket.connect;


import java.util.List;

/**
 * 作为客户端连接服务器所需要的协议对象
 * Created by arvin on 2017/1/3.
 */
public interface IConnectPolicy {

    int nServerID();
    List<ServerHost> getServerHost();
    int minInterval();
    int heartbeatTime();

    class ServerHost {
        public String serverHost;
        public int serverPort;
    }
}
