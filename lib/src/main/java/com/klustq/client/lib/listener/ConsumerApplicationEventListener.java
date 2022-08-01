package com.klustq.client.lib.listener;

import com.klustq.client.lib.Topic;
import com.klustq.client.lib.common.model.PartitionRecord;
import com.klustq.client.lib.consumer.KConsumer;
import com.klustq.client.lib.consumer.annotation.KlustqHandler;
import com.klustq.client.lib.consumer.annotation.KlustqListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ConsumerApplicationEventListener implements KConsumer.KConsumerListener {

    private final ApplicationEventPublisher eventPublisher;
    @Value("${klustq.bootstrap-server}")
    private String bootstrapServer;

    private final ApplicationContext context;
    private final String[] topicsBeansName;

    private final List<KConsumer> cContainer = new CopyOnWriteArrayList<>();

    /**
     * Mapping the topic name to its topic
     */
    private final Map<String, Topic> topicsBeans = new HashMap<>();

    /**
     * Mapping the (topic name + group id) to its topic
     */
    private final Map<String, Object> klustListenersContainer = new HashMap<>();

    @Autowired
    public ConsumerApplicationEventListener(ApplicationContext context, ApplicationEventPublisher applicationEventPublisher) {
        this.context = context;
        this.eventPublisher = applicationEventPublisher;

        topicsBeansName = this.context.getBeanNamesForType(Topic.class);
        System.out.println(Arrays.toString(topicsBeansName));
        Arrays.stream(topicsBeansName).sequential().forEach(
                bn -> {
                    Topic topic = context.getBean(bn, Topic.class);
                    topicsBeans.put(topic.getName(), topic);
                }
        );
    }

    @EventListener({ApplicationReadyEvent.class})
    void handleReadyEvent(ApplicationReadyEvent event) {
        //The application is ready, all topics are
        //created. Now we check if there is a listener
        //for a topic, if so we start a consumer client for that
        //topic
        context.getBeansWithAnnotation(KlustqListener.class)
                .keySet().forEach(
                        v -> {
                            KlustqListener k = context.findAnnotationOnBean(v, KlustqListener.class);
                            //Our bean is annotated with a listener
                            if (k != null) {
                                klustListenersContainer.put(
                                        String.format("%s#%s", k.topic(), k.group())
                                        ,context.getBean(v));
                                createConsumer(k.topic(), k.group());
                            }
                        }
                );

    }

    @EventListener({ContextStoppedEvent.class})
    void handleShutdown() {
        cContainer.forEach(KConsumer::stop);
    }

    private void createConsumer(String topic, String group) {
        if (!topicsBeans.containsKey(topic)) {
            //We have not been registered for this topic
            throw new RuntimeException(
                    new Exception("Topic " + topic + " has not been registered as bean")
            );
        }

        KConsumer consumer = new KConsumer(topic, group, bootstrapServer);
        consumer.setListener(this);
        cContainer.add(consumer);
        consumer.start();
    }

    @EventListener(PartitionRecord.class)
    @Async
    public void handlePartitionRecord(PartitionRecord record){
        synchronized (klustListenersContainer) {
            final Object lstn = klustListenersContainer.get(
                    String.format("%s#%s", record.getTopic(), record.getGroupId()));
            Arrays.stream(lstn.getClass().getDeclaredMethods()).filter(
                            m -> m.isAnnotationPresent(KlustqHandler.class)
                    ).findFirst()
                    .ifPresentOrElse((method) -> {
                        try {
                            method.invoke(lstn, record.getData().getMessage());
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }, () -> {
                        throw new RuntimeException(new Exception(
                                String.format("No Handler found for topic %s group %s",
                                        record.getTopic(), record.getGroupId()))
                        );
                    });
        }
    }

    @Override
    public void publish(PartitionRecord record) {
        //TODO add group to partition record

        eventPublisher.publishEvent(record);
    }
}
