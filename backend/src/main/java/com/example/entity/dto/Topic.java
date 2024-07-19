package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 帖子详细信息
 * @date 2024/7/19 下午2:17
 */

@Data
@TableName("db_topic")
public class Topic implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private Integer uid;
    private Integer type;
    private Date time;
}
