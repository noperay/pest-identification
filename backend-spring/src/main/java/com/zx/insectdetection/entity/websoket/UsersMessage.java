package com.zx.insectdetection.entity.websoket;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "users_message")
public class UsersMessage {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String message;
    private Date sendTime;
}

