package com.zzh.mapper;

import com.zzh.pojo.News;
import com.zzh.pojo.NewsPart;
import com.zzh.pojo.NewsShowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    public List<NewsPart> getNewsPartByPage(@Param("start") Integer start, @Param("num") Integer num);

    public List<News> getNewsByPage(@Param("start") Integer start, @Param("num") Integer num);

    public List<NewsPart> getNewsPartById(@Param("newsId") Integer newsId);

    public List<NewsShowing> getNewsShowingById(@Param("newsId") Integer newsId);

    public List<NewsShowing> getNewsShowingByPage(@Param("start") Integer start, @Param("num") Integer num);
}
