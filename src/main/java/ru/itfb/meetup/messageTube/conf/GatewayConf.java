package ru.itfb.meetup.messageTube.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.handler.annotation.Header;
import ru.itfb.meetup.messageTube.consts.ConstNames;
import ru.itfb.meetup.messageTube.dto.MessageDTO;

@Configuration
public class GatewayConf {

    // Канал для эмуляции отправки
    @Bean
    public DirectChannel outputChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel invalidOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel deadLetterChannel() {
        return new DirectChannel();
    }

    @MessagingGateway
    public interface ChannelGateways {

        // Шлюз для эмуляции отправки в work channel
        @Gateway(requestChannel = "outputChannel")
        void outputChannel(MessageDTO messageDTO);

        @Gateway(requestChannel = "invalidOutputChannel")
        void sendInvalidRetryGateway(
                @Header(ConstNames.FAILURE_COUNT_HEADER_NAME) Object count,
                MessageDTO messageDTO
        );

        @Gateway(requestChannel = "deadLetterChannel")
        void sendDeadLetterGateway(MessageDTO messageDTO);

    }
}
