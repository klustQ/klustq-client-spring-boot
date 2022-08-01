package com.klustq.client.lib;

import com.klustq.client.lib.common.exception.SerializationException;
import com.klustq.client.lib.common.serializer.JsonSerializer;
import com.klustq.client.lib.common.serializer.Serializer;
import com.klustq.client.lib.common.serializer.StringSerializer;
import com.klustq.client.lib.producer.KProducer;
import com.klustq.client.lib.producer.ProducerConfig;
import com.klustq.client.lib.producer.ProducerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @class FakeKafkaTemplate used by client to produce message
 */
public class KlustqTemplate<K, M> {

    private final ProducerFactory<K, M> producerFactory;
    private Serializer<K> keySerializer;
    private Serializer<M> messageSerializer;

    public KlustqTemplate(ProducerFactory<K, M> producerFactory) {
        this.producerFactory = producerFactory;

        try {
            Class<?> keySerializerClazz = (Class<?>) producerFactory.getConfigs()
                    .get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
            Class<?> messageSerializerClazz = (Class<?>) producerFactory.getConfigs()
                    .get(ProducerConfig.MESSAGE_SERIALIZER_CLASS_CONFIG);

            if (keySerializerClazz == StringSerializer.class) {
                this.keySerializer = (Serializer<K>) (new StringSerializer());
            } else if (keySerializerClazz == JsonSerializer.class) {
                this.keySerializer = (Serializer<K>) (new JsonSerializer());
            }

            if (messageSerializerClazz == StringSerializer.class) {
                this.messageSerializer = (Serializer<M>) new StringSerializer();
            } else if (messageSerializerClazz == JsonSerializer.class) {
                this.messageSerializer = (Serializer<M>) new JsonSerializer();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send message to broker
     */
    public void send(@NonNull String topic, @NonNull M message) {
        this.send(topic, null, message);
    }

    /**
     * Send message to broker
     *
     * @param topic
     * @param key
     * @param message
     */
    public void send(@NonNull String topic, @Nullable K key, @NonNull M message) {
        KProducer p = this.producerFactory.createProducer(
                topic,
                (String) producerFactory.getConfigs()
                        .get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)
        );
        try {
            p.sendMessage(
                    this.keySerializer.serialize(key),
                    this.messageSerializer.serialize(message));


        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }
}
