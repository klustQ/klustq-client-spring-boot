package com.klustq.client.lib.producer;

import java.util.Map;

/**
 * @class DefaultProducerFactory use String as key and value 's type
 */
public class DefaultKlustqProducerFactory<K, M> extends ProducerFactory<K, M> {

    public DefaultKlustqProducerFactory(Map<String, Object> configs) {
        super(configs);
    }
    public DefaultKlustqProducerFactory() {}
}
