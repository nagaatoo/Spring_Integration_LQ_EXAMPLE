package ru.itfb.meetup.messageTube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.itfb.meetup.messageTube.service.MessageService;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/generate/{message}")
    public Integer generateMessage(@PathVariable("message") String message) {
        return messageService.sendMessage(message);
    }

}
