package com.sankuai.sjst.nio.event;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NioWorker implements Worker{

    private Selector selector;

    private Queue<SocketChannel> newChannels = new ConcurrentLinkedQueue<>();

    private AtomicInteger channelsNum = new AtomicInteger();

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    private AtomicBoolean isShutdown = new AtomicBoolean();

    private String name;

    NioWorker(int index) throws IOException {
        this.selector = Selector.open();
        this.name = "nio-worker-" + index;
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isShutdown.get()) {
                        selector.select();
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> itr = keys.iterator();
                        while (itr.hasNext()) {
                            SelectionKey key = itr.next();
                            itr.remove();
                            handleRead(key);
                        }

                        //处理新的channel
                        handleNewChannel();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (isShutdown.get()) {
                    shutdownLatch.countDown();
                }
            }
        }).start();
    }

    @Override
    public void addChannel(Channel channel) {
        SocketChannel socketChannel = (SocketChannel) channel;
        newChannels.add(socketChannel);
        int total = channelsNum.incrementAndGet();
        System.out.println(name + " has " + total + " clients");
        selector.wakeup();

    }

    private void handleNewChannel() {
        SocketChannel channel;
        while ((channel = newChannels.poll()) != null) {
            try {
                channel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            isShutdown.compareAndSet(false, true);
            selector.wakeup();
            shutdownLatch.await();
            selector.close();
            System.out.println("Worker " + name + " shutdown.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        long bytesRead = socketChannel.read(readBuffer);
        if (bytesRead > 0) {
            readBuffer.flip();

            byte[] data = new byte[readBuffer.remaining()];
            readBuffer.get(data);
            String req = new String(data, "utf-8");
//            System.out.println(name + " receive from client: " + socketChannel.getRemoteAddress() + ". msg: " + req);
            if ("TIME".equalsIgnoreCase(req)) {
                ByteBuffer writeBuf = ByteBuffer.allocate(1024);
                writeBuf.put(new Date().toString().getBytes());
                writeBuf.flip();
                socketChannel.write(writeBuf);
            } else {
                ByteBuffer writeBuf = ByteBuffer.allocate(1024);
                writeBuf.put("BAD ORDER".getBytes());
                writeBuf.flip();
                socketChannel.write(writeBuf);
            }
        }
    }
}
