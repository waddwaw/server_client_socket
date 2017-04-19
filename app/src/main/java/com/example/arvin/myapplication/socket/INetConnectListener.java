package com.example.arvin.myapplication.socket;

/**
 * Created by RyanLee on 2015/4/29.
 */
public interface INetConnectListener {

     void connectStatusChange(int serverID, boolean connected);

     void failedToConnect(int serverID, Exception e);
}
