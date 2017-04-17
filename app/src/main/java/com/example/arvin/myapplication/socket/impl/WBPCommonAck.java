package com.example.arvin.myapplication.socket.impl;

import com.example.arvin.myapplication.socket.connect.IMessage;

/**
 * Created by Ryan on 2016/4/14.
 */
public class WBPCommonAck extends IWBPByteMsg implements IMessage{
	public final static int WBP_COMMONACK_LENGTH = WBPHead.WBPHEAD_LENGTH + 1;
	public WBPHead head;
	public byte    errno;

	public WBPCommonAck(WBPHead head, int errno) {
		this.head = head;
		this.errno = (byte) errno;
	}

	public WBPCommonAck(WBPHead head, byte[] bytes, int pos) {
		this.head = head;
		this.errno = bytes[pos];
	}

	@Override
	public byte[] getBytes() {
		byte[] msg = new byte[WBPHead.WBPHEAD_LENGTH + 1];
		int nPos = 0;
		System.arraycopy(head.getBytes(), 0, msg, nPos, WBPHead.WBPHEAD_LENGTH);
		nPos += WBPHead.WBPHEAD_LENGTH;
		msg[nPos] = errno;

		return msg;
	}

	@Override
	public void setSequence(int seq) {

	}

	@Override
	public void setProtocolVer(byte version) {

	}

	@Override
	public short getCmdID() {
		return 0;
	}

	@Override
	public WBPLLHead getWBPLLHead() {
		return null;
	}


	@Override
	public int getSequenceId() {
		return 0;
	}

	@Override
	public void setSequenceId(int seq) {

	}

	@Override
	public byte[] getMessage() {
		return getBytes();
	}
}
