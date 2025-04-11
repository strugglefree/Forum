package com.example.utils;

/**
 * @author Ll
 * @description: 常量字符串集合
 * @date 2024/7/11 下午7:10
 */
public class Const {
    public static final String JWT_BAN_LIST = "jwt:banlist:";   //令牌黑名单
    public final static String JWT_FREQUENCY = "jwt:frequency:";

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";

    public static final String FLOW_LIMIT_COUNTER = "flow:counter:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";
    public static final String BANNED_BLOCK = "Banned:block:";

    public static final int ORDER_CORE = -102; //跨域请求的顺序
    public static final int ORDER_LIMIT = -101;

    //请求自定义属性
    public final static String ATTR_USER_ID = "userId";
    //消息队列
    public final static String MQ_MAIL = "mail";
    //用户角色
    public final static String ROLE_DEFAULT = "user";
    public final static String ROLE_ADMIN = "admin";
    //论坛相关
    public final static String FORUM_WEATHER_CACHE = "weather:cache:";
    public final static String FORUM_IMAGE_COUNTER = "forum:image:";
    public final static String FORUM_TOPIC_CREATE_COUNTER = "forum:topic:create:";
    public final static String FORUM_TOPIC_COMMENT_COUNTER = "forum:topic:comment:";
    public final static String FORUM_TOPIC_CACHE = "forum:topic:cache:";
    public final static String FORUM_TOPIC_PRIVATE_CACHE = "forum:topic:private:cache";
}
