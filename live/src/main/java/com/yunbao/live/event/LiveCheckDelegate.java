package com.yunbao.live.event;

public interface LiveCheckDelegate {
    public void doThing(LiveWaitToastType msg);
    public void showCost(String msg);
    public void saveThing(String msg);

    public enum LiveWaitToastType{
        Noraml,Finish,StartCount,ShouldToCharge,ShowCost,Showf3kOpen,Showf3kclose
    }
}
