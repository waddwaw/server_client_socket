package com.example.arvin.myapplication.socket.impl;

import com.example.arvin.myapplication.socket.connect.IConnectPolicy;

import java.util.List;

/**
 * Created by arvin on 2017/4/13.
 */

public class ClientConnectPolicy implements IConnectPolicy {

    private List<ServerHost> hosts;
    private int minInterval;
    private int hearbeatTime;
    private int serverId;
    public ClientConnectPolicy(int id, List<ServerHost> host, int minInterval, int hearbeatTime) {
        this.serverId = id;
        this.hosts = host;
        this.minInterval = minInterval;
        this.hearbeatTime = hearbeatTime;
    }

    @Override
    public int nServerID() {
        return serverId;
    }

    @Override
    public List<ServerHost> getServerHost() {
        return hosts;
    }

    @Override
    public int minInterval() {
        return minInterval;
    }

    @Override
    public int heartbeatTime() {
        return hearbeatTime;
    }

}
