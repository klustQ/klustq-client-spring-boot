package com.klustq.client.lib.common.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @class TopicMessage A message dispatched through a topic. A topic consists
 * of the topic name and the message a string. It is a part of a record partition
 */
public class TopicMessage {

    @NonNull
    private String message;

    @Nullable
    private String key;

    public TopicMessage() {
    }

    /**
     * Constructor
     * @param message the message a json stringifies
     */
    public TopicMessage(@NonNull String message) {
        this.message = message;
        this.key = null;
    }

    /**
     * Constructor
     * @param key  a key associated to the message
     * @param message the message a json stringifies
     */
    public TopicMessage(@Nullable String key, @NonNull String message) {
        this.message = message;
        this.key = key;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    @Nullable
    public String getKey() {
        return key;
    }

    public void setKey(@Nullable String key) {
        this.key = key;
    }
}
