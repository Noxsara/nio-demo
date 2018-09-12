package com.sankuai.sjst.nio.event;

import com.sankuai.sjst.nio.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class BossEventLoop implements EventLoop{

    private Config poolConfig;

    private EventLoop workerEventLoop;

    private Selector selector;

    private AtomicBoolean isShutdown = new AtomicBoolean();

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    public BossEventLoop(Config poolConfig){
        this.poolConfig = poolConfig;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));
    }

    @Override
    public void start() throws IOException{
        System.out.println("nio-server: 127.0.0.1/" + poolConfig.getPort() + " started.");

        //启动子循环
        workerEventLoop = new WorkerEventLoop();
        workerEventLoop.start();

        //启动主循环
        selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        register(serverChannel);

        loop();

    }

    private void loop() throws IOException{
        while (!isShutdown.get()) {
            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> itr = keys.iterator();

            while (itr.hasNext()) {
                SelectionKey key = itr.next();
                if (key.isAcceptable()) {
                    itr.remove();
                    handleAccept(key);
                }

            }
        }
        shutdownLatch.countDown();
    }

    @Override
    public void register(Channel channel) throws IOException{
        ServerSocketChannel serverChannel = (ServerSocketChannel) channel;
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(poolConfig.getPort()));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void handleAccept(SelectionKey key) throws IOException{
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("new connection: "+ socketChannel.getRemoteAddress());
        workerEventLoop.register(socketChannel);
    }

    @Override
    public void shutdown() {
        try {
            //主线程关闭, 不处理新连接
            isShutdown.compareAndSet(false, true);
            selector.wakeup();

            shutdownLatch.await();
            selector.close();
            System.out.println("BossEventLoop shutdown.");

            //子线程关闭
            workerEventLoop.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
