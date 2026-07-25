package com.zx.insectdetection.entity.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "user_follows")
@Data
public class UserFollow {
    @Id
    @NotNull
    private Integer id;

    private Integer  followerId;

    private Integer  followeeId;

    private Date followTime;


}