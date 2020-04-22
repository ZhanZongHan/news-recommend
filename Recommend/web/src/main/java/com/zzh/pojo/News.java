package com.zzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private Integer newsId;
    private String url;
    private Date publicationDate;
    private String title;
    private String content;
    private String author;
    private String category;
}
