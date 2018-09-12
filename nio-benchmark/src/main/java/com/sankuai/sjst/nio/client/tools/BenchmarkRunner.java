package com.sankuai.sjst.nio.client.tools;

import com.sankuai.sjst.nio.Channel;
import com.sankuai.sjst.nio.NioClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class BenchmarkRunner {

    private static final String ip = "127.0.0.1";
    private static final int port = 9527;

    public static RunResult run(int threads) throws Exception{
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch mainLatch = new CountDownLatch(threads);
        AtomicLong theadsCost = new AtomicLong();

        for (int i = 0; i < threads; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();

                        long start = System.currentTimeMillis();
                        Channel channel = NioClient.instance().connect(ip, port);
                        if (channel == null) {
                            throw new IllegalStateException("not connected.");
                        }
                        String msg = channel.post("TIME");
                        channel.close();
                        long cost = System.currentTimeMillis() - start;
                        theadsCost.getAndAdd(cost);

//                        System.out.println(msg);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mainLatch.countDown();
                    }
                }
            }).start();
        }
        //确保所有线程都跑到 latch.await();
        Thread.sleep(1000 * 2);

        //开始时间
        long start = System.currentTimeMillis();
        latch.countDown();

        //结束时间
        mainLatch.await();
        long end = System.currentTimeMillis();

        //总共耗时
        long cost = end - start;

        RunResult result = new RunResult();
        result.setThreads(threads);
        result.setTotalCost(cost);
        result.setAverage(theadsCost.get() / threads);

        return result;

    }
}
