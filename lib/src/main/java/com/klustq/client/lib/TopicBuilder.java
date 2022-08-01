package com.klustq.client.lib;

import com.klustq.client.lib.common.Constants;
import com.klustq.client.lib.common.SpringUtils;
import com.klustq.client.lib.configs.KlustQConfigurationProperties;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class TopicBuilder {

    KlustQConfigurationProperties kProps =
            (KlustQConfigurationProperties) SpringUtils.ctx.getBean("kProps");

    private final String topicsResourceUrl = String.format(
            "http://%s%s", kProps.getBootstrapServer(), Constants.CREATE_DELETE_TOPIC_URL
    );

    private final String name;

    private TopicBuilder(String name){
        this.name = name;
        System.out.println("Topic Remote url: " + topicsResourceUrl);
    }

    public Topic build(){
        final boolean[] isSuccess = {false};

        final Thread thread = new Thread(() -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                //May we will need to use it later
                /*RequestCallback c = clientHttpRequest -> {
                    clientHttpRequest.getHeaders().set("Content-Type", "application/json");
                };*/

                restTemplate.postForEntity(
                        new URI(this.topicsResourceUrl.replace("[name]", this.name)),
                        null, String.class
                );
                isSuccess[0] = true;

            } catch (RestClientException | URISyntaxException rce){
                //TODO log the error
                //We have the insurance that the topic no matter
                //it already exists or not. Therefore if we have an error
                //then this error is very significant
                rce.printStackTrace();
            }
        });

        thread.start();
        try {
            thread.join();
            if (isSuccess[0]) {
                //KlustQ.topics.add(this.name);
            }
            return new Topic(this.name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TopicBuilder name(String topicName){
        return new TopicBuilder(topicName);
    }
}
