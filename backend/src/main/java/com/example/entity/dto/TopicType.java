package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ll
 * @description: 帖子类型
 * @date 2024/7/19 下午1:04
 */
@Data
@AllArgsConstructor
@TableName("db_topic_type")
public class TopicType implements BaseData {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("`desc`")
    private String desc;
    private String color;
}
