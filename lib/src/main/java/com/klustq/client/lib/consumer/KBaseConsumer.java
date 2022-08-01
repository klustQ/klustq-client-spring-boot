package com.klustq.client.lib.consumer;

import com.klustq.client.lib.common.Constants;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Deprecated
public class KBaseConsumer {

    private final StandardWebSocketClient simpleWebSocketClient;
    private final WebSocketStompClient stompClient;

    public KBaseConsumer() {
        this.simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://localhost:7000/chat";
        StompSessionHandler sessionHandler = new MyStompSessionHandler("ii");
        try {
            StompSession session = stompClient.connect(url, sessionHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    static public class MyStompSessionHandler extends StompSessionHandlerAdapter {
        private String userId;


        public MyStompSessionHandler(String userId) {
            this.userId = userId;
        }

        private void showHeaders(StompHeaders headers) {
            for (Map.Entry<String, List<String>> e : headers.entrySet()) {
                System.err.print("  " + e.getKey() + ": ");
                boolean first = true;
                for (String v : e.getValue()) {
                    if (!first) System.err.print(", ");
                    System.err.print(v);
                    first = false;
                }
                System.err.println();
            }
        }

        private void sendJsonMessage(StompSession session) {
            //ClientMessage msg = new ClientMessage(userId,
            //"hello from spring");
            //session.send("/app/chat/java", msg);
        }

        private void subscribeTopic(String topic, StompSession session) {
            session.subscribe(topic, new StompFrameHandler() {

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class; //ServerMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers,
                                        Object payload) {
                    System.err.println(payload.toString());
                }
            });
        }

        @Override
        public void afterConnected(StompSession session,
                                   StompHeaders connectedHeaders) {
            System.err.println("Connected! Headers:");
            showHeaders(connectedHeaders);

            subscribeTopic("/topic/messages", session);
            sendJsonMessage(session);
        }
    }
}
