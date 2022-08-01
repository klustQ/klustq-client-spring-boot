package com.klustq.client.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klustq.client.lib.common.Constants;
import com.klustq.client.lib.common.model.ParticipantEnum;
import com.klustq.client.lib.common.dto.SessionRequestDto;
import com.klustq.client.lib.common.dto.SessionResponseDto;
import com.klustq.client.lib.common.model.PartitionRecord;
import com.klustq.client.lib.consumer.channels.Channels;
import com.klustq.client.lib.consumer.event.ConsumerSubscribedEvent;
import com.klustq.client.lib.consumer.event.DefaultEvent;
import com.klustq.client.lib.consumer.event.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KlustQClient extends KStompClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(KlustQClient.class);

    private final String sessionEndpoint;
    private final String topic;
    private StandardWebSocketClient simpleWebSocketClient;
    private WebSocketStompClient stompClient;
    private ParticipantEnum participantType;

    private SessionResponseDto klustqSession;
    private String consumerId;

    private KlustQClientListener mKListener;

    private ScheduledExecutorService healthTllScheduledExecutorService;


    /**
     * @param topic
     * @param server
     */
    public KlustQClient(String topic, String server) {
        super(server);

        this.topic = topic;
        this.sessionEndpoint = String.format(
                "http://%s%s", server, Constants.BROKER_ENDPOINT
        );

        this.healthTllScheduledExecutorService = Executors.newScheduledThreadPool(1);

        this.setListener(new KStompClientListener() {
            @Override
            public void onReady() {

            }

            @Override
            public void onStop() {
                //We have stopped
            }
        });

    }

    /**
     * Set the participant type. Set if the current client
     * will be a consumer or a producer
     *
     * @param value the type of client
     * @throws Error if the value provided is invalid
     */
    public void setType(ParticipantEnum value) {
        this.participantType = value;
    }

    public KlustQClientListener getKlustQListener() {
        return mKListener;
    }

    public void setKlustQListener(KlustQClientListener listener) {
        this.mKListener = listener;
    }

    public SessionResponseDto getKlustqSession() {
        return klustqSession;
    }

    public void setKlustqSession(SessionResponseDto klustqSession) {
        this.klustqSession = klustqSession;
    }

    public void handle() {
        final ObjectMapper objectMapper = new ObjectMapper();
        //Set the session handler
        this.setSessionHandler(new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                //super.afterConnected(session, connectedHeaders);
                //System.out.println("After connected");

                //Update the session
                KlustQClient.this.setSession(session);

                //We subscribe to all
                KlustQClient.this.subscriptions.add(
                        session.subscribe(Channels.TOPIC_BROADCAST_CHANNEL, new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return DefaultEvent.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                DefaultEvent ev = (DefaultEvent) payload;
                                try {
                                    if (Objects.equals(ev.getEvent(), Events.CONSUMER_HEALTH_EVENT)) {
                                        LOGGER.info("--> Healthy Server <--");
                                    }
                                    //LOGGER.info("Message " + objectMapper.writeValueAsString(payload));
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        })
                );

                if (KlustQClient.this.participantType == ParticipantEnum.CONSUMER_PARTICIPANT_TYPE) {
                    //Subscribe to topic record channel
                    KlustQClient.this.subscriptions.add(
                            session.subscribe(Channels.USER_QUEUE_MESSAGE_CHANNEL, new StompFrameHandler() {
                                @Override
                                public Type getPayloadType(StompHeaders headers) {
                                    return PartitionRecord.class;
                                }

                                @Override
                                public void handleFrame(StompHeaders headers, Object payload) {
                                    //We have a record on a topic from th broker
                                    mKListener.onRecord(
                                            (PartitionRecord) payload
                                    );
                                }
                            })
                    );

                    //Subscribe as a consumer
                    KlustQClient.this.subscriptions.add(
                            session.subscribe(Channels.APP_CONSUMER_CHANNEL, new StompFrameHandler() {
                                @Override
                                public Type getPayloadType(StompHeaders headers) {
                                    return DefaultEvent.class;
                                }

                                @Override
                                public void handleFrame(StompHeaders headers, Object payload) {
                                    //We have a record on a topic from th broker
                                    mKListener.onConsumerCreated();
                                    //Here we save client id assigned by the broker
                                    //It will serve to disconnec the consumer client from the broker
                                    //this.consumerId = d.id;
                                }
                            })
                    );
                }

                //Handle private messages got from the server
                //Topic private messages
                KlustQClient.this.subscriptions.add(
                        session.subscribe(Channels.USER_TOPIC_PRIVATE_CHANNEL, new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return DefaultEvent.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                DefaultEvent ev = (DefaultEvent) payload;
                                try {
                                    if (Objects.equals(ev.getEvent(), Events.CONSUMER_SUBSCRIBED_EVENT)) {
                                        //TODO convert to a consumer subscribed event
                                        //and save the consumer id. It helps for unsubscribing from broker
                                        //ConsumerSubscribedEvent event = (ConsumerSubscribedEvent) payload;

                                        //We start sending ttl update to server in order to be alive
                                        //on server side
                                        handleHealthTtl();

                                    } else if (Objects.equals(ev.getEvent(), Events.CONSUMER_SUBSCRIBE_ERROR_EVENT)) {
                                        //We try to reestablish the connection
                                        mKListener.onError();

                                    } else if (Objects.equals(ev.getEvent(), Events.CONSUMER_DISCONNECTED_EVENT)) {
                                        //We are still alive but the server disconnected us
                                        mKListener.onServerDisconnectedConsumer();
                                    } else if (Objects.equals(ev.getEvent(), Events.CONSUMER_UPDATED_TTL_EVENT)) {
                                        //We have sent a healthy ttl to the server
                                        LOGGER.info("--> Healthy <--");
                                    } else {
                                        //Nothing to do
                                    }
                                    //LOGGER.info("Private message " + objectMapper.writeValueAsString(payload));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                );
            }
        });
    }

    private void handleHealthTtl() {
        if (this.getSession() != null) {
            this.healthTllScheduledExecutorService.scheduleAtFixedRate(
                    () -> {
                        this.getSession().send(
                                Channels.APP_TOPIC_TTL, "health");
                    },
                    Constants.SERVER_TTL_HEALTH_CHECKER_SECONDS,
                    Constants.SERVER_TTL_HEALTH_CHECKER_SECONDS,
                    TimeUnit.SECONDS
            );

        }
    }

    /**
     * Get the session that will be assigned to this consumer
     */
    public void fetchSessionId() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            RequestCallback c = clientHttpRequest -> {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(clientHttpRequest.getBody(), new SessionRequestDto(
                        this.participantType.getValue(), this.topic)
                );
                clientHttpRequest.getHeaders().set("Content-Type", "application/json");
            };
            ;

            this.klustqSession = restTemplate.execute(this.sessionEndpoint, HttpMethod.POST,
                    c,
                    response -> {
                        String b = new String(response.getBody().readAllBytes());
                        return (new ObjectMapper()).readValue(b, SessionResponseDto.class);
                    }
            );
        } catch (RestClientException rce) {
            //TODO log the error
            rce.printStackTrace();
        }
    }

    public void stop(){
        if (this.healthTllScheduledExecutorService != null){
            this.healthTllScheduledExecutorService.shutdownNow();
        }

        if (this.participantType == ParticipantEnum.CONSUMER_PARTICIPANT_TYPE){
            //TODO send disconnect request to the server
        }

        if (this.stompClient.isRunning()){
            super.disconnect();
        }
    }

    public interface KlustQClientListener {
        void onConnection(StompSession session, StompHeaders connectedHeaders);

        void onConsumerCreated();

        void onRecord(PartitionRecord payload);

        void onServerDisconnectedConsumer();

        void onError();
    }

}
