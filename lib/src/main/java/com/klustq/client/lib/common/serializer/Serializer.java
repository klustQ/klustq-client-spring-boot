package com.klustq.client.lib.common.serializer;

import com.klustq.client.lib.common.ConfigDef;
import com.klustq.client.lib.common.exception.SerializationException;

public interface Serializer<T> extends ConfigDef {

    /**
     * Convert the message to send into a string. We reminder that The content exchanged
     * between broker and consumers are in string format
     *
     * @param v the data to serialize
     * @return the string format of the parameter object
     * @throws SerializationException occurs in case we have an error
     */
    public String serialize(T v) throws SerializationException;
}
