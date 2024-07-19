package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.StoreImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 对图片储存的数据库操作
 * @date 2024/7/19 上午10:48
 */
@Mapper
public interface ImageStoreMapper extends BaseMapper<StoreImage> {
}
