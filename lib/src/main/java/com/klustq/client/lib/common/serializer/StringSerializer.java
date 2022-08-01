package com.klustq.client.lib.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klustq.client.lib.common.exception.SerializationException;

public class StringSerializer implements Serializer<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String serialize(String v) throws SerializationException {
        //final SingleFieldObject o = new SingleFieldObject( (v == null) ? "null" : v);
        try {
            //(v != null ) ? objectMapper.writeValueAsString(v) : v
            return v;
        } catch (Exception e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
