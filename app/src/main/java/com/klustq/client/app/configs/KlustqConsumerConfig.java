package com.klustq.client.app.configs;

import com.klustq.client.lib.KlustqTemplate;
import com.klustq.client.lib.common.serializer.StringSerializer;
import com.klustq.client.lib.consumer.ConsumerConfig;
import com.klustq.client.lib.consumer.DefaultKlustqConsumerFactory;
import com.klustq.client.lib.producer.DefaultKlustqProducerFactory;
import com.klustq.client.lib.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KlustqConsumerConfig {

    @Value("${klustq.bootstrap-server}")
    private String bootstrapServer;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServer);
        props.put(ConsumerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return props;
    }

    @Bean
    public DefaultKlustqConsumerFactory<String, String> consumerFactory() {
        return new DefaultKlustqConsumerFactory<>(consumerConfigs());
    }
}
