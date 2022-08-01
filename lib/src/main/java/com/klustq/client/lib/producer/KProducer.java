package com.klustq.client.lib.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klustq.client.lib.common.Constants;
import com.klustq.client.lib.common.dto.MessagePayloadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KProducer {

    private final static Logger LOGGER = LoggerFactory.getLogger(KProducer.class);

    private final String topic;
    private final String endpoint;

    private final ExecutorService threadPoolExecutor;

    /**
     * Constructor
     *
     * @param topic  the topic name
     * @param server the server url including the port number
     */
    public KProducer(String topic, String server) {
        this.topic = topic;
        this.endpoint = String.format(
                "http://%s%s", server, Constants.PRODUCER_SEND_MESSAGE_URL
        ).replace("[topic]", this.topic);

        this.threadPoolExecutor = Executors.newFixedThreadPool(10);
    }

    /**
     * Send message to broker that will be dispatched by the broker to consumers
     *
     * @param key
     * @param message
     */
    public void sendMessage(String key, String message) {
        this.threadPoolExecutor.submit(
                () -> {
                    final boolean[] isSuccess = {false};
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        RequestCallback c = clientHttpRequest -> {
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.writeValue(clientHttpRequest.getBody(), new MessagePayloadDto(key, message));
                            clientHttpRequest.getHeaders().set("Content-Type", "application/json");
                        };;

                        restTemplate.execute(
                                this.endpoint,
                                HttpMethod.POST,
                                c,
                                response -> {
                                    isSuccess[0] = true;
                                    return null;
                                }
                        );

                        if (isSuccess[0]){
                            LOGGER.info("Message sent");
                        } else {
                            //
                        }
                    } catch (RestClientException rce) {
                        //TODO log the error
                        rce.printStackTrace();
                    }
                }
        );
    }
}
