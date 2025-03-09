package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 程凯
 * @Package: com.example.entity.dto
 * @Project: Forum
 * @Name: Follow
 * @Date: 2025/3/7  下午8:40
 */
@Data
@TableName("db_follow")
public class Follow {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer uid;
    Integer followUid;

    public Follow(Integer uid, Integer followUid) {
        this.uid = uid;
        this.followUid = followUid;
    }
}
