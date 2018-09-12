package com.sankuai.sjst.nio;

import com.sankuai.sjst.nio.event.BossEventLoop;

import java.io.IOException;

public class ConnectionManager {

    private BossEventLoop bossEventLoop;

    public ConnectionManager(Config config){
        bossEventLoop = new BossEventLoop(config);
    }

    public void start() throws IOException{
        bossEventLoop.start();
    }

    public void close() {
        bossEventLoop.shutdown();
    }
}
