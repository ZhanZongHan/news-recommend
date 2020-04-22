package com.zzh.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSim {

    @Field("userId")
    private Integer userId;
    @Field("recommendedNewsIds")
    private ArrayList<Integer> recommendedNewsIds;
}
