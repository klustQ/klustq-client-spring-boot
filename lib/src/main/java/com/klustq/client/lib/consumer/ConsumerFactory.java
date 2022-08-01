package com.klustq.client.lib.consumer;

import com.klustq.client.lib.producer.KProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for producers. The producer creation depends on
 * the key type and the value type
 * @param <K>
 * @param <M>
 */
public class ConsumerFactory<K, M> {

    private Map<String, Object> configs;

    /**
     * Mapping topic to producer
     */
    private final Map<String, KConsumer> cContainer = new HashMap<>();

    public ConsumerFactory(Map<String, Object> configs) {
        this.configs = configs;
    }

    public ConsumerFactory() {
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Object> configs) {
        this.configs = configs;
    }

    synchronized public KConsumer createConsumer(String topic, String server){
        if (cContainer.containsKey(topic)){
            return cContainer.get(topic);
        }

        KConsumer c = new KConsumer(topic, "group", server);
        cContainer.put(topic, c);

        return c;
    }
}
