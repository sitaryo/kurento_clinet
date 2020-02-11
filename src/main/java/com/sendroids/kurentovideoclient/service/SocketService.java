package com.sendroids.kurentovideoclient.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.sendroids.kurentovideoclient.SocketServer;
import com.sendroids.kurentovideoclient.entity.Some;
import com.sendroids.kurentovideoclient.entity.User;
import com.sendroids.kurentovideoclient.model.kurento.StartModel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SocketService {
    private final SocketIOServer server;
    private final KurentoClient kurento;
    private final Map<UUID, User> onlineUsers = new HashMap<>();


    @Autowired
    public SocketService(
            KurentoClient kurento
    ) {
        this.kurento = kurento;
        this.server = SocketServer.init();
        this.addListener();
    }

    private void addListener() {

        server.addConnectListener(
                client -> log.info("First connect, wait for connect-auth, clientId={}", client.getSessionId()));

        server.addDisconnectListener(
                client -> log.info("{} disconnect from server ", client.getSessionId())
        );
        server.addEventListener("hello", Some.class, ((client, data, ackSender) -> {
            log.info("hello data {}", data);
        }));
        this.server.addEventListener("start", StartModel.class, (client, msg, request) -> {
            log.info("start data {}", msg);
            final User user = new User();
            user.setSessionId(client.getSessionId());
            // 新建 pipeline 和 endpoint
            val pipeline = kurento.createMediaPipeline();
            val endpoint = new WebRtcEndpoint.Builder(pipeline).build();
            val player = new PlayerEndpoint.Builder(pipeline, msg.getVideoUrl()).build();

            // 将参数set 进user
            user.setPipeline(pipeline);
            user.setEndpoint(endpoint);
            user.setPlayer(player);

            // 添加user入库
            onlineUsers.put(user.getSessionId(), user);

            // 连接 播放端 和 视频端
            player.connect(endpoint);

            endpoint.addIceCandidateFoundListener(
                    event -> client.sendEvent("iceCandidate", event.getCandidate()));

            val sdpAnswer = endpoint.processOffer(msg.getSdpOffer());
            client.sendEvent("startResponse", sdpAnswer);

            endpoint.gatherCandidates();

            player.addErrorListener(event -> {
                log.info("ErrorEvent: {}", event.getDescription());
                client.sendEvent("playEnd");
            });

            player.addEndOfStreamListener(event -> {
                log.info("EndOfStreamEvent: {}", event.getTimestamp());
                client.sendEvent("playEnd");
            });

            player.play();
        });

        this.server.addEventListener("onIceCandidate", IceCandidate.class, (client, ice, request) -> {
            log.info("onIceCandidate data candidate:{} sdpMid:{} sdpMLineIndex:{}",
                    ice.getCandidate(), ice.getSdpMid(), ice.getSdpMLineIndex());
            val onlineUser = onlineUsers.get(client.getSessionId());
            Optional.of(onlineUser).ifPresent(user -> user.getEndpoint().addIceCandidate(ice));
        });

        this.server.addEventListener("stop", Object.class,
                (client, msg, request) -> this.userRls(client));
    }

    private void userRls(SocketIOClient client) {
        val user = onlineUsers.remove(client.getSessionId());
        user.rls();
    }
}
