package com.zzh.mapper;

import com.zzh.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user_info limit 15")
    List<User> getAll();

    public List<User> getUserById(@Param("userId") Integer userId);

    public List<User> getUserByName(@Param("username") String username);

    public List<User> getUsersByPage(@Param("start") Integer start, @Param("num") Integer num);

    public Integer getCount();
}
