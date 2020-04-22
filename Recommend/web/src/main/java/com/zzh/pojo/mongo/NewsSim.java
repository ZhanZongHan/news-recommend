package com.zzh.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsSim {
    @Field("newsId")
    private Integer newsId;
    @Field("recommendedNewsIds")
    private List<Integer> recommendedNewsIds;

}
