package com.zx.insectdetection.service;

import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.entity.user.UserFollow;

import javax.validation.constraints.Pattern;
import java.util.List;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);

    //注册
    void register(String username, String password, Integer money, String url);

    //更新
    void update(User user);

    //更新头像
    void updateAvatar(String avatarUrl);

    //更新密码
    void updatePwd(String newPwd);

    //加款
    void addMoney(Integer money);

    List<User> findAllUser();

    void deleteUser(Integer id);

    void addUser(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password, Integer money, String url);

    void updateUser(User user);

    boolean followUser(Integer followee);

    boolean cancelFollow(Integer userId);

    List<UserFollow> findFollowList();

    User findUserById(Integer id);

}
