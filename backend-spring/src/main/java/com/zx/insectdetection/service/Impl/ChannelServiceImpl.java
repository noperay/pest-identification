package com.zx.insectdetection.service.Impl;

import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.entity.websoket.Channel;
import com.zx.insectdetection.entity.websoket.ChannelSubscribe;
import com.zx.insectdetection.mapper.ChannelMapper;
import com.zx.insectdetection.mapper.UserMapper;
import com.zx.insectdetection.service.ChannelService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean addChannel(Integer userId) {
        User user = userMapper.findUserById(userId);
        Channel channel = new Channel();
        channel.setChannelName(user.getUsername()+"的频道");
        channel.setAdministratorId(userId);
        channel.setStatus("已启用");
        channel.setCreateTime(LocalDateTime.now());
        Boolean result = channelMapper.addChannel(channel);
        if (result) {
            return true;
        }
        return false;
    }

    @Override
    public void addChannelSubscribe(ChannelSubscribe channelSubscribe) {
        channelSubscribe.setCreateTime(LocalDateTime.now());
        channelMapper.addChannelSubscribe(channelSubscribe);
    }

    @Override
    public List<ChannelSubscribe> findChannelByUserId(Integer userId) {
        return channelMapper.findChannelByUserId(userId);
    }

    @Override
    public List<Channel> findChannelById(Integer channelId) {
        return channelMapper.findChannelById(channelId);
    }
}
