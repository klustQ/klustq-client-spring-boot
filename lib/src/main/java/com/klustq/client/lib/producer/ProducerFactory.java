package com.klustq.client.lib.producer;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for producers. The producer creation depends on
 * the key type and the value type
 * @param <K>
 * @param <M>
 */
public class ProducerFactory<K, M> {

    private Map<String, Object> configs;

    /**
     * Mapping topic to producer
     */
    private final Map<String, KProducer> pContainer = new HashMap<>();

    public ProducerFactory(Map<String, Object> configs) {
        this.configs = configs;
    }

    public ProducerFactory() {
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Object> configs) {
        this.configs = configs;
    }

    synchronized public KProducer createProducer(String topic, String server){
        if (pContainer.containsKey(topic)){
            return pContainer.get(topic);
        }

        KProducer p = new KProducer(topic, server);
        pContainer.put(topic, p);

        return p;
    }
}
