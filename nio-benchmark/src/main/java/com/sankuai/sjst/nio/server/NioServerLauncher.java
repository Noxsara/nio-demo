package com.sankuai.sjst.nio.server;

import com.sankuai.sjst.nio.Config;
import com.sankuai.sjst.nio.ConnectionManager;

public class NioServerLauncher {

    public static void main(String[] args) {
        Config config = new Config().setPort(9527);

        ConnectionManager manager = null;
        try {
            manager= new ConnectionManager(config);
            manager.start();
        } catch (Exception e) {
            if (manager != null) {
                manager.close();
            }
        }
    }
}
