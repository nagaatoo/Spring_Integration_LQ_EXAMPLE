package ru.itfb.meetup.messageTube.conf.prod;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import ru.itfb.meetup.messageTube.consts.ConstNames;

import java.util.Map;

@Configuration
@Profile("prod")
public class KafkaConf {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> retryContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, ConstNames.EQ_TOPIC);
        configurer.configure(factory, new DefaultKafkaConsumerFactory<>(consumerProperties));
        return factory;
    }


    @Bean
    public ConcurrentMessageListenerContainer<?, ?> retryMessageListenerContainer(
            ConcurrentKafkaListenerContainerFactory<?, ?> retryContainerFactory
    ) {
        var container = retryContainerFactory.createContainer(ConstNames.EQ_TOPIC);
        var executor = new SimpleAsyncTaskExecutor();
        container.getContainerProperties().setListenerTaskExecutor(executor);
        container.setConcurrency(1);
        return container;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> inputContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, ConstNames.WORK_TOPIC);
        configurer.configure(factory, new DefaultKafkaConsumerFactory<>(consumerProperties));
        return factory;
    }


    @Bean
    public ConcurrentMessageListenerContainer<?, ?> messageListenerContainer(
            ConcurrentKafkaListenerContainerFactory<?, ?> retryContainerFactory
    ) {
        var container = retryContainerFactory.createContainer(ConstNames.WORK_TOPIC);
        var executor = new SimpleAsyncTaskExecutor();
        container.getContainerProperties().setListenerTaskExecutor(executor);
        container.setConcurrency(1); // сколько нужно
        return container;
    }

    @Bean
    public DefaultKafkaHeaderMapper kafkaHeaderMapper() {
        return new DefaultKafkaHeaderMapper();
    }
}
