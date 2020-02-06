package com.sendroids.kurentovideoclient.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.kurento.client.KurentoObject;
import org.kurento.client.MediaPipeline;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.WebRtcEndpoint;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(of = {"sessionId"}, callSuper = true)
@EqualsAndHashCode(of = {"sessionId"}, callSuper = false)
public class User extends BaseEntity {
    UUID sessionId;

    WebRtcEndpoint endpoint;

    MediaPipeline pipeline;

    Collection<PlayerEndpoint> playerEndpoints;

    PlayerEndpoint player;

    public void rls() {
        this.pipeline.release();
        this.playerEndpoints.forEach(KurentoObject::release);
    }
}
