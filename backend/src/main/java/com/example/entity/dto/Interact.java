package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 点赞或收藏所需要的具体参数（数据实体类）
 * @date 2024/7/22 下午2:35
 */

@Data
@AllArgsConstructor
public class Interact {
    int tid;
    Integer uid;
    Date time;
    String type;

    public String toKey(){
        return tid+":"+uid;
    }

    public static Interact parseInteract(String str , String type){
        String[] keys = str.split(":");
        return new Interact(Integer.parseInt(keys[0]),Integer.parseInt(keys[1]),new Date(),type);
    }
}
