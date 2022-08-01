package com.klustq.client.lib.consumer.event;

public class DefaultEvent {

    private String event;

    public DefaultEvent() {
    }

    public DefaultEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
