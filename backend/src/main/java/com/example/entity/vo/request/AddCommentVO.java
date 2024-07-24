package com.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author Ll
 * @description: 增加评论所需要的类
 * @date 2024/7/24 下午3:13
 */
@Data
public class AddCommentVO {
    @Min(1)
    int tid;
    String content;
    @Min(-1)
    int quote;
}
