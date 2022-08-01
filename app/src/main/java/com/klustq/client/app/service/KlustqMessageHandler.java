package com.klustq.client.app.service;

import com.klustq.client.lib.consumer.annotation.KlustqHandler;
import com.klustq.client.lib.consumer.annotation.KlustqListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@KlustqListener(topic = "default", group = "group_0")
@Component
public class KlustqMessageHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(KlustqMessageHandler.class);

    @KlustqHandler()
    public void handle(String message){

        LOGGER.info("From service " + message);
    }
}
