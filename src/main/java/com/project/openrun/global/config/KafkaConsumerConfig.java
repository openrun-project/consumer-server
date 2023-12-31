package com.project.openrun.global.config;

import com.project.openrun.global.kafka.consumer.OrderCreateConsumer;
import com.project.openrun.global.kafka.dto.OrderEventDto;
import com.project.openrun.global.kafka.consumer.exceptionHandler.CustomErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.group-id}")
    private String groupId;

    //default
    @Value("${kafka.max-poll-records}")
    private int maxPollRecords;

    @Value("${kafka.topic.notification}")
    private String topic;

    /*나중에 지울게요 => 여기 빈으로 주입 받고 => 아래서 productErrorHandler.handle() or productErrorHandler::handle 을 통해서 구현체에서 작동*/
    private final CustomErrorHandler productErrorHandler;
    private final OrderCreateConsumer orderCreateConsumer;

    @Bean
    public KafkaMessageListenerContainer<Long, OrderEventDto> kafkaMessageListenerContainer() {
        ContainerProperties properties = new ContainerProperties(topic);
        properties.setMessageListener((MessageListener<Long, OrderEventDto>) data -> {
            orderCreateConsumer.createOrderInConsumer(data.value());
        });
        properties.setAckMode(ContainerProperties.AckMode.RECORD);
        DefaultKafkaConsumerFactory<Long, OrderEventDto> factory = new DefaultKafkaConsumerFactory<>(consumerConfigs());

        KafkaMessageListenerContainer<Long, OrderEventDto> container = new KafkaMessageListenerContainer<>(factory, properties);
        container.setCommonErrorHandler(getDefaultErrorHandler());
        return container;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderEventDto.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, LongDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.KEY_DEFAULT_TYPE, Long.class);

        return props;
    }

    private DefaultErrorHandler getDefaultErrorHandler() {
        return new DefaultErrorHandler(productErrorHandler::handle, new FixedBackOff(2000L, 3L));
    }
}

