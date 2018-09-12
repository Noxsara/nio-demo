package com.sankuai.sjst.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Channel {

    private SocketChannel socketChannel;

    Channel (SocketChannel channel) {
        this.socketChannel = channel;
    }

    public String post(String msg) throws IOException{
        byte[] data = msg.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(data.length);
        writeBuffer.put(data);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);

        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        long readeBytes = socketChannel.read(readBuffer);
        if (readeBytes > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            return new String(bytes, "utf-8");
        }
        return null;
    }

    public void close() throws IOException{
        socketChannel.close();
    }
}
