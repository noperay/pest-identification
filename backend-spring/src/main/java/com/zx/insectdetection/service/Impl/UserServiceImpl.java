package com.zx.insectdetection.service.Impl;

import com.zx.insectdetection.entity.websoket.ChannelSubscribe;
import com.zx.insectdetection.mapper.ChannelMapper;
import com.zx.insectdetection.mapper.MessageMapper;
import com.zx.insectdetection.mapper.UserMapper;
import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.entity.user.UserFollow;
import com.zx.insectdetection.service.ChannelService;
import com.zx.insectdetection.service.UserService;
import com.zx.insectdetection.utils.Md5Util;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public User findByUserName(String username) {
        User u = userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password, Integer money, String url) {
        String md5String = Md5Util.getMD5String(password);
        userMapper.add(username,md5String,money,url);
        User u = userMapper.findByUserName(username);
        Integer userId = u.getId();
        channelService.addChannel(userId);
        Integer channelId = channelMapper.findChannelIdByUserId(userId);
        ChannelSubscribe channelSubscribe = new ChannelSubscribe();
        channelSubscribe.setUserId(userId);
        channelSubscribe.setChannelId(10001);
        channelSubscribe.setCreateTime(LocalDateTime.now());
        channelService.addChannelSubscribe(channelSubscribe);
        channelMapper.subscribeChannel(channelId,userId);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        messageMapper.updateMessageUsername(user.getNickname(),user.getId());
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),id);
    }
    @Override
    public void addMoney(Integer money) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.addMoney(money,id);
    }

    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }

    @Override
    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }

    @Override
    public void addUser(String username, String password, Integer money, String url) {
        String md5String = Md5Util.getMD5String(password);
        userMapper.addUser(username,md5String,money,url);
        User u = userMapper.findByUserName(username);
        Integer userId = u.getId();
        channelService.addChannel(userId);
        Integer channelId = channelMapper.findChannelIdByUserId(userId);
        ChannelSubscribe channelSubscribe = new ChannelSubscribe();
        channelSubscribe.setUserId(userId);
        channelSubscribe.setChannelId(10001);
        channelSubscribe.setCreateTime(LocalDateTime.now());
        channelService.addChannelSubscribe(channelSubscribe);
        channelMapper.subscribeChannel(channelId,userId);
    }

    @Override
    public void updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateUser(user);
        messageMapper.updateMessageUsername(user.getNickname(),user.getId());
    }
    @Override
    public boolean followUser(Integer followeeId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer followerId = (Integer) map.get("id");
        int count = userMapper.checkFollowExists(followerId, followeeId);
        if (count > 0) {
            return false;
        }
        int result1 = userMapper.followUser(followerId, followeeId);
        int result2 = userMapper.addUserFollows(followerId);
        int result3 = userMapper.addUserFans(followeeId);
        if (result1 > 0 && result2 > 0 && result3 > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelFollow(Integer followeeId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer followerId = (Integer) map.get("id");
        int result1 = userMapper.cancelFollow(followerId, followeeId);
        int result2 = userMapper.deleteUserFollows(followerId);
        int result3 = userMapper.deleteUserFans(followeeId);
        if(result1 > 0 && result2 > 0 && result3 > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<UserFollow> findFollowList() {
        return userMapper.findFollowList();
    }

    @Override
    public User findUserById(Integer id) {
        return userMapper.findUserById(id);
    }

}
