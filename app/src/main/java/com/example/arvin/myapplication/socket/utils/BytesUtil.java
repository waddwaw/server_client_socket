package com.example.arvin.myapplication.socket.utils;

import java.nio.charset.Charset;

/**
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created��22 Dec 2014 16:58:28
 */

public class BytesUtil {


	/*  -----------------------start of   small Endian  --------------------*/

	public static char bytesToChar(byte[] bytes) {
		return (char) ((0xff00 & (bytes[1] << 8) | (0xff & bytes[0])));
	}

	public static short bytesToShort(byte[] b) {
		return (short) ((b[1] & 0xff) << 8 | (b[0] & 0xff));// << 8);
	}

	public static short bytesToShort(byte[] b, int offset) {
		return (short) (b[offset + 1] & 0xff << 8 | (b[offset] & 0xff) << 0);
	}

	public static int bytesToInt(byte[] b) {
		return (b[3] & 0xff) << 24 | (b[2] & 0xff) << 16 | (b[1] & 0xff) << 8 | (b[0] & 0xff) << 0;
	}

	public static long bytesToLong(byte[] array) {
		return ((((long) array[0] & 0xff) << 0) | (((long) array[1] & 0xff) << 8) | (((long) array[2] & 0xff) << 16)
				| (((long) array[3] & 0xff) << 24) | (((long) array[4] & 0xff) << 32)
				| (((long) array[5] & 0xff) << 40) | (((long) array[6] & 0xff) << 48) | (((long) array[7] & 0xff) << 56));
	}

	//------------------------------------------------------------------------------------
	public static byte[] toBytes(byte data) {
		byte[] bytes = new byte[1];
		bytes[0] = data;
		return bytes;
	}

	public static byte[] toBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] toBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] toBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] toBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] toBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}
/*  -----------------------end of   small End  --------------------*/


	/*  -----------------------start of   Big End  --------------------*/
	public static short getShort(byte[] bytes) {
		return (short) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
	}

	public static short getShort(byte[] bytes, int pos) {
		if (bytes.length < pos + 1)
			return 0;
		return (short) ((0xff & bytes[pos + 1]) | (0xff00 & (bytes[pos] << 8)));
	}

	public static char getChar(byte[] bytes) {
		return (char) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
	}

	public static char getChar(byte[] bytes, int pos) {
		if (bytes.length < pos + 1)
			return 0;
		return (char) ((0xff & bytes[pos + 1]) | (0xff00 & (bytes[pos] << 8)));
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[3]) | (0xff00 & (bytes[2] << 8)) | (0xff0000 & (bytes[1] << 16)) | (0xff000000 & (bytes[0] << 24));
	}

	public static int getInt(byte[] bytes, int pos) {
		if (bytes.length < pos + 3)
			return 0;
		return (0xff & bytes[pos + 3]) | (0xff00 & (bytes[pos + 2] << 8)) | (0xff0000 & (bytes[pos + 1] << 16)) | (0xff000000 & (bytes[pos] << 24));
	}

	public static long getLong(byte[] bytes) {
		return (0xffL & (long) bytes[7]) | (0xff00L & ((long) bytes[6] << 8)) | (0xff0000L & ((long) bytes[5] << 16)) | (0xff000000L & ((long) bytes[4] << 24))
				| (0xff00000000L & ((long) bytes[3] << 32)) | (0xff0000000000L & ((long) bytes[2] << 40)) | (0xff000000000000L & ((long) bytes[1] << 48)) | (0xff00000000000000L & ((long) bytes[0] << 56));
	}

	public static long getLong(byte[] bytes, int pos) {
		if (bytes.length < pos + 7)
			return 0;
		return (0xffL & (long) bytes[pos + 7]) | (0xff00L & ((long) bytes[pos + 6] << 8)) | (0xff0000L & ((long) bytes[pos + 5] << 16)) | (0xff000000L & ((long) bytes[pos + 4] << 24))
				| (0xff00000000L & ((long) bytes[pos + 3] << 32)) | (0xff0000000000L & ((long) bytes[pos + 2] << 40)) | (0xff000000000000L & ((long) bytes[pos + 1] << 48)) | (0xff00000000000000L & ((long) bytes[pos] << 56));
	}

	/***
	 * charsetName: utf-8, gbk
	 */
	public static String getString(byte[] bytes, String charsetName) {
		return new String(bytes, Charset.forName(charsetName));
	}

	public static String getString(byte[] bytes, int offset, int byteCount, String charsetName) {
		return new String(bytes, offset, byteCount, Charset.forName(charsetName));
	}

	//------------------------------------------------------------------------------------
	public static byte[] getBytes(byte data) {
		byte[] bytes = new byte[1];
		bytes[0] = data;
		return bytes;
	}

	public static byte[] getBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data & 0xff);
		bytes[0] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) (data);
		bytes[0] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (data & 0xff);
		bytes[2] = (byte) ((data & 0xff00) >> 8);
		bytes[1] = (byte) ((data & 0xff0000) >> 16);
		bytes[0] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] getBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[7] = (byte) (data & 0xff);
		bytes[6] = (byte) ((data >> 8) & 0xff);
		bytes[5] = (byte) ((data >> 16) & 0xff);
		bytes[4] = (byte) ((data >> 24) & 0xff);
		bytes[3] = (byte) ((data >> 32) & 0xff);
		bytes[2] = (byte) ((data >> 40) & 0xff);
		bytes[1] = (byte) ((data >> 48) & 0xff);
		bytes[0] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}

