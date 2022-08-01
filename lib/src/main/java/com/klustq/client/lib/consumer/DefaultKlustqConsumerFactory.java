package com.klustq.client.lib.consumer;

import com.klustq.client.lib.producer.ProducerFactory;

import java.util.Map;

/**
 * @class
 */
public class DefaultKlustqConsumerFactory<K, M> extends ConsumerFactory<K, M> {

    public DefaultKlustqConsumerFactory(Map<String, Object> configs) {
        super(configs);
    }
    public DefaultKlustqConsumerFactory() {}
}
