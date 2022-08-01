package com.klustq.client.lib.consumer.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsumerSubscribedEvent extends DefaultEvent {

    private String message;

    @JsonProperty("client_id")
    private String clientId;

    public ConsumerSubscribedEvent() {
        super();
    }

    public ConsumerSubscribedEvent(String event, String message, String clientId) {
        super(event);
        this.message = message;
        this.clientId = clientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
