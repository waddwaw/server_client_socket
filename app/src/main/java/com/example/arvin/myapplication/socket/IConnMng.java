package com.example.arvin.myapplication.socket;

/**
 * 当读数据线程和写数据线程出现错误进行回调操作
 * Created by Ryan on 2016/2/22.
 */
public interface IConnMng {
	public void closeSocket(int nServerID);
}
