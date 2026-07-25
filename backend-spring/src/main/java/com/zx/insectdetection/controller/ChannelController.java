package com.zx.insectdetection.controller;

import com.zx.insectdetection.entity.others.Result;
import com.zx.insectdetection.entity.websoket.Channel;
import com.zx.insectdetection.entity.websoket.ChannelSubscribe;
import com.zx.insectdetection.mapper.ChannelMapper;
import com.zx.insectdetection.service.ChannelService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/channel")
@Validated
public class ChannelController {
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelMapper channelMapper;

    // 根据用户id增加频道
    @PostMapping("/addChannel")
    public Result<String> addChannel(@RequestParam Integer userId) {
        Boolean result = channelService.addChannel(userId);
        if (result) {
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    // 增加订阅
    @PostMapping("/addChannelSubscribe")
    public Result<String> addChannelSubscribe(@RequestBody ChannelSubscribe channelSubscribe) {
        channelService.addChannelSubscribe(channelSubscribe);
        return Result.success();
    }

    //根据用户id查找订阅的频道
    @GetMapping("/findChannelByUserId")
    public Result<List<ChannelSubscribe>> findChannelByUserId(@RequestParam Integer userId) {
        List<ChannelSubscribe> ChannelSubscribe = channelService.findChannelByUserId(userId);
        return Result.success(ChannelSubscribe);
    }
    //根据channalId查信息
    @GetMapping("/findChannelById")
    public Result<List<Channel>> findChannelById(@RequestParam Integer channelId) {
        List<Channel> Channel = channelService.findChannelById(channelId);
        return Result.success(Channel);
    }
    //禁用频道
    @GetMapping("/disableChannel")
    public Result<String> disableChannel(@RequestParam Integer channelId) {
        System.out.println(channelId);
        Boolean result = channelMapper.disableChannel(channelId);
        System.out.println(result);
        return Result.success();
    }

    //启用频道
    @GetMapping("/enableChannel")
    public Result<String> enableChannel(@RequestParam Integer channelId) {
        System.out.println(channelId);
        channelMapper.enableChannel(channelId);
        return Result.success();
    }
    //修改频道信息
    @PutMapping("/updateChannel")
    public Result<String> updateChannel(@RequestBody Channel channel) {
        channelMapper.updateChannel(channel);
        return Result.success();
    }
    //订阅频道
    @PostMapping("/subscribeChannel")
    public Result<String> subscribeChannel(@RequestParam Integer channelId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        channelMapper.subscribeChannel(channelId,userId);
        return Result.success();
    }
    //查询是否有订阅该频道
    @GetMapping("/findIfChannelSubscribe/{channelId}")
    public Result<String> findIfChannelSubscribe(@PathVariable Integer channelId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Boolean result = channelMapper.findIfChannelSubscribe(channelId,userId);
        if(result) {
            return Result.success("已订阅");
        }else{
            return Result.success("未订阅");
        }

    }
    //取消订阅频道
    @PostMapping("/unsubscribeChannel")
    public Result<String> unsubscribeChannel(@RequestParam Integer channelId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        channelMapper.unsubscribeChannel(channelId,userId);
        return Result.success();
    }
    //新建频道
    @PostMapping("/createChannel")
    public Result<String> createChannel(@RequestBody Channel channel) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        channel.setAdministratorId(userId);
        channelMapper.addNewChannel(channel);
        channelMapper.subscribeChannel(channel.getId(),userId);
        return Result.success();
    }
    //删除频道
    @PostMapping("/deleteChannel")
    public Result<String> deleteChannel(@RequestParam Integer channelId) {
        channelMapper.deleteChannel(channelId);
        return Result.success();
    }
    //获取全部频道
    @GetMapping("/getChannelList")
    public Result<List<Channel>> getAllChannel() {
        List<Channel> channel = channelMapper.getAllChannel();
        return Result.success(channel);
    }

    //根据自定义用户id新建频道
    @PostMapping("/createChannelByUserId")
    public Result<String> createChannelByUserId(@RequestBody Channel channel) {
        channelMapper.createChannelByUserId(channel);
        return Result.success();
    }


}
