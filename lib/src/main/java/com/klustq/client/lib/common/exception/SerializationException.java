package com.klustq.client.lib.common.exception;

public class SerializationException extends Exception {

    public SerializationException(String message){
        super("Unable to serialize the object: " + message);
    }
}
