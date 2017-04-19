package com.example.arvin.myapplication.socket.thread;

import android.util.Log;

import com.example.arvin.myapplication.ConstDef;
import com.example.arvin.myapplication.socket.IConnMng;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MsgSendThread extends Thread {
	IConnMng m_connectMng;
	int m_serverID;
	boolean m_bStart;
	boolean m_bClose;
	DataOutputStream m_DataOut;

	public class SendMsg {
		public byte[] m_msg;
		public boolean m_bEnable;
		public int m_waitTime;

		SendMsg(byte[] msg, int waitTime) {
			m_msg = msg;
			m_waitTime = waitTime;
			m_bEnable = true;
		}
	}

	private Lock lock;
	//private LinkedList<SendMsg> 			m_queue;
	private LinkedBlockingQueue<SendMsg> m_queue;
	private Queue<SendMsg> m_syncSendQueue;
	private boolean m_syncSendResult = true;

	public MsgSendThread(IConnMng connectMng, int serverID) {
		this.setName("MsgSendThread");

		m_connectMng = connectMng;
		m_serverID = serverID;
		lock = new ReentrantLock();
		m_queue = new LinkedBlockingQueue<>();
		m_syncSendQueue = new LinkedList<>();
		m_bStart = false;
		m_bClose = false;
	}

	public void close()
    {
		Log.d("socket" , "MsgSendThread close");
		m_queue.clear();
		m_bClose = true;
		interrupt();
	}

	public boolean Start(OutputStream outputStream) {
		m_DataOut = new DataOutputStream(outputStream);

		State state = getState();
		System.out.println(state);
		if (!m_bStart && (state == State.NEW)) {
			this.start();
		}
		Log.d("socket" , "start send thread ID:" + m_serverID + "thread:" + getName() + "threadId:" + getId());
		m_bStart = true;
		return true;
	}

	public void Stop() {
		m_bStart = false;
		if (m_DataOut != null) {
			try {
				m_DataOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//m_DataOut = null;
		}
		this.m_queue.clear();
		lock.lock();
		try {
			m_syncSendQueue.clear();
		} finally {
			lock.unlock();
		}
		Log.d("socket" , "stop send thread ID:" + m_serverID + "thread:" + getName() + "threadId:" +
				getId());
	}

	public boolean syncSend(byte[] message, int level, int waitTime) {
		if (!m_bStart) {
			return false;
		}
		SendMsg sendmsg = new SendMsg(message, waitTime);
		lock.lock();
		try {
			if (m_syncSendResult && m_syncSendQueue.isEmpty()) {
				m_syncSendResult = false;
				send(message, level, waitTime);
			} else {
				this.m_syncSendQueue.add(sendmsg);
			}
		} finally {
			lock.unlock();
		}

		return true;
	}

	public void syncSendNext() {
		lock.lock();
		try {
			m_syncSendResult = true;
			if (!m_syncSendQueue.isEmpty()) {
				SendMsg sendmsg = m_syncSendQueue.poll();

				if (sendmsg != null) {
					this.m_queue.add(sendmsg);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean send(byte[] message, int level, int waitTime) {
		if (!m_bStart) {
			return false;
		}
		SendMsg sendmsg = new SendMsg(message, waitTime);

		this.m_queue.add(sendmsg);
		return true;
	}

	public void run() {
		while (!m_bClose) {
			try {
				//if (!m_bStart) {
				//	Thread.sleep(100);
				//	continue;
				//}
				SendMsg sendmsg = m_queue.take();

				if (null != sendmsg) {
					if (sendmsg.m_bEnable) {
						if (null != m_DataOut) {
							//Logger.d(LogDef.LOG_SOCKET, "syncSend sending");
							m_DataOut.write(sendmsg.m_msg);
							m_DataOut.flush();

							if (sendmsg.m_waitTime > 0 && m_queue.size() > 0) {
								Thread.sleep(sendmsg.m_waitTime > ConstDef.CTRLDEV_TIME ? ConstDef.CTRLDEV_TIME : sendmsg.m_waitTime);
							}
						} else {
							Log.d("socket" , "closeSocket1 send sockete ID : " + m_serverID);
							m_connectMng.closeSocket(m_serverID);
						}
					}
				}

				//Thread.sleep(10);
			} catch (IOException e1) {
				if (null != m_connectMng) {
					Log.d("socket" , "closeSocket2 send sockete ID " + m_serverID);
					m_connectMng.closeSocket(m_serverID);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
