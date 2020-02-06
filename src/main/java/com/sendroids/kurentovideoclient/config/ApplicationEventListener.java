package com.sendroids.kurentovideoclient.config;


import com.sendroids.kurentovideoclient.SocketServer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationEventListener implements ApplicationListener {

    /**
     * WebSocketService 初始化时，手动注入
     */
    @Setter
    private static SocketServer socketServer;

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        // 应用关闭
        if (event instanceof ContextClosedEvent) {
            if (socketServer != null) {
                log.info("Received Server ClosedEvent...");
                socketServer.stopServer();
            }
        } else if (event instanceof ContextStoppedEvent) {
            if (socketServer != null) {
                log.info("Received Server StoppedEvent...");
                socketServer.stopServer();
            }
        }
    }
}
