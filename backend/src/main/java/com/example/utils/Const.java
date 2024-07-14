package com.example.utils;

/**
 * @author Ll
 * @description: Redis的分类
 * @date 2024/7/11 下午7:10
 */
public class Const {
    public static final String JWT_BAN_LIST = "jwt:banlist:";   //令牌黑名单
    public final static String JWT_FREQUENCY = "jwt:frequency:";

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";

    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";

    public static final int ORDER_CORE = -102; //跨域请求的顺序
    public static final int ORDER_LIMIT = -101;

    //请求自定义属性
    public final static String ATTR_USER_ID = "userId";
    //消息队列
    public final static String MQ_MAIL = "mail";
    //用户角色
    public final static String ROLE_DEFAULT = "user";
}
