package com.example.arvin.myapplication.socket.handler;

import android.util.Log;

import com.example.arvin.myapplication.ConstDef;
import com.example.arvin.myapplication.socket.ILogicHandler;
import com.example.arvin.myapplication.socket.entity.IMessage;
import com.example.arvin.myapplication.socket.IRecvHandler;
import com.example.arvin.myapplication.socket.ITransInfo;
import com.example.arvin.myapplication.socket.entity.WBPAPLHead;
import com.example.arvin.myapplication.socket.entity.WBPCommonAck;
import com.example.arvin.myapplication.socket.entity.WBPHead;
import com.example.arvin.myapplication.socket.entity.WBPLLHead;
import com.example.arvin.myapplication.socket.utils.BytesUtil;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by Ryan on 2016/4/19.
 */
public class WBPRcvHandler extends IRecvHandler {

	byte[] m_CtrlRcvBuffer;
	int m_nCtrlRcvPos;
	ILogicHandler m_logicHandler;
	Map<String, LLPartPackage> partPackageMap = new HashMap<>();

	byte[] ctrlMac;
	Map<Integer, byte[]> devMacs = new HashMap<>();

	WBPCommonAck wbpCommonAck = null;

	class LLPartPackage {
		byte[] mac = new byte[ConstDef.MAC_LEN];
		int packageCount;
		int packageIndex;
		byte[] data;
	}

	public WBPRcvHandler(ITransInfo transaction, ILogicHandler logicHandler, byte[] mac) {
		m_CtrlRcvBuffer = new byte[ConstDef.CONSUM_BUFF_SIZE];
		m_nCtrlRcvPos = 0;
		m_transInfo = transaction;
		m_logicHandler = logicHandler;
		ctrlMac = mac;
	}

	public void addDevMac(int serverID, byte[] mac) {
		devMacs.put(serverID, mac);
	}

	public byte[] deleteDevMac(int serverID) {
		return devMacs.remove(serverID);
	}

	private byte[] rellocBytes(byte[] existBuff, int newSize) {
		int extendSize = newSize - existBuff.length;
		int size = (extendSize / ConstDef.CONSUM_BUFF_SIZE + extendSize % ConstDef.CONSUM_BUFF_SIZE == 0 ? 0 : 1) * ConstDef.CONSUM_BUFF_SIZE + existBuff.length;
		byte[] allocBytes = new byte[size];
		System.arraycopy(existBuff, 0, allocBytes, 0, existBuff.length);
		return allocBytes;
	}

	@Override
	public IMessage handleRecvByteMsg(int nServerID, byte[] recvMsg, int rcvSize) {
		try {
			if (m_nCtrlRcvPos > 0 || rcvSize < WBPHead.WBPHEAD_LENGTH) {
				if (rcvSize + m_nCtrlRcvPos > m_CtrlRcvBuffer.length) {
					m_CtrlRcvBuffer = rellocBytes(m_CtrlRcvBuffer, rcvSize + m_nCtrlRcvPos);
					if (null == m_CtrlRcvBuffer) {
						Log.e("socket", "relloc bytes failed !!!");
						return null;
					}
				}

				System.arraycopy(recvMsg, 0, m_CtrlRcvBuffer, m_nCtrlRcvPos, rcvSize);
				m_nCtrlRcvPos += rcvSize;

				int nConsumPos = 0;
				while (true) {
					int nConsumSize = consumeBytes(nServerID, m_CtrlRcvBuffer, m_nCtrlRcvPos, nConsumPos);
					nConsumPos += nConsumSize;
					if (nConsumPos >= m_nCtrlRcvPos || nConsumSize == 0) {
						break;
					}
				}

				if (nConsumPos < m_nCtrlRcvPos && nConsumPos != 0) {
					System.arraycopy(m_CtrlRcvBuffer, nConsumPos, m_CtrlRcvBuffer, 0, m_nCtrlRcvPos - nConsumPos);
				}
				m_nCtrlRcvPos -= nConsumPos;
				if (m_nCtrlRcvPos < 0) {
					m_nCtrlRcvPos = 0;
				}
			} else {
				int nConsumPos = 0;
				while (true) {
					int nConsumSize = consumeBytes(nServerID, recvMsg, rcvSize, nConsumPos);
					nConsumPos += nConsumSize;
					if (nConsumPos >= rcvSize || nConsumSize == 0) {
						break;
					}
				}
				int nConsumLength = rcvSize - nConsumPos;
				if (nConsumLength > 0) {
					System.arraycopy(recvMsg, nConsumPos, m_CtrlRcvBuffer, 0, nConsumLength);
					m_nCtrlRcvPos = nConsumLength;
				}
			}
		} catch (Exception e) {
			m_nCtrlRcvPos = 0;
		}
		return null;
	}

	@Override
	public void clearRecvMsg(int nServerID) {
		partPackageMap.clear();
		m_nCtrlRcvPos = 0;
	}

	@Override
	public boolean handlerHeartBeat(int clientID, IMessage message) {
		//todo 验证是否是心跳
		if (message == null) {
			return false;
		}
		return  false;

	}

	@Override
	public void handleMsg(int nServerID, IMessage message) {

	}

