package com.example.arvin.myapplication.socket.thread;

import android.util.Log;

import com.example.arvin.myapplication.socket.IConnMng;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;



public class UDPSendThread extends Thread {
	DatagramSocket m_sock;
	String m_strIP;
	short m_Port;

	public String getM_strIP() {
		return m_strIP;
	}

	public void setM_strIP(String m_strIP) {
		this.m_strIP = m_strIP;
	}

	public short getM_Port() {
		return m_Port;
	}

	public void setM_Port(short m_Port) {
		this.m_Port = m_Port;
	}

	//private Lock 			lock;
	private LinkedBlockingQueue<byte[]> m_queue;
	boolean m_bStart;
	IConnMng m_ConnectMng;
	int m_nServerID;
	boolean m_bClose = false;

	public UDPSendThread(IConnMng connectMng, Integer nServerID, String strServerIP, short nPort) {
		this.setName("UDPSendThread");
		m_ConnectMng = connectMng;
		m_nServerID = nServerID;
		m_strIP = strServerIP;
		m_Port = nPort;
		//lock = new ReentrantLock();
		m_queue = new LinkedBlockingQueue<byte[]>();
		m_bStart = false;
		start();
	}

	public void Start(DatagramSocket sock) {
		m_sock = sock;
		if (!m_bStart) {
			Log.d("socket" , "udp conn" + "Start");
			m_bStart = true;
		}
		Log.d("socket" , "start UDP send thread :" + getName() + "threadId:" + getId());
	}

	public void close() {
		Log.d("socket" , "close UDP send thread :" + getName() + "threadId:" + getId());
		m_bClose = true;
	}

	public void Stop() {
		m_bStart = false;
		Log.d("socket" , "udp conn" + "stop");
		Log.d("socket" , "start UDP send thread :" + getName() + "threadId:" + getId());
	}

	public void sendTo(byte[] message) {
		//lock.lock();
		try {
			m_queue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//lock.unlock();
	}

	public void run() {
		while (!m_bClose) {
			try {
				if (!m_bStart) {
					Thread.sleep(1000);
					continue;
				}

				if (null == m_sock || m_sock.isClosed()) {
					Thread.sleep(1000);
					continue;
				}
				//lock.lock();
				byte[] message = m_queue.take();
				if (null != message) {
					DatagramPacket dp = new DatagramPacket(message, message.length, InetAddress.getByName(m_strIP), m_Port);
					m_sock.send(dp);
				}
				//lock.unlock();

			} catch (Exception e) {
				//lock.unlock();
//				m_ConnectMng.closeUDPSocket(m_nServerID);
				e.printStackTrace();
			}

		}
	}
}
