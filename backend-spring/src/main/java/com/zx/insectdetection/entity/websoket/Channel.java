package com.zx.insectdetection.entity.websoket;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String channelName;
    private Integer administratorId;
    private String status;
    private LocalDateTime CreateTime;
}
