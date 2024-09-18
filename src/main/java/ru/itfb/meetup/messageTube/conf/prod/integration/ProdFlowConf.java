package ru.itfb.meetup.messageTube.conf.prod.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import ru.itfb.meetup.messageTube.consts.ConstNames;

@Configuration
@Profile("prod")
public class ProdFlowConf {

    @Bean
    public IntegrationFlow toWorkflowKafkaFlow(
            MessageChannel outputChannel,
            ProducerFactory<?, ?> producerFactory
    ) {
        return IntegrationFlow
                .from(outputChannel)
                .handle(Kafka.outboundChannelAdapter(producerFactory).topic(ConstNames.WORK_TOPIC))
                .get();
    }

    @Bean
    public IntegrationFlow fromKafkaWorkflow(
            ConcurrentMessageListenerContainer<?, ?> messageListenerContainer,
            MessageChannel inputChannel
    ) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(messageListenerContainer))
                .channel(inputChannel)
                .get();
    }

    @Bean
    public IntegrationFlow fromKafkaToInvalidChannelFlow(
            ConcurrentMessageListenerContainer<?, ?> retryMessageListenerContainer,
            MessageChannel inputChannel
    ) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(retryMessageListenerContainer))
                .channel(inputChannel)
                .get();
    }

    @Bean
    public IntegrationFlow toInvalidChannelFlow(
            MessageChannel invalidOutputChannel,
            MessageChannel deadLetterChannel,
            ProducerFactory<?, ?> producerFactory
    ) {
        return IntegrationFlow
                .from(invalidOutputChannel)
                .transform(Message.class, m ->
                        MessageBuilder
                                .fromMessage(m)
                                .setHeader(
                                        ConstNames.FAILURE_COUNT_HEADER_NAME,
                                        (Integer) m.getHeaders().get(ConstNames.FAILURE_COUNT_HEADER_NAME) + 1)
                                .build()
                )
                .route(
                        Message.class,
                        m -> (Integer) m.getHeaders().get(ConstNames.FAILURE_COUNT_HEADER_NAME) > ConstNames.ERROR_COUNT,
                        m ->
                                m
                                        .subFlowMapping(false, subflow -> {
                                            subflow.handle(Kafka.outboundChannelAdapter(producerFactory).topic(ConstNames.EQ_TOPIC));
                                        })
                                        .channelMapping(true, deadLetterChannel)
                )
                .get();
    }

    @Bean
    public IntegrationFlow toDeadLetterChannelFlow(
            MessageChannel deadLetterChannel,
            ProducerFactory<?, ?> producerFactory
    ) {
        return IntegrationFlow
                .from(deadLetterChannel)
                .log(e -> {
                    System.out.println("To dead letter message " + e.getPayload());
                    return e;
                })
                .handle(Kafka.outboundChannelAdapter(producerFactory).topic(ConstNames.DLQ_TOPIC))
                .get();
    }

}