/*  -----------------------end of   Big End  --------------------*/

	public static long getMacID(byte[] bytes) {
		return ((((long) bytes[5] & 0xff)) | (((long) bytes[4] & 0xff) << 8) | (((long) bytes[3] & 0xff) << 16)
				| (((long) bytes[2] & 0xff) << 24) | (((long) bytes[1] & 0xff) << 32) | (((long) bytes[0] & 0xff) << 40));
	}

	public static int Ip2Int(int strIp) {
		return ((strIp << 24) | ((strIp & 0xff00) << 8) | ((strIp & 0xff0000) >> 8) | (strIp >> 24) & 0xff);
	}

	/**
	 * 将字符串型ip转成int型ip
	 *
	 * @param strIp
	 * @return
	 */
	public static int Ip2Int(String strIp) {
		String[] ss = strIp.split("\\.");
		if (ss.length != 4) {
			return 0;
		}
		byte[] bytes = new byte[ss.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(ss[i]);
		}
		bytes = arrayReverse(bytes);
		return BytesUtil.getInt(bytes);
	}

	public static byte[] arrayReverse(byte[] array) {
		byte[] reverse = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			reverse[array.length - (i + 1)] = array[i];
		}
		return reverse;
	}

	/**
	 * 将int型ip转成String型ip
	 *
	 * @param intIp
	 * @return
	 */
	public static String int2Ip(int intIp) {
		byte[] bytes = BytesUtil.getBytes(intIp);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			sb.append(bytes[i] & 0xFF);
			if (i < 3) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	public static String macByte2String(byte[] mac) {
		String macAddress = "";
		if (null == mac) {
			return macAddress;
		}
		for (int i = 0; i < mac.length; ++i) {
			if (i != 0) {
				macAddress += "-";
			}
			String hexString = Integer.toHexString(mac[i]);
			int hexLen = hexString.length();
			if (hexLen < 2) {
				macAddress += "0" + hexString;
			} else if (hexLen > 2) {
				macAddress += hexString.substring(hexLen - 2);
			} else {
				macAddress += hexString;
			}
		}

		return macAddress;
	}

	public static byte[] macString2Byte(String macAddress) {
		String[] subMacs = macAddress.split("-");
		if (null == subMacs) {
			return null;
		}
		byte[] mac = new byte[subMacs.length];
		int i = 0;
		for (String subMac : subMacs) {
			if ("".equals(subMac)) {
				continue;
			}
			mac[i] = (byte) Integer.parseInt(subMac, 16);
			++i;
		}

		return mac;
	}

	public static String byteToPrintString(byte[] bytes) {
		String printString = "";
		if (bytes != null) {
			for (byte byteData : bytes) {
				String hexString = Integer.toHexString(byteData);
				int hexLen = hexString.length();
				if (hexLen < 2) {
					printString += "0" + hexString;
				} else if (hexLen > 2) {
					printString += hexString.substring(hexLen - 2);
				} else {
					printString += hexString;
				}
				printString += ",";
			}
		}

		return printString;
	}

	public static String byteToPrintString(byte[] bytes, int pos, int length) {
		String printString = "";
		if (bytes != null) {
			int end = pos + length;
			end = end > bytes.length ? bytes.length : end;
			for (int i = pos; i < end; ++i) {
				String hexString = Integer.toHexString(bytes[i]);
				int hexLen = hexString.length();
				if (hexLen < 2) {
					printString += "0" + hexString;
				} else if (hexLen > 2) {
					printString += hexString.substring(hexLen - 2);
				} else {
					printString += hexString;
				}
				printString += ",";
			}
		}

		return printString;
	}

	public static boolean isEqual(byte[] array1, int pos1, byte[] array2, int pos2, int length) {
		if (null == array1 || null == array2 || array1.length < pos1 + length || array2.length < pos2 + length) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			if (array1[pos1++] != array2[pos2++]) {
				return false;
			}
		}

		return true;
	}

	public static boolean isEmptyArray(byte[] data) {
		if (null == data) {
			return false;
		}
		for (byte aData : data) {
			if (aData != 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 把16进制字符串转换成字节数组
	 * @param hex
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}
	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * 计算CRC
	 * @param buffer 字节数组
	 * @param length 从0到length字节
	 * @return
	 */
	public static int crc16(final byte[] buffer, int length) {
		int crc = 0;
		for (int j = 0; j < length; j++) {
			crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
			crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
			crc ^= ((crc & 0xff) >> 4);
			crc ^= (crc << 12) & 0xffff;
			crc ^= ((crc & 0xFF) << 5) & 0xffff;
		}
		crc &= 0xffff;
		return crc;
	}


	/**
	 * CRC校验，（后两位为校验位）
	 */
	public static boolean isCRCValid(byte[] msg) {
		short nCrc = (short) crc16(msg, msg.length - 2);
		short msgCrc = BytesUtil.getShort(msg, msg.length - 2);
		return msgCrc == nCrc;
	}

	public static int bcd2Int(byte[] data, int pos, int length) {
		int number = 0;
		for (int i=length-1; i>=0; --i) {
			number = number * 100 + ((data[pos+i] >>> 4) * 10 + (data[pos+i]&0x0F));
		}

		return number;
	}

	public static byte[] int2bcd(int number) {
		byte[] bcdByte = new byte[10];
		int i=0;
		for (; number > 0; ++i) {
			bcdByte[i] = (byte) (number % 10);
			number /= 10;

			if (number > 0) {
				bcdByte[i] |=  (byte)((number % 10) << 4);
				number /= 10;
			}
		}
		byte[] bcdRet = new byte[i>1 ? i : 1];
		System.arraycopy(bcdByte, 0, bcdRet, 0, i);
		return bcdRet;
	}

	public static void main(String[] args) {
		byte[] b2 = {1, 26};
		short x = getShort(b2);
		byte[] b4 = getBytes(x);

		System.out.println(x);
		System.out.println(b4[0] + "," + b4[1]);

		char ch = '5';
		byte[] bchar = getBytes(ch);
		System.out.println(bchar[0] + "," + bchar[1]);

		byte[] mac = {(byte) 255, 0, 0, 0, 0, 1};
		long macID = getMacID(mac);
		System.out.println("macID = " + macID);

		byte[] data = int2bcd(0);
		int dataInt = bcd2Int(data, 0, data.length);
		byte[] data1 = int2bcd(1);
		int dataInt1 = bcd2Int(data1, 0, data1.length);
		byte[] data2 = int2bcd(12);
		int dataInt2 = bcd2Int(data2, 0, data2.length);
		byte[] data3 = int2bcd(333);
		int dataInt3 = bcd2Int(data3, 0, data3.length);
		byte[] data4 = int2bcd(65535);
		int dataInt4 = bcd2Int(data4, 0, data4.length);
		byte[] data5 = int2bcd(2147483647);
		int dataInt5 = bcd2Int(data5, 0, data5.length);
		byte[] data6 = int2bcd(0);
		int dataInt6 = bcd2Int(data6, 0, data6.length);

	}
}
