package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ll
 * @description: 用户隐私状态类
 * @date 2024/7/17 上午11:16
 */
@Data
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId
    final private Integer id;
    private boolean email = true;
    private boolean phone = true;
    private boolean wx = true;
    private boolean qq = true;
    private boolean gender = true;

    /**
     * @description: 隐藏的用户信息
     * @param: []
     * @return: java.lang.String[]
     * @author Ll
     * @date: 2024/7/21 下午3:13
     */
    public String[] hiddenFields(){
        List<String> hiddenFields = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try{
                if(field.getType().equals(boolean.class) && !field.getBoolean(this)){
                    hiddenFields.add(field.getName());
                }
            }catch (Exception ignored){}
        }
        return hiddenFields.toArray(String[]::new);
    }
}
