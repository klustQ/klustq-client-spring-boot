package com.klustq.client.lib.common.dto;

public class SessionRequestDto {

    private String type;
    private String topic;

    public SessionRequestDto() {
    }

    public SessionRequestDto(String type, String topic) {
        this.type = type;
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
