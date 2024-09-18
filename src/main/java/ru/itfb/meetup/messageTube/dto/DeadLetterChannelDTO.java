package ru.itfb.meetup.messageTube.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeadLetterChannelDTO {
    private MessageDTO message;
    private String stacktrace;
}
