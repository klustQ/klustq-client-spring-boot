package com.klustq.client.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MarshallingMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class KStompClient {

    private final String server;
    private StandardWebSocketClient simpleWebSocketClient;
    private WebSocketStompClient stompClient;
    private StompSession session;
    private String consumerBrokerUrl;

    private KStompClientListener mListener;

    private StompSessionHandlerAdapter sessionHandler;

    protected List<StompSession.Subscription> subscriptions = new ArrayList<>();
    private WebSocketHttpHeaders wsHeaders;
    private StompHeaders stHeaders;

    public KStompClient(String server) {
        this.server = server;
        //We are using socket js
        this.consumerBrokerUrl = String.format("ws://%s/klust-queue/sockJs", this.server);
    }


    public StompSessionHandlerAdapter getSessionHandler() {
        return sessionHandler;
    }

    public void setSessionHandler(StompSessionHandlerAdapter sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    public StompSession getSession() {
        return session;
    }

    public void setSession(StompSession session) {
        this.session = session;
    }

    /**
     *
     * @param klustId
     * @param topic
     * @param group
     */
    public void init(String klustId, String topic, String group) {
        this.simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        //TODO find a way to get the string form of the message

        this.wsHeaders = new WebSocketHttpHeaders();
        wsHeaders.add("topic", topic);
        wsHeaders.add("group", group);
        wsHeaders.add("id", klustId);

        this.stHeaders = new StompHeaders();
        this.stHeaders.add("topic", topic);
        this.stHeaders.add("group", group);
        this.stHeaders.add("id", klustId);

    }

    public void connect(){
        if (this.stompClient == null){
            throw new RuntimeException(new Exception("Stomp Client no initialized"));
        }
        try {
            this.session = stompClient.connect(this.consumerBrokerUrl,wsHeaders, stHeaders, sessionHandler).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(){
        try {
            this.subscriptions.forEach(StompSession.Subscription::unsubscribe);
            this.stompClient.stop(() -> {
                if (mListener != null) mListener.onStop();
            });
        }catch (Exception ignored) {

        }

    }

    public KStompClientListener getListener() {
        return mListener;
    }

    public void setListener(KStompClientListener listener) {
        this.mListener = listener;
    }

    public interface KStompClientListener {
        void onReady();
        void onStop();
    }
}
