package com.zzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrowsingLog {
    private Integer logId;
    private Integer userId;
    private Integer newsId;
    private Date browsingDate;
}
