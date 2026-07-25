package com.zx.insectdetection.entity.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "administrator")
public class Administrator {
    @Id
    private Integer id;//主键ID
    private Integer userId;
    private String type;
    private LocalDateTime createTime;
}
