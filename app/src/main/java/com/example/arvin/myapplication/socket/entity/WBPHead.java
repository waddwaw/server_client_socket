package com.example.arvin.myapplication.socket.entity;


/**
 * Created by Ryan on 2016/4/14.
 */
public class WBPHead {
	public static final int WBPHEADTAG_LENGTH = 3; //tag 2 + length 1
	public static final int WBPHEAD_LENGTH = 11;
	public static final int WBPHEAD_DATA_LENGTH = 8;
	public static final int WBPHEAD_MSG_TYPE = 0x6;
	public static final int WBPHEAD_MSG_ACK_TYPE = 0x5;
	public static final byte[] WBPHEAD_TAG = {0x55, (byte) 0xaa};

	public byte[]  msgTag = WBPHEAD_TAG;
	public byte    length;
	public byte    msgType = WBPHead.WBPHEAD_MSG_TYPE;
	public byte    sequence;
	public byte[]  reverse = new byte[6];

	public WBPHead() {

	}

	public WBPHead(WBPHead other) {
		this.length = other.length;
		this.msgType = other.msgType;
		this.sequence = other.sequence;
		System.arraycopy(other.reverse, 0, this.reverse, 0, 6);
	}

	public WBPHead(int length, int msgType, int sequence) {
		this.length = (byte) length;
		this.msgType = (byte) msgType;
		this.sequence = (byte) sequence;
	}

	public WBPHead(byte[] bytes, int pos) {
		pos += 2;
		this.length = bytes[pos++];
		this.msgType = bytes[pos++];
		this.sequence = bytes[pos++];
		System.arraycopy(bytes, pos, this.reverse, 0, 6);
	}

	public static void updateLength(byte[] data, byte length) {
		if (data != null && data.length >= WBPHEADTAG_LENGTH) {
			data[2] = length;
		}
	}
	public static boolean isHead(byte[] headBytes, int pos) {
		return (headBytes[pos] == (byte)0x55 && headBytes[pos+1] == (byte)0xaa);
	}


	public byte[] getBytes() {
		byte[] bytes = new byte[WBPHEAD_LENGTH];
		int pos = 0;
		System.arraycopy(msgTag, 0, bytes, pos, 2);
		pos += 2;
		bytes[pos++] = this.length;
		bytes[pos++] = this.msgType;
		bytes[pos++] = this.sequence;
		System.arraycopy(this.reverse, 0, bytes, pos, 6);

		return bytes;
	}


}
