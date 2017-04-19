package com.example.arvin.myapplication.socket.thread;

import android.util.Log;

import com.example.arvin.myapplication.socket.IConnMng;
import com.example.arvin.myapplication.socket.IRecvHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class UDPRecvThread extends Thread{
	DatagramSocket 		m_sock;
	IConnMng m_ConnectMng;
	int 				m_nServerID;
	boolean				m_bStart;
	IRecvHandler m_recvHandler;
	Lock 				lock;
	byte[] 				m_buffer;
	boolean			m_bClose = false;
	
	public UDPRecvThread(IConnMng connectMng, int nServerID) {
		this.setName("UDPRecvThread");
		m_ConnectMng = connectMng;
		m_nServerID = nServerID;
		m_bStart = false;
		lock = new ReentrantLock();
		m_buffer = new byte[4096];
		this.start();
	}
	
	public void setRecvHandler(IRecvHandler recvHandler) {
		m_recvHandler = recvHandler;
	}
	
	public void Start(DatagramSocket udpSock) {
		m_sock = udpSock;
		if (!m_bStart) {
			m_bStart = true;
		}
		Log.d("socket" , "start UDP recv thread :" + getName() + "threadId:" + getId());
	}
	
	public void Stop() {
		m_bStart = false;
		Log.d("socket" , "stop UDP recv thread :" + getName() + "threadId:" + getId());
}

	public void close() {
		m_bClose = true;
		Log.d("socket" , "close UDP recv thread :" + getName() + "threadId:" + getId());
	}
	
	public void run() {
		while (!m_bClose){
			try {
				if (!m_bStart){
					Thread.sleep(1000);
					continue;
				}
				
				if (null == m_sock || m_sock.isClosed()) {
					Thread.sleep(1000);
					continue;
				}
				
				DatagramPacket dp = new DatagramPacket(m_buffer, m_buffer.length);    
				m_sock.receive(dp);    
				 
				if (null != m_recvHandler){
					m_recvHandler.handleRecvByteMsg(m_nServerID, m_buffer, dp.getLength());
				}
			       
				Thread.sleep(100);
			} catch (Exception e) {
//				m_ConnectMng.closeSocket(m_nServerID);
				e.printStackTrace();
			}
		}
		
	}
}
