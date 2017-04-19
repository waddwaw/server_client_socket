package com.example.arvin.myapplication.socket;

/**
 * 心跳超时和处理 进行失败回调
 * Created by zengbin on 4/29/16.
 * Copyright © 2016 GuangTian. All rights reserved.
 */
public interface IHeartBeatCallBack {
    public void remoteDidFailedToBeat(int clientId);
}
