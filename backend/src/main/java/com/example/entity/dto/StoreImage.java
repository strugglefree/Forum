package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 存储图片信息
 * @date 2024/7/19 上午10:43
 */

@Data
@TableName("db_image_store")
@AllArgsConstructor
public class StoreImage {
    Integer uid;
    String name;
    Date time;
}
