package com.zzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// News类的部分对象，展示时不需要读取内容，只要给个url
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsPart {
    private Integer newsId;
    private String url;
    private Date publicationDate;
    private String title;
}
