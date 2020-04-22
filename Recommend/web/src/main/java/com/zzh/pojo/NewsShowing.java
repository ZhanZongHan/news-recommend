package com.zzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsShowing {
    // 用于展示在前端的字段
    private Integer newsId;
    private String url;
    private Date publicationDate;
    private String title;
    private String introduction;
}
