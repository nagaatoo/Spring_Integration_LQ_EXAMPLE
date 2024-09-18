package ru.itfb.meetup.messageTube.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class MessageDTO {
    private Integer id;
    private String message;
}
