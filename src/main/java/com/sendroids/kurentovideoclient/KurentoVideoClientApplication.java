package com.sendroids.kurentovideoclient;

import com.sendroids.kurentovideoclient.config.ApplicationEventListener;
import lombok.val;
import org.kurento.client.KurentoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KurentoVideoClientApplication {

	@Bean
	public KurentoClient kurentoClient() {
		return KurentoClient.create();
	}

	public static void main(String[] args) {
		SpringApplication.run(KurentoVideoClientApplication.class, args);
	}

}
