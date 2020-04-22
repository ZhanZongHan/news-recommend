package com.zzh.mapper;

import com.zzh.dto.BrowsingLogDto;
import com.zzh.pojo.BrowsingLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogMapper {
    public List<BrowsingLogDto> getLogsByPage(@Param("start") Integer start, @Param("num") Integer num);

    public List<BrowsingLogDto> getLogsByUserId(@Param("userId") Integer userId);

    public List<BrowsingLogDto> getLogsByUsername(@Param("username") String username);

    public List<BrowsingLog> getAllSimpleLogs();
}
