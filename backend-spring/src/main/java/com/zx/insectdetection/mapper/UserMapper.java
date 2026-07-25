package com.zx.insectdetection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zx.insectdetection.entity.user.Administrator;
import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.entity.user.UserFollow;
import com.zx.insectdetection.entity.websoket.Channel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    //根据用户名查询用户
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);
    //根据用户id查找用户信息
    @Select("select * from user where id=#{id}")
    User findUserById(Integer id);
    //添加
    @Insert("insert into user(username,nickname,password,create_time,update_time,money,avatar_url,follows,fans,views_count,visitor_count)" +
            " values(#{username},#{username},#{password},now(),now(),#{money},#{url},0,0,0,0)")
    void add(@Param("username") String username, @Param("password") String password, @Param("money") Integer money,@Param("url") String url);

    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    @Update("update user set avatar_url=#{url},update_time=now() where id=#{id}")
    void updateAvatar(@Param("url") String url,@Param("id") Integer id);

    @Update("update user set password=#{md5String},update_time=now() where id=#{id}")
    void updatePwd(@Param("md5String")String md5String,@Param("id") Integer id);
    //充值
    @Update("update user set money=#{money} where id=#{id}")
    void addMoney(@Param("money")Integer money,@Param("id") Integer id);
    //根据用户名查询用户
    @Select("select * from user")
    List<User> findAllUser();
    // 删除用户
    @Delete("delete from user where id = #{id}")
    void deleteUser(Integer id);
    //添加用户
    @Insert("insert into user(username,nickname, password, create_time, update_time, money, avatar_url,follows,fans,views_count,visitor_count) " +
            "values (#{username},#{username}, #{md5String}, now(),now(),#{money},#{url},0,0,0,0)")
    void addUser(@Param("username") String username,@Param("md5String") String md5String,@Param("money") Integer money,@Param("url") String url);
    //修改用户信息
    @Update("update user set username=#{username},nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void updateUser(User user);
    //关注
    @Insert("insert into user_follows(follower_id,followee_id,follow_time) values(#{followerId},#{followeeId},now())")
    int followUser(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
    //取消关注
    @Delete("delete from user_follows where follower_id = #{followerId} and followee_id = #{followeeId}")
    int cancelFollow(@Param("followerId")Integer followerId,@Param("followeeId") Integer followeeId);
    // 查询是否已经存在关注关系
    @Select("SELECT COUNT(*) FROM user_follows WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int checkFollowExists(@Param("followerId") Integer followerId, @Param("followeeId") Integer followeeId);
    //user表关注+
    @Update("update user set follows=follows+1 where id=#{userId}")
    int addUserFollows(Integer userId);
    //user表关注-
    @Update("update user set follows=follows-1 where id=#{userId}")
    int deleteUserFollows(Integer userId);
    //user表fans+
    @Update("update user set fans=fans+1 where id=#{userId}")
    int addUserFans(Integer userId);
    //user表fans-
    @Update("update user set fans=fans-1 where id=#{userId}")
    int deleteUserFans(Integer userId);
    //获取关注表
    @Select("select * from user_follows")
    List<UserFollow> findFollowList();
    //viewsCount+1 文章阅读量
    @Update("update user set views_count=views_count+1 where id=#{userId}")
    int addViewsCount(Integer userId);
    //根据用户id查找创建的频道
    @Select("select * from channel where administrator_id=#{userId}")
    List<Channel> getChannelByUserId(Integer userId);
    //获取我的关注列表
    @Select("select * from user_follows where follower_id=#{followerId} and followee_id=#{followeeId}")
    List<UserFollow> getMyFollows(@Param("followerId") Integer followerId,@Param("followeeId") Integer followeeId);
    //获取文章数量
    @Select("select count(*) from article where user_id=#{userId}")
    Integer getArticleCounts(Integer userId);
    //增加访客
    @Update("update user set visitor_count=visitor_count+1 where id=#{userId}")
    void addVisitorCount(Integer userId);
    //根据id获取用户名
    @Select("select nickname from user where id=#{userId}")
    String getUserNameById(Integer userId);
    //删除关注
    @Delete("delete from user_follows where id=#{id}")
    void deleteUserFollow(Integer id);
    //获取管理员
    @Select("select * from administrator")
    List<Administrator> findAdmin();
    //添加管理员
    @Insert("insert into administrator(user_id,type,create_time) values(#{userId},#{type},now())")
    void addAdmin(Administrator administrator);
    //更新管理员
    @Update("update administrator set type=#{type} where id=#{id}")
    void updateAdmin(Administrator administrator);
    //删除管理员
    @Delete("delete from administrator where id=#{id}")
    void deleteAdmin(Integer id);
    //根据userId查询管理员类型
    @Select("select type from administrator where user_id=#{userId}")
    String findAdminTypeByUserId(Integer userId);
}
