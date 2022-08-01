package com.klustq.client.app.configs;

import com.klustq.client.lib.KlustqTemplate;
import com.klustq.client.lib.common.serializer.StringSerializer;
import com.klustq.client.lib.producer.DefaultKlustqProducerFactory;
import com.klustq.client.lib.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KlustqProducerConfig {

    @Value("${klustq.bootstrap-server}")
    private String bootstrapServer;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.MESSAGE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return props;
    }

    @Bean
    public DefaultKlustqProducerFactory<String, String> producerFactory() {
        return new DefaultKlustqProducerFactory<>(producerConfigs());
    }

    @Bean
    public KlustqTemplate<String, String> klustqTemplate() {
        return new KlustqTemplate<>(producerFactory());
    }
}
