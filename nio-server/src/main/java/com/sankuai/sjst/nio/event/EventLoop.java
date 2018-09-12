package com.sankuai.sjst.nio.event;

import java.io.IOException;
import java.nio.channels.Channel;

public interface EventLoop {

    /**
     * 启动nio事件循环
     */
    void start() throws IOException;

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 注册一个新的channel
     * @param newChannel
     */
    void register(Channel newChannel) throws IOException;
}
