package ru.itfb.meetup.messageTube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itfb.meetup.messageTube.conf.GatewayConf;
import ru.itfb.meetup.messageTube.dto.MessageDTO;
import ru.itfb.meetup.messageTube.exception.RetryException;

import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final GatewayConf.ChannelGateways gateways;

    public Integer sendMessage(String message) {
        var msg = new MessageDTO()
                .setId(generateId())
                .setMessage(message);

        gateways.outputChannel(msg);
        return msg.getId();
    }

    private Integer generateId() {
        return RandomGenerator.getDefault().nextInt(12);
    }

    public void doAction(MessageDTO message) {
        if (message.getId() % 2 == 0) {
            System.out.println("Error the message: " + message);
            throw new RetryException();
        }

        if (message.getId() == 10) {
            throw new RuntimeException();
        }

        System.out.println("Got the message: " + message);
    }
}
