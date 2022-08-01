package com.klustq.client.lib.consumer;

import com.klustq.client.lib.KlustQClient;
import com.klustq.client.lib.common.model.ParticipantEnum;
import com.klustq.client.lib.common.model.PartitionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

public class KConsumer {

    private final static Logger LOGGER = LoggerFactory.getLogger(KlustQClient.class);
    private final String topic;
    private String endpoint;
    private final String group;

    private KlustQClient client;
    private Thread thread;

    private KConsumerListener mListener;
    private volatile boolean runFlag = true;

    /**
     * Constructor
     *
     * @param topic  the topic name
     * @param group consumer group id
     * @param server the server url including the port number
     */
    public KConsumer(String topic, String group, String server) {
        this.topic = topic;
        this.group = group;
        
        this.client = new KlustQClient(topic, server);
        this.client.setType(ParticipantEnum.CONSUMER_PARTICIPANT_TYPE);
    }

    public KConsumerListener getListener() {
        return mListener;
    }

    public void setListener(KConsumerListener listener) {
        this.mListener = listener;
    }

    public void start(){
        this.thread = new Thread(() -> {
            try {
                //Get id from the klustQ server
                this.client.fetchSessionId();

                //Init
                this.client.init(this.client.getKlustqSession().getId(), this.topic, this.group);
                this.client.setKlustQListener(new KlustQClient.KlustQClientListener() {
                    @Override
                    public void onConnection(StompSession session, StompHeaders connectedHeaders) {

                    }

                    @Override
                    public void onConsumerCreated() {

                    }

                    @Override
                    public void onRecord(PartitionRecord payload) {
                        if (mListener != null){
                            mListener.publish(payload);
                        } else throw new RuntimeException(new Exception("No Listener on klustq consumer"));
                    }

                    @Override
                    public void onServerDisconnectedConsumer() {
                        try {
                            KConsumer.this.thread.interrupt();
                        } catch (Exception ignored){}

                        KConsumer.this.start();
                    }

                    @Override
                    public void onError() {
                        throw new RuntimeException(new Exception("An error Occurred while subscribing as consumer"));
                    }
                });

                this.client.handle();
                this.client.connect();
            } catch (Exception e){
                throw new RuntimeException(e);
            }

            while (runFlag){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {

                }
            }
        }, "consumer-" + this.topic + "-" + this.group);

        this.thread.start();
    }

    public void stop(){
        try {
            this.runFlag = false;
            this.thread.interrupt();
            this.client.stop();

            LOGGER.info(String.format("Consumer stopped topic %s group %s", this.topic, this.group));
        } catch (Exception ignored){}
    }

    public interface KConsumerListener {
        void publish(PartitionRecord record);
    }
}
