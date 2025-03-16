package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 请求帖子创建时所需要的类
 * @date 2024/7/19 下午2:27
 */
@Data
public class TopicCreateVO {
    @Length(min = 1, max = 30)
    private String title;
    private JSONObject content;
    @Min(1)
    private Integer type;
    private String see;
}
