package com.klustq.client.lib.common.serializer;

public class SingleFieldObject {

    private String value;

    public SingleFieldObject(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
