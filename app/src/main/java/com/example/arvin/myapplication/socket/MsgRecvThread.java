package com.example.arvin.myapplication.socket;

import android.util.Log;

import com.example.arvin.myapplication.ConstDef;
import com.example.arvin.myapplication.socket.connect.IConnMng;
import com.example.arvin.myapplication.socket.connect.IRecvHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MsgRecvThread extends Thread {
	IConnMng m_connectMng;
	IRecvHandler m_recvHandler;
	InputStream m_DataIn;
	Lock lock;
	byte[] m_buffer;
	int m_serverID;
	boolean m_bStart;
	boolean m_bClose = false;

	public MsgRecvThread(IConnMng connectMng, int serverID) {
		this.setName("MsgRecvThread");
		m_connectMng = connectMng;
		lock = new ReentrantLock();
		m_buffer = new byte[ConstDef.RECV_BUFF_SIZE + 1];
		m_bStart = false;
		m_serverID = serverID;
		m_DataIn = null;
	}

	public void close() {
		m_bClose = true;
	}

	public void Start(InputStream inputStream) {
		m_DataIn = inputStream;

		if (!m_bStart && getState() == State.NEW) {
			this.start();
		}
		Log.d("socket" ,"start recv thread ID:" + m_serverID + "thread:" + getName() + "threadId:" + getId());
		m_bStart = true;
	}

	public void Stop() {
		m_bStart = false;
		Log.d("socket" , "stop recv thread ID:" + m_serverID + "thread:" + getName() + "threadId:" + getId());
		if (null != m_DataIn) {
			try {
				m_DataIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			m_DataIn = null;
		}
		if (null != m_recvHandler) {
			m_recvHandler.clearRecvMsg(m_serverID);
		}
	}

	public void setRecvHandler(IRecvHandler recvHandler) {
		m_recvHandler = recvHandler;
	}

	public void run() {
		while (!m_bClose) {
			try {
				if (!m_bStart) {
					Thread.sleep(100);
					continue;
				}

				int nRcv = m_DataIn.read(m_buffer, 0, ConstDef.RECV_BUFF_SIZE);
				if (nRcv < 0) {
					Log.d("socket" ,"closeSocket recv sockete1 severID:" + m_serverID + " recieve : " + nRcv);
					m_connectMng.closeSocket(m_serverID);
					if (null != m_recvHandler) {
						m_recvHandler.clearRecvMsg(m_serverID);
					}
					continue;
				}
				//Logger.fd(LogDef.LOG_SOCKET, "revicedata: "+nRcv+ "--server:"+m_serverID);
				if (null != m_recvHandler && nRcv > 0) {
					m_recvHandler.handleRecvMsg(m_serverID, m_buffer, nRcv);
				}

			} catch (IOException e) {
				Log.d("socket", "close recv sockete2 ID " + m_serverID);
				m_connectMng.closeSocket(m_serverID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
