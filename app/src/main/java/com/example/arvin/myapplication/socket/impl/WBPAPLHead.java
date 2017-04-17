package com.example.arvin.myapplication.socket.impl;

/**
 * Created by Administrator on 2016/4/14.
 */
public class WBPAPLHead {
	public static final int WBPAPLHEAD_LENGTH = 2;

	public byte   protocolVersion;
	public byte    cmdID;

	public WBPAPLHead( byte cmdID) {
		this.cmdID = cmdID;
	}

	public WBPAPLHead(byte[] bytes, int pos) {
		this.protocolVersion = bytes[pos++];
		this.cmdID = bytes[pos];
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[WBPAPLHEAD_LENGTH];
		int pos = 0;
		bytes[pos++] = protocolVersion;
		bytes[pos++] = cmdID;

		return bytes;
	}
}
