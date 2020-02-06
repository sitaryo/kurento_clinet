package com.sendroids.kurentovideoclient;

import com.corundumstudio.socketio.SocketIOServer;
import com.sendroids.kurentovideoclient.config.ApplicationEventListener;
import lombok.extern.slf4j.Slf4j;
import com.corundumstudio.socketio.Configuration;

@Slf4j
public class SocketServer {

    private static final int SOCKET_SERVER_PORT = 9090;

    private final static SocketIOServer server =
            new SocketIOServer(SocketServer.getServerConfig());

    private static volatile boolean isStart;
    private final static SocketServer serverWrapper = new SocketServer();

    public synchronized static SocketIOServer init() {
        if (isStart) {
            return server;
        }
        return serverWrapper.startServer();
    }

    // 单独启动测试用
    public static void main(String[] args) {
        serverWrapper.startServer();
    }

    // 启动服务
    public synchronized SocketIOServer startServer() {
        if (isStart) {
            log.warn("Web Socket Server already started on port " + SOCKET_SERVER_PORT);
            return null;
        }
        server.start();
        log.info("Web Socket Server started on port " + SOCKET_SERVER_PORT);

        // 注入程序关闭的监听器
        ApplicationEventListener.setSocketServer(this);

        isStart = true;
        return server;
    }

    // 关闭服务
    public synchronized void stopServer() {
        if (isStart) {
            server.stop();
            log.info("Web Socket Server stopped. ");
        } else {
            log.warn("Web Socket Server already stopped");
        }
    }


    /**
     * web socket config
     *
     * @return config
     */
    private static Configuration getServerConfig() {
        // 创建Socket，并设置监听端口
        Configuration config = new Configuration();
        //设置主机名
        config.setHostname("0.0.0.0");
//        config.setAllowCustomRequests(true);
        //设置监听端口
        config.setPort(SOCKET_SERVER_PORT);
//        config.setOrigin("*");
//        config.setContext("/socket");
        // 协议升级超时时间（毫秒），默认10秒。HTTP握手升级为ws协议超时时间
        config.setUpgradeTimeout(10000);
        // Ping消息超时时间（毫秒），默认60秒，这个时间间隔内没有接收到心跳消息就会发送超时事件
        config.setPingTimeout(180000);
        // Ping消息间隔（毫秒），默认25秒。客户端向服务器发送一条心跳消息间隔
        config.setPingInterval(60000);

        log.info("init socket IO : " + config.getHostname() + ":" + config.getPort() +
                "; upgradeTimeout : " + config.getUpgradeTimeout() / 1000 + "; pingTimeout : " + config.getPingTimeout() / 1000);

        return config;
    }
}
