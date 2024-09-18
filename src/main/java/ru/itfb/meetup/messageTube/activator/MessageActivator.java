package ru.itfb.meetup.messageTube.activator;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.itfb.meetup.messageTube.consts.ConstNames;
import ru.itfb.meetup.messageTube.conf.GatewayConf;
import ru.itfb.meetup.messageTube.dto.MessageDTO;
import ru.itfb.meetup.messageTube.exception.RetryException;
import ru.itfb.meetup.messageTube.service.MessageService;

@Component
@RequiredArgsConstructor
public class MessageActivator {

    private final MessageService messageService;
    private final GatewayConf.ChannelGateways gateways;

    @ServiceActivator(inputChannel = "inputChannel")
    public void getMessage(Message<MessageDTO> message) {
        try {
            messageService.doAction(message.getPayload());
        } catch (RetryException e) {
            gateways.sendInvalidRetryGateway(
                    message.getHeaders().getOrDefault(ConstNames.FAILURE_COUNT_HEADER_NAME, 0),
                    message.getPayload()
            );
        } catch (Exception e) {
            gateways.sendDeadLetterGateway(message.getPayload());
        }
    }
}

