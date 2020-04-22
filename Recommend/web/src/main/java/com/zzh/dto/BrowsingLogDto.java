package com.zzh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrowsingLogDto {
    private Integer logId;
    private Integer userId;
    private String username;
    private Integer gender;
    private Integer newsId;
    private String title;
    private String url;
    private Date browsingDate;
}
