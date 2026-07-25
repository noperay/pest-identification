package com.zx.insectdetection.mapper;

import com.zx.insectdetection.entity.websoket.Channel;
import com.zx.insectdetection.entity.websoket.ChannelSubscribe;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface ChannelMapper {
    @Insert("insert into channel (id,channel_name,administrator_id,create_time,status) values (#{id},#{channelName},#{administratorId},#{createTime},#{status})")
    Boolean addChannel(Channel channal);

    @Insert("insert into channel_subscribe (channel_id,user_id,create_time) values (#{channelId},#{userId},#{createTime})")
    void addChannelSubscribe(ChannelSubscribe channelSubscribe);
    @Select("select * from channel_subscribe where user_id = #{userId}")
    List<ChannelSubscribe> findChannelByUserId(Integer userId);

    @Select("select * from channel where id = #{channelId}")
    List<Channel> findChannelById(Integer channelId);

    //禁用频道
    @Update("update channel set status = '已禁用' where id = #{channelId}")
    Boolean disableChannel(Integer channelId);

    //启用频道
    @Update("update channel set status = '已启用' where id = #{channelId}")
    Boolean enableChannel(Integer channelId);

    //更新频道
    @Update("update channel set channel_name = #{channelName} where id = #{id}")
    void updateChannel(Channel channel);

    //订阅频道
    @Insert("insert into channel_subscribe (channel_id,user_id,create_time) values (#{channelId},#{userId},now())")
    void subscribeChannel(@Param("channelId") Integer channelId,@Param("userId") Integer userId);

    //查询是否订阅
    @Select("select count(*) from channel_subscribe where channel_id = #{channelId} and user_id = #{userId}")
    Boolean findIfChannelSubscribe(@Param("channelId")Integer channelId,@Param("userId") Integer userId);

    //取消订阅
    @Delete("delete from channel_subscribe where channel_id = #{channelId} and user_id = #{userId}")
    void unsubscribeChannel(@Param("channelId") Integer channelId,@Param("userId") Integer userId);

    //查询频道id
    @Select("select id from channel where administrator_id = #{userId}")
    Integer findChannelIdByUserId(Integer userId);
    //手动添加新频道
    @Insert("insert into channel (channel_name,administrator_id,create_time,status) values (#{channelName},#{administratorId},now(),#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addNewChannel(Channel channel);

    //删除频道
    @Delete("delete from channel where id = #{channelId}")
    void deleteChannel(Integer channelId);

    //查询所有频道
    @Select("select * from channel")
    List<Channel> getAllChannel();
    //根据用户id创建频道
    @Insert("insert into channel (channel_name,administrator_id,create_time,status) values (#{channelName},#{administratorId},now(),#{status})")
    void createChannelByUserId(Channel channel);
}
