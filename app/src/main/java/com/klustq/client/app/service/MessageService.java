package com.klustq.client.app.service;

import com.klustq.client.app.UseApplication;
import com.klustq.client.lib.KlustqTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private final KlustqTemplate<String, String> klustqTemplate;

    @Autowired
    public MessageService(KlustqTemplate<String, String> klustqTemplate) {
        this.klustqTemplate = klustqTemplate;
    }

    public void sendMessage(){
        this.klustqTemplate.send("default", "KlustQ Client");
    }
}
