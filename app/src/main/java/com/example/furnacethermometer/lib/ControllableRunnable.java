package com.example.furnacethermometer.lib;

public class ControllableRunnable implements Runnable{
    private volatile boolean stopThread = false;
    @Override
    public void run() {

    }
    public void setStopThread(){
        stopThread=true;
    }
    public void setStartThread(){
        stopThread=false;
    }
}
