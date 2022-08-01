package com.klustq.client.lib.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessagePayloadDto {
    private String key;

    @JsonProperty("message")
    private String payload;

    public MessagePayloadDto() {
    }

    public MessagePayloadDto(String key, String payload) {
        this.key = key;
        this.payload = payload;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
