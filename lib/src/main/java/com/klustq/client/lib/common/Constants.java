package com.klustq.client.lib.common;

public class Constants {

    public static final String BROKER_ENDPOINT = "/broker";
    public static final String TOPIC_ENDPOINT = "/broker/topics";

    public static final String CREATE_DELETE_TOPIC_URL = "/broker/topics?name=[name]";
    public static final String PRODUCER_SEND_MESSAGE_URL ="/broker/topics/[topic]/messages";

    //TODO review this on the server
    public static final Integer SERVER_TTL_HEALTH_CHECKER_SECONDS = 9;
}
