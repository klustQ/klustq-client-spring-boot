package com.klustq.client.lib.common.dto;

public class SessionResponseDto {

    private String id;

    public SessionResponseDto() {
    }

    public SessionResponseDto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
