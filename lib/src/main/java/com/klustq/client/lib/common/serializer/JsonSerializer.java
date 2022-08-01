package com.klustq.client.lib.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klustq.client.lib.common.exception.SerializationException;

public class JsonSerializer implements Serializer<Object> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String serialize(Object v) throws SerializationException {
        if (v == null){
            throw new SerializationException("Null object is not accepted..");
        }

        try {
            return objectMapper.writeValueAsString(v);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
