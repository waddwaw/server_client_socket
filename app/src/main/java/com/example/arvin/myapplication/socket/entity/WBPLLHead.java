package com.example.arvin.myapplication.socket.entity;


import com.example.arvin.myapplication.ConstDef;

/**
 * Created by Ryan on 2016/4/14.
 */
public class WBPLLHead {
	static final public int WBPLLHEAD_NOWBPHEAD_LENGTH =  15;
	static final public int WBPLLHEAD_LENGTH = WBPHead.WBPHEAD_LENGTH + WBPLLHEAD_NOWBPHEAD_LENGTH;
	static final public int WBPLLHEAD_NOTAG_LENGTH = WBPHead.WBPHEAD_LENGTH - WBPHead.WBPHEADTAG_LENGTH + 15;
	static final public int WBPLLHEAD_HEARTBEAT_LENGTH = WBPHead.WBPHEAD_LENGTH + 13;

	static final public int WBPLLHEAD_PAYLOAD_MAX = 90;

	static final public byte WBP_FRAME_TYPE_DATA = 0x3;
	static final public byte WBP_FRAME_TYPE_HEARTBEAT = 0x7;

	public WBPHead head;
	public byte[]  dstMac = new byte[ConstDef.MAC_LEN];
	public byte[]  srcMac = new byte[ConstDef.MAC_LEN];
	public byte    frameType = WBP_FRAME_TYPE_DATA;

	public byte    packageCount = 1;
	public byte    packageIndex = 0;

	public WBPLLHead(WBPLLHead other) {
		if (null == other) {
			return;
		}
		this.head = new WBPHead(other.head);
		System.arraycopy(other.srcMac, 0, this.srcMac, 0, 6);
		System.arraycopy(other.dstMac, 0, this.dstMac, 0, 6);
		this.frameType = other.frameType;
		this.packageCount = other.packageCount;
		this.packageIndex = other.packageIndex;
	}
	public WBPLLHead(byte[] srcMac, byte[] dstMac) {
		this.head = new WBPHead();
		System.arraycopy(srcMac, 0, this.srcMac, 0, 6);
		System.arraycopy(dstMac, 0, this.dstMac, 0, 6);
	}

	public WBPLLHead(WBPHead head, byte[] bytes, int pos) {
		this.head = head;
		System.arraycopy(bytes, pos, this.dstMac, 0, 6);
		pos += ConstDef.MAC_LEN;
		System.arraycopy(bytes, pos, this.srcMac, 0, 6);
		pos += ConstDef.MAC_LEN;
		this.frameType = bytes[pos++];
		if (frameType == WBP_FRAME_TYPE_DATA) {
			this.packageCount = bytes[pos++];
			this.packageIndex = bytes[pos++];
		}
	}

	public byte[] getBytes() {
		if (null == head) {
			return null;
		}
		byte[] bytes = new byte[WBPLLHEAD_LENGTH];
		int pos = 0;
		System.arraycopy(head.getBytes(), 0, bytes, pos, WBPHead.WBPHEAD_LENGTH);
		pos += WBPHead.WBPHEAD_LENGTH;
		System.arraycopy(dstMac, 0, bytes, pos, ConstDef.MAC_LEN);
		pos += ConstDef.MAC_LEN;
		System.arraycopy(srcMac, 0, bytes, pos, ConstDef.MAC_LEN);
		pos += ConstDef.MAC_LEN;
		bytes[pos++] = this.frameType;
		bytes[pos++] = this.packageCount;
		bytes[pos++] = this.packageIndex;

		return bytes;
	}
}
