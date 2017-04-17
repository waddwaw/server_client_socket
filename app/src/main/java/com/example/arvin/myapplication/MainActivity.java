package com.example.arvin.myapplication;

import android.net.wifi.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.arvin.myapplication.socket.ConnectManager;
import com.example.arvin.myapplication.socket.connect.IConnectPolicy;
import com.example.arvin.myapplication.socket.connect.INetConnectListener;
import com.example.arvin.myapplication.socket.impl.ClientConnectPolicy;
import com.example.arvin.myapplication.socket.impl.Transaction;
import com.example.arvin.myapplication.socket.test.TestMsg;
import com.example.arvin.myapplication.socket.test.TestRecvHander;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button startAP;
    Button wifiSan;
    Button stopWifi;
    Button openWifi;
    Button closeWifi;
    Button startSocket;
    Button clientSend;
    Button startServer;
    Button serverSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAP = (Button) findViewById(R.id.startAP);
        wifiSan = (Button) findViewById(R.id.wifiSan);
        stopWifi = (Button) findViewById(R.id.stopWifi);
        openWifi = (Button) findViewById(R.id.openWifi);
        closeWifi = (Button) findViewById(R.id.closeWifi);
        startSocket = (Button) findViewById(R.id.startSocket);
        clientSend = (Button) findViewById(R.id.clientSend);
        startServer = (Button) findViewById(R.id.sartServer);
        serverSend = (Button) findViewById(R.id.serverSend);


        final WifiTool wifiTool = new WifiTool(MainActivity.this);

        startAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiTool.startAp("abc" , "abc123456", true);
            }
        });

        wifiSan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wifiTool.isWifiOpen()) {
                    wifiTool.openWifi();
                }
                List<ScanResult> wifis = wifiTool.getScanSSIDsResult();
                Log.d("arvin", "ok");
                for (ScanResult wifi : wifis) {
                    Log.d("arvin", "SSID:" + wifi.SSID + "信号等级:" + wifi.level);
                }
            }
        });

        stopWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiTool.stopAp();
            }
        });

        openWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiTool.openWifi();
            }
        });

        closeWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiTool.closeWifi();
            }
        });


        startSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IConnectPolicy.ServerHost host = new IConnectPolicy.ServerHost();
                host.serverHost = "172.16.45.196";
                host.serverPort = 13911;
                List<IConnectPolicy.ServerHost> hosts = new ArrayList<>();
                hosts.add(host);
                ClientConnectPolicy policy = new ClientConnectPolicy(110, hosts, 1000 * 10, 10);
                TestRecvHander recvHander = new TestRecvHander();
                Transaction transaction = new Transaction();
                ConnectManager.getInstance().addClientConnect(policy, recvHander, transaction, new INetConnectListener() {
                    @Override
                    public void connectStatusChange(int serverID, boolean connected) {
                        Log.d("socket" ,"connectStatusChange:" + serverID  + "=====" + connected);
                    }

                    @Override
                    public void failedToConnect(int serverID, Exception e) {
                        Log.d("socket" ,"failedToConnect:" + serverID  + "=====" + e.toString());
                    }
                });
            }
        });

        clientSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestMsg msg = new TestMsg("tsst");
                boolean ok = ConnectManager.getInstance().send(110, -1, msg);
                Log.d("socket:", "sned : " + ok );
            }
        });


        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestRecvHander recvHander = new TestRecvHander();
                Transaction transaction = new Transaction();
                ConnectManager.getInstance().addServerConnect(111, 14411, recvHander, transaction, new INetConnectListener(){

                    @Override
                    public void connectStatusChange(int serverID, boolean connected) {
                        Log.d("socket" ,"connectStatusChange:" + serverID  + "=====" + connected);
                    }

                    @Override
                    public void failedToConnect(int serverID, Exception e) {
                        Log.d("socket" ,"failedToConnect:" + serverID  + "=====" + e.toString());
                    }
                });
            }
        });

        serverSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestMsg msg = new TestMsg("tsst");
                boolean ok = ConnectManager.getInstance().send(110, -1, msg);
                Log.d("socket:", "sned : " + ok );
            }
        });
    }
}
