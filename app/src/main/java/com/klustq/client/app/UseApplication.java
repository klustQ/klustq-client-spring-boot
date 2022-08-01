package com.klustq.client.app;

import com.klustq.client.app.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.klustq.client"})
public class UseApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(UseApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UseApplication.class, args);
	}

	@Autowired
	MessageService service;

	@Bean
	CommandLineRunner run() {
		return (args) -> {
			for (int i = 0; i < 10; i++) {
				service.sendMessage();

			}
		};
	};
}
