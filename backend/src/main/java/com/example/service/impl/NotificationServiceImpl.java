package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Notification;
import com.example.entity.vo.response.NotificationVO;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ll
 * @description: 消息通知具体实现
 * @date 2024/7/25 下午3:58
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    /**
     * @description: 查找对应的uid的消息
     * @param: [uid]
     * @return: java.util.List<com.example.entity.vo.response.NotificationVO>
     * @author Ll
     * @date: 2024/7/25 下午4:18
     */
    @Override
    public List<NotificationVO> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid",uid))
                .stream()
                .map((obj) -> obj.asViewObject(NotificationVO.class))
                .toList();
    }

    /**
     * @description: 删除对应贴子和对应用户的消息
     * @param: [id, uid]
     * @return: void
     * @author Ll
     * @date: 2024/7/25 下午4:19
     */
    @Override
    public void deleteUserNotification(int id, int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid",uid).eq("id",id));
    }

    /**
     * @description: 删除用户的所有消息
     * @param: [uid]
     * @return: void
     * @author Ll
     * @date: 2024/7/25 下午4:20
     */
    @Override
    public void deleteUserAllNotification(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid",uid));
    }

    /**
     * @description: 新增一条通知
     * @param: [uid, title, content, type, url]
     * @return: void
     * @author Ll
     * @date: 2024/7/25 下午4:20
     */
    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        this.save(notification);
    }
}
