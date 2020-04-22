package com.zzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer userId;
    private String username;
    private String password;
    private Date registerDate;
    private Integer gender; // 0：女  1：男
    private Date birthday;
    private Integer authority;  // 0：普通用户   1：管理员
}
