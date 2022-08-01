package com.klustq.client.lib.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KlustQueueConfiguration {

    @Autowired
    KlustQueueConfiguration(KlustQConfigurationProperties klustQConfigurationProperties){}

    @Bean
    public KlustQConfigurationProperties kProps(){
        return new KlustQConfigurationProperties();
    }
}
