package com.zx.insectdetection.entity.user;



import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @NotNull
    private Integer id;//主键ID
    private String username;//用户名
//    @JsonIgnore
    private String password;//密码
    @Pattern(regexp = "^\\S{1,10}$")
    private String nickname;//昵称

    @Email
    private String email;//邮箱
    private String avatarUrl;//用户头像 URL
    private Integer money;//用户头像 URL
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;//更新时间
    private int follows;
    private int fans;
    private boolean online;
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer viewsCount;
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer visitorCount;
    private String type;
}
