package com.sankuai.sjst.nio.event;

import java.nio.channels.Channel;

public interface Worker {

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 添加一个新的SocketChannel
     * @param channel
     */
    void addChannel(Channel channel);
}
