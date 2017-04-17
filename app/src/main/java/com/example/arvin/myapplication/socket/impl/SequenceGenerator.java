package com.example.arvin.myapplication.socket.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceGenerator {

    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    private SequenceGenerator() {
    }

    public static int generateSequenceId() {
        return atomicInteger.getAndIncrement();
    }
}
