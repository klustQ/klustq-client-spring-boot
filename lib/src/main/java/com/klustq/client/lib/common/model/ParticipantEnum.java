package com.klustq.client.lib.common.model;

public enum ParticipantEnum {

    CONSUMER_PARTICIPANT_TYPE("CONSUMER"),
    PRODUCER_PARTICIPANT_TYPE("PRODUCER");

    private final String value;

    ParticipantEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
