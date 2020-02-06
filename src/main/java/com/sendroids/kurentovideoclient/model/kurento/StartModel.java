package com.sendroids.kurentovideoclient.model.kurento;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Collection;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartModel {
    String sdpOffer;
    String videoUrl;

}
