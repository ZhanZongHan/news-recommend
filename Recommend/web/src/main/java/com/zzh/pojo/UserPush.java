package com.zzh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPush {
    private User user;
    private List<NewsShowing> RecommendedNews;
    private List<NewsShowing> normalNews;
}
