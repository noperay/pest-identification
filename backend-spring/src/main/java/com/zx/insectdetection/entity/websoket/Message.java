package com.zx.insectdetection.entity.websoket;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String username;
    private String message;
    private Integer channelId;
    private Date sendTime;

}
