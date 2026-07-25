package com.zx.insectdetection.service;

import com.zx.insectdetection.entity.websoket.Channel;
import com.zx.insectdetection.entity.websoket.ChannelSubscribe;

import java.util.List;

public interface ChannelService {
    Boolean addChannel(Integer userId);

    void addChannelSubscribe(ChannelSubscribe channelSubscribe);

    List<ChannelSubscribe> findChannelByUserId(Integer userId);

    List<Channel> findChannelById(Integer channelId);
}
