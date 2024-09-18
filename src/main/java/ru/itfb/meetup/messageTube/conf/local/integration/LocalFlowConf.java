package ru.itfb.meetup.messageTube.conf.local.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import ru.itfb.meetup.messageTube.consts.ConstNames;

@Configuration
@Profile("!prod")
public class LocalFlowConf {

    @Bean
    public IntegrationFlow toWorkflowFlow(
            MessageChannel outputChannel,
            MessageChannel inputChannel
    ) {
        return IntegrationFlow
                .from(outputChannel)
                .channel(inputChannel)
                .get();
    }

    @Bean
    public IntegrationFlow toInvalidChannelFlow(
            MessageChannel invalidOutputChannel,
            MessageChannel outputChannel,
            MessageChannel deadLetterChannel
    ) {
        return IntegrationFlow
                .from(invalidOutputChannel)
                .transform(Message.class, e ->
                        MessageBuilder
                                .fromMessage(e)
                                .setHeader(
                                        ConstNames.FAILURE_COUNT_HEADER_NAME,
                                        (Integer) e.getHeaders().get(ConstNames.FAILURE_COUNT_HEADER_NAME) + 1)
                                .build()
                )
                .route(
                        Message.class,
                        m -> (Integer) m.getHeaders().get(ConstNames.FAILURE_COUNT_HEADER_NAME) > ConstNames.ERROR_COUNT,
                        m ->
                                m
                                        .channelMapping(false, outputChannel)
                                        .channelMapping(true, deadLetterChannel)
                )
                .get();
    }

    @Bean
    public IntegrationFlow toDeadLetterChannelFlow(MessageChannel deadLetterChannel) {
        return IntegrationFlow
                .from(deadLetterChannel)
                .handle(e -> System.out.println("To dead letter message " + e.getPayload()))
                .get();
    }
}
