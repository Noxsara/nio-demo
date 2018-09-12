package com.sankuai.sjst.nio.event;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.Random;

public class WorkerEventLoop implements EventLoop{

    private Worker[] workers;

    WorkerEventLoop() throws IOException {
        int workersNum = Runtime.getRuntime().availableProcessors();
        this.workers = new NioWorker[workersNum];
        for (int index = 0; index < workersNum; index++) {
            Worker worker = new NioWorker(index);
            workers[index] = worker;
        }
    }

    @Override
    public void start() {
        for (Worker worker : workers) {
            worker.start();
        }
    }

    @Override
    public void register(Channel channel) {
        Worker worker = select();
        worker.addChannel(channel);
    }

    private Worker select() {
        Random random = new Random(System.nanoTime());
        int selectedIndex = random.nextInt(workers.length);
        return workers[selectedIndex];
    }

    @Override
    public void shutdown() {
        System.out.println("WorkerEventLoop shutdown.");
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }
}
