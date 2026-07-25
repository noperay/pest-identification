package com.zx.insectdetection.controller;

import com.zx.insectdetection.entity.article.Article;
import com.zx.insectdetection.entity.others.Result;
import com.zx.insectdetection.entity.user.Administrator;
import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.entity.user.UserFollow;
import com.zx.insectdetection.entity.websoket.Channel;
import com.zx.insectdetection.mapper.UserMapper;
import com.zx.insectdetection.service.UserService;
import com.zx.insectdetection.utils.JwtUtil;
import com.zx.insectdetection.utils.Md5Util;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    String defaltAvatarUrl = "https://insectdetection.oss-cn-guangzhou.aliyuncs.com/2025/03/27/3dd4b28e98e047b8aa154ecfcc95e847.png";
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {

        User u = userService.findByUserName(username);
        if (u == null) {
            userService.register(username, password,0, defaltAvatarUrl);
            return Result.success();
        } else {
            return Result.error("用户名已被占用");
        }
    }

    @PostMapping("/addUser")
    public Result addUser(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password,Integer money) {
        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {
            //没有占用
            userService.addUser(username,password,money,defaltAvatarUrl);
            return Result.success();
        } else {
            //占用
            return Result.error("用户名已被占用");
        }
    }
    //根据用户id查找用户信息
    @GetMapping("/findUserById/{id}")
    public Result findUserById(@PathVariable Integer id) {
        User user = userService.findUserById(id);
        return Result.success(user);
    }
    //登录
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        System.out.println(password);
        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);
        //判断该用户是否存在
        if (loginUser == null) {
            return Result.error("用户名错误");
        }

        //判断密码是否正确  loginUser对象中的password是密文
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,24, TimeUnit.HOURS);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }
    //获取当前用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo() {
        //根据用户名查询用户
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);
        return Result.success(user);
    }
    //根据用户名查询用户
    @GetMapping("/findUserByUsername")
    public Result findUserByUsername(@RequestParam String username) {
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    //更新用户信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }

    //更换头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }
    //更改密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params,@RequestHeader("Authorization") String token) {
        //1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }
        //原密码是否正确
        //调用userService根据用户名拿到原密码,再和old_pwd比对
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUserName(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }
        //newPwd和rePwd是否一样
        if (!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }
        //2.调用service完成密码更新
        userService.updatePwd(newPwd);
        //删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }
    //加款
    @PatchMapping("/addMoney")
    public Result addMoney(@RequestParam Integer money) {
        userService.addMoney(money);
        return Result.success();
    }
    //获取所有用户
    @GetMapping("/getUsers")
    public Result<List<User>> getUsers() {
        List<User> users = userService.findAllUser();
        return Result.success(users);
    }

    // 删除用户
    @DeleteMapping("/deleteUser/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }
    //修改用户信息
    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody @Validated User user) {
//        String newPwd = Md5Util.getMD5String(user.getPassword());
//        user.setPassword(newPwd);
        userService.updateUser(user);
        return Result.success();
    }
    // 用户关注接口
    @PostMapping("/followUser")
    public Result followUser(@RequestParam Integer followee) {
        boolean result = userService.followUser(followee);
        if(result == false) {
            return Result.error("关注失败");
        }
        return Result.success();
    }
    //取消关注
    @PostMapping("/cancelFollow")
    public Result cancelFollow(@RequestParam Integer followee) {
        boolean result = userService.cancelFollow(followee);
        if(result == false) {
            return Result.error("取关失败");
        }
        return Result.success();
    }
    //获取关注表
    @GetMapping("/getFollows")
    public Result<List<UserFollow>> getFollows() {
        List<UserFollow> followList = userService.findFollowList();
        return Result.success(followList);
    }
    //根据id获取创建的频道
    @GetMapping("/getChannelByUserId/{userId}")
    public Result<List<Channel>> getChannelByUserId(@PathVariable Integer userId) {
        List<Channel> channelList = userMapper.getChannelByUserId(userId);
        return Result.success(channelList);
    }
    //我是否关注？？
    @GetMapping("/getMyFollows/{followeeId}")
    public Result<List<UserFollow>> getMyFollows(@PathVariable Integer followeeId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer followerId = (Integer) map.get("id");
        List<UserFollow> myFollows = userMapper.getMyFollows(followerId,followeeId);
        return Result.success(myFollows);
    }
    //计算文章发布数量
    @GetMapping("/getArticleCounts/{userId}")
    public Result<Integer> getArticleCounts(@PathVariable Integer userId) {
        Integer counts = userMapper.getArticleCounts(userId);
        return Result.success(counts);
    }
    //增加访客数量
    @GetMapping("/addVisitorCount/{userId}")
    public Result addVisitorCount(@PathVariable Integer userId) {
        userMapper.addVisitorCount(userId);
        return Result.success();
    }
    //根据用户id获取用户昵称
    @GetMapping("/getUserNameById/{userId}")
    public Result<String> getUserNameById(@PathVariable Integer userId) {
        String userName = userMapper.getUserNameById(userId);
        return Result.success(userName);
    }
    //根据表id删除关注表信息
    @DeleteMapping("/deleteFollow/{id}")
    public Result deleteUserFollow(@PathVariable Integer id) {
        userMapper.deleteUserFollow(id);
        return Result.success();
    }
    //查找所有管理员
    @GetMapping("/findAdmin")
    public Result<List<Administrator>> findAdmin() {
        List<Administrator> adminList = userMapper.findAdmin();
        return Result.success(adminList);
    }
    //修改管理员类型
    @PutMapping("/updateAdmin")
    public Result updateAdmin(@RequestBody @Validated Administrator administrator) {
        userMapper.updateAdmin(administrator);
        return Result.success();
    }
    //删除管理员
    @DeleteMapping("/deleteAdmin/{id}")
    public Result deleteAdmin(@PathVariable Integer id) {
        userMapper.deleteAdmin(id);
        return Result.success();
    }
    //添加管理员
    @PostMapping("/addAdmin")
    public Result addAdmin(@RequestBody @Validated Administrator administrator) {
        userMapper.addAdmin(administrator);
        return Result.success();
    }
    //根据userID查询管理员类型
    @GetMapping("/getAdminType/{userId}")
    public Result<String> findAdminByUserId(@PathVariable Integer userId) {
        String result =  userMapper.findAdminTypeByUserId(userId);
        return Result.success(result);
    }

}