	private int consumeBytes(int nServerID, byte[] bytes, int rcvSize, int consumPos) {
		int nConsumSize = 0;
		if (!WBPHead.isHead(bytes, consumPos)) {
			consumPos = findTag(bytes, consumPos, rcvSize);
			if (consumPos < 0) {
				return nConsumSize;
			}
			nConsumSize += consumPos;
		}
		WBPHead head = new WBPHead(bytes, consumPos);
		int nMsgLength = head.length + WBPHead.WBPHEADTAG_LENGTH;
		if (nMsgLength > rcvSize-consumPos) {
			return nConsumSize;
		}
		consumPos += WBPHead.WBPHEAD_LENGTH;

		if (nMsgLength == WBPCommonAck.WBP_COMMONACK_LENGTH) {
			m_transInfo.commitTrans(head.sequence, null);
			nConsumSize += nMsgLength;
		} else {
			WBPLLHead llHead = new WBPLLHead(head, bytes, consumPos);
			sendWBPACK(llHead.srcMac, head.sequence);
			if (llHead.frameType == WBPLLHead.WBP_FRAME_TYPE_HEARTBEAT) {
				m_logicHandler.handleWBPReq(nServerID, null, llHead, bytes, consumPos, llHead.head.length - WBPLLHead.WBPLLHEAD_NOTAG_LENGTH);
				return WBPLLHead.WBPLLHEAD_HEARTBEAT_LENGTH;
			}

			consumPos += WBPLLHead.WBPLLHEAD_NOWBPHEAD_LENGTH;
			if (llHead.packageCount==1 || llHead.packageCount == llHead.packageIndex + 1) {
				if (llHead.packageCount > 1 && llHead.packageCount > 1) {
					String macKey = BytesUtil.macByte2String(llHead.srcMac);
					LLPartPackage llPartPackage = partPackageMap.get(macKey);
					if (null != llPartPackage) {
						llPartPackage.packageIndex = llHead.packageIndex;
						byte[] data = new byte[llPartPackage.data.length + llHead.head.length - WBPLLHead.WBPLLHEAD_NOTAG_LENGTH];
						System.arraycopy(llPartPackage.data, 0, data, 0, llPartPackage.data.length);
						System.arraycopy(bytes, consumPos, data, llPartPackage.data.length, llHead.head.length - WBPLLHead.WBPLLHEAD_NOTAG_LENGTH);
						parseAPLBytes(nServerID, llHead, data, 0, data.length);
						partPackageMap.remove(macKey);
					} else {
						Log.e("socket", "WBP SERVICE drop part package count:" + llHead.packageCount + " index:" + llHead.packageIndex);
					}
				} else {
					parseAPLBytes(nServerID, llHead, bytes, consumPos, consumPos + llHead.head.length - WBPLLHead.WBPLLHEAD_NOTAG_LENGTH);
				}
			} else {
				LLPartPackage llPartPackage = new LLPartPackage();
				llPartPackage.packageCount = llHead.packageCount;
				llPartPackage.packageIndex = llHead.packageIndex;
				System.arraycopy(llHead.dstMac, 0, llPartPackage.mac, 0, ConstDef.MAC_LEN);
				System.arraycopy(bytes, consumPos, llPartPackage.data, 0, llHead.head.length - WBPLLHead.WBPLLHEAD_NOTAG_LENGTH);
				partPackageMap.put(BytesUtil.macByte2String(llHead.srcMac), llPartPackage);
			}
			nConsumSize += head.length + WBPHead.WBPHEADTAG_LENGTH;
		}

		return nConsumSize;
	}

	private void sendWBPACK(byte[] mac, int sequence) {
		/*
		DoubleChannelInfo dcInfo = DoubleChannelManager.instance().getDCInfo(mac);
		if (null != dcInfo && dcInfo.channelStatus== DoubleChannelInfo.WORKING) {
			if (null == wbpCommonAck) {
				WBPHead wbpHead = new WBPHead(WBPCommonAck.WBP_COMMONACK_LENGTH - WBPHead.WBPHEADTAG_LENGTH, WBPHead.WBPHEAD_MSG_ACK_TYPE, sequence);
				wbpCommonAck = new WBPCommonAck(wbpHead, 0);
			} else {
				wbpCommonAck.head.sequence = (byte) sequence;
			}

			ConnectManager.getInstance().send(dcInfo.connectID, 0, wbpCommonAck);
		}
		*/
	}


	private int findTag(byte[] bytes, int pos, int length) {
		for ( ;pos<length-1; ++pos) {
			if (BytesUtil.isEqual(bytes, pos, WBPHead.WBPHEAD_TAG, 0, 2)) {
				return pos;
			}
		}
		return -1;
	}



	private int parseAPLBytes(int nServerID, WBPLLHead wbpllHeads, byte[] bytes, int pos, int length) {
		WBPAPLHead wbpaplHead = new WBPAPLHead(bytes, pos);
		if (null != m_logicHandler) {
			m_logicHandler.handleWBPReq(nServerID, wbpaplHead, wbpllHeads, bytes, pos + WBPAPLHead.WBPAPLHEAD_LENGTH, length - WBPAPLHead.WBPAPLHEAD_LENGTH);
		} else {
			Log.e("socket", "WBP SERVICE null handler");
		}

		return 0;
	}
}
