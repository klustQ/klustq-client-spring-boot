package com.klustq.client.app.configs;

import com.klustq.client.lib.Topic;
import com.klustq.client.lib.TopicBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicsConfig {

    @Bean
    Topic defaultTopic(){
        return TopicBuilder.name("default").build();
    }
}
