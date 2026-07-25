package com.zx.insectdetection.entity.websoket;

import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;


@Data
public class ChannelSubscribe {
    @Id
    private Integer id;
    private Integer userId;
    private Integer channelId;
    private LocalDateTime createTime;
}
