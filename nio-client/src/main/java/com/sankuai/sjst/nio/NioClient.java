package com.sankuai.sjst.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioClient {

    private NioClient() {}

    public static NioClient instance() {
        return InstanceHolder.CLIENT;
    }

    public Channel connect(String ip, int port) throws IOException{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress(ip, port));
        if (socketChannel.isConnected()) {
            return new Channel(socketChannel);
        }
        return null;
    }

    private static class InstanceHolder{
        static NioClient CLIENT = new NioClient();
    }
}
