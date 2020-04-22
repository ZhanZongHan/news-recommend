package com.zzh.service;

import com.zzh.dto.BrowsingLogDto;
import com.zzh.mapper.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    @Autowired
    private LogMapper logMapper;

    public List<BrowsingLogDto> getLogsByPage(Integer start, Integer num){
        return logMapper.getLogsByPage(start - 1, num);
    }

    public List<BrowsingLogDto> getLogsByUserId(Integer userId) {
        return logMapper.getLogsByUserId(userId);
    }

    public List<BrowsingLogDto> getLogsByUsername(String username) {
        return logMapper.getLogsByUsername(username);
    }

}
