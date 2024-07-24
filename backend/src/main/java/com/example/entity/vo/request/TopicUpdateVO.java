package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 帖子更新需要的参数
 * @date 2024/7/24 下午12:20
 */

@Data
public class TopicUpdateVO {
    @Min(0)
    int id;
    @Min(0)
    @Max(5)
    int type;
    @Length(min = 1,max = 30)
    String title;
    JSONObject content;
}
