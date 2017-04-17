package com.example.arvin.myapplication.DoubleChannel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ryan on 2016/4/13.
 */
public class DoubleChannelInfo {
	public final static int NONEVERSION = 0; //未获取过版本号
	public final static int DISCONNECT = 1; //未连接
	public final static int CONNECTING = 2; //正在连接
	public final static int IDLE = 3; //空闲
	public final static int WORKING = 4; //工作

	public int connectID;
	public List<Integer> devIDs;
	public int channelStatus;
	public String mac;
	public String ip;
	public int port;

	public int setWifiTimes;

	public DoubleChannelInfo(int devID, int connectID, String mac, String ip, int port) {
		devIDs = new ArrayList<>();
		this.devIDs.add(devID);
		this.connectID = connectID;
		this.mac = mac;
		this.ip = ip;
		this.port = port;
		setChannelStatus(NONEVERSION);
	}

	public void reset() {
		setChannelStatus(NONEVERSION);
		ip = "";
		port = 0;
		setWifiTimes = 0;
	}

	public void setChannelStatus(int channelStatus) {
		this.channelStatus = channelStatus;
		Log.d("socket", "double channel status " + channelStatus + " (" + mac + ")");
	}
}
