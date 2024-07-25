package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.CommentVO;
import com.example.entity.vo.response.TopTopicVO;
import com.example.entity.vo.response.TopicDetailsVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.mapper.*;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Ll
 * @description: 发布帖子的相关功能的实现
 * @date 2024/7/19 下午1:01
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    private TopicTypesMapper mapper;
    @Resource
    private FlowUtils flowUtils;
    @Resource
    private CacheUtils cacheUtils;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private AccountDetailsMapper accountDetailsMapper;
    @Resource
    private AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private TopicCommentMapper topicCommentMapper;
    @Resource
    private TopicMapper topicMapper;
    @Resource
    private NotificationService notificationService;

    @PostConstruct
    public void init() {
        type = this.getListTopicTypes().stream().map(TopicType::getId).collect(Collectors.toSet());
    }

    private static Set<Integer> type = null;

    @Override
    public List<TopicType> getListTopicTypes() {
        return mapper.selectList(null);
    }

    /**
     * @description: 发布帖子
     * @param: [vo, uid]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/20 下午1:50
     */
    @Override
    public String createTopic(TopicCreateVO vo, int uid) {
        if (this.textLimitCheck(vo.getContent(),20000)){
            return "您输入的字符超出字数限制了，请做部分精简";
        }
        if(!type.contains(vo.getType())){ return "非法类型！"; }
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if(flowUtils.limitPeriodCountCheck(key, 10, 1800)){
            return "您发文过于频繁，请稍后再试...";
        }
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo,topic);
        topic.setUid(uid);
        topic.setContent(vo.getContent().toString());
        topic.setTime(new Date());
        if(!this.save(topic)){
            return "内部错误，请联系管理员";
        }
        cacheUtils.deleteFromCache(Const.FORUM_TOPIC_CACHE + "*");
        return null;
    }
    /**
     * @description: 获取帖子的预览（一次只从库里取最多十个）
     * @param: [page, type]
     * @return: java.util.List<com.example.entity.vo.response.TopicPreviewVO>
     * @author Ll
     * @date: 2024/7/21 下午1:24
     */
    @Override
    public List<TopicPreviewVO> getListTopic(int page, int type) {
        String key = Const.FORUM_TOPIC_CACHE + page + ':' + type;
        List<TopicPreviewVO> list = cacheUtils.getListFromCache(key,TopicPreviewVO.class);
        if(list != null){ return list;}
        Page<Topic> p = Page.of(page,10);
        if(type == 0) baseMapper.selectPage(p,Wrappers.<Topic>query().orderByDesc("time"));
        else baseMapper.selectPage(p,Wrappers.<Topic>query().eq("type",type).orderByDesc("time"));
        List<Topic> topics = p.getRecords();
        if (topics.isEmpty()) { return null;}
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }

    /**
     * @description: 获取置顶帖子列表
     * @param: []
     * @return: java.util.List<com.example.entity.vo.response.TopTopicVO>
     * @author Ll
     * @date: 2024/7/21 下午1:27
     */
    @Override
    public List<TopTopicVO> getListTopTopic() {
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time").eq("top", 1));
        return topics.stream().map((topic) -> {
            TopTopicVO vo = new TopTopicVO();
            BeanUtils.copyProperties(topic,vo);
            return vo;
        }).toList();
    }

    /**
     * @description: 获取帖子详情
     * @param: [tid]
     * @return: com.example.entity.vo.response.TopicDetailsVO
     * @author Ll
     * @date: 2024/7/21 下午3:47
     */
    @Override
    public TopicDetailsVO getTopicDetails(int tid,int uid) {
        TopicDetailsVO vo = new TopicDetailsVO();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic,vo);
        TopicDetailsVO.Interact interact = new TopicDetailsVO.Interact(
                hasInteract(tid,uid,"like"),
                hasInteract(tid,uid,"collect")
        );
        vo.setInteract(interact);
        TopicDetailsVO.User user = new TopicDetailsVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user,topic.getUid()));
        vo.setComments(topicCommentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid",tid)));
        return vo;
    }
    /**
     * @description: 先查看缓存中有没有数据，没有则看数据库。(查是否发生了交互 )
     * @param: [tid, uid, type]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/23 下午12:43
     */
    private boolean hasInteract(int tid , int uid , String type){
        String key = tid + ":" + uid;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key)))
            return Boolean.parseBoolean(stringRedisTemplate.opsForHash().entries(key).get(type).toString());
        return baseMapper.userInteractCount(type,tid,uid) > 0 ;
    }

    /**
     * @description: 保存点赞或收藏的数据
     * @param: [interact, state]
     * @return: void
     * @author Ll
     * @date: 2024/7/22 下午4:06
     */
    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()){
            stringRedisTemplate.opsForHash().put(type,interact.toKey(),Boolean.toString(state));
            this.saveInteractSchedule(type);
        }
    }

    /**
     * @description: 获取收藏的帖子
     * @param: [uid]
     * @return: java.util.List<com.example.entity.vo.response.TopicPreviewVO>
     * @author Ll
     * @date: 2024/7/23 下午3:40
     */
    @Override
    public List<TopicPreviewVO> getCollectionTopic(int uid) {
        return baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVO vo = new TopicPreviewVO();
                    if(topic.getContent() == null) return null;
                    BeanUtils.copyProperties(topic, vo);
                    return vo;
                })
                .toList();
    }


    /**
     * @description: 更新帖子内容
     * @param: [vo, uid]
     * @return: String
     * @author Ll
     * @date: 2024/7/24 下午12:26
     */
    @Override
    public String updateTopic(TopicUpdateVO vo, int uid) {
        if (this.textLimitCheck(vo.getContent(),20000)){
            return "您输入的字符超出字数限制了，请做部分精简";
        }
        if(!type.contains(vo.getType())){ return "非法类型！"; }
        baseMapper.update(null,Wrappers.<Topic>update()
                .eq("uid",uid)
                .eq("id",vo.getId())
                .set("title",vo.getTitle())
                .set("type",vo.getType())
                .set("content",vo.getContent().toString())
        );
        return null;
    }

    /**
     * @description: 创建评论
     * @param: [vo, uid]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/24 下午3:19
     */
    @Override
    public String createComment(AddCommentVO vo, int uid) {
        String key = Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        if(flowUtils.limitPeriodCountCheck(key, 3, 60)){
            return "您发言过于频繁，请稍后再试...";
        }
        if (this.textLimitCheck(JSONObject.parseObject(vo.getContent()),2000)){
            return "您输入的字符超出字数限制了，请做部分精简";
        }
        TopicComment comment = new TopicComment();
        BeanUtils.copyProperties(vo,comment);
        comment.setUid(uid);
        comment.setTime(new Date());
        topicCommentMapper.insert(comment);
        Topic topic = topicMapper.selectById(vo.getTid());
        Account account = accountMapper.selectById(uid);
        if(vo.getQuote() > 0){
            TopicComment topicComment = topicCommentMapper.selectById(vo.getQuote());
            if(!Objects.equals(account.getId(),topicComment.getUid())){
                notificationService.addNotification(topicComment.getUid(),
                        "您有一条新的帖子评论回复",
                        account.getUsername()+"回复了你发表的评论，快去看看吧",
                        "success",
                        "/index/topic-detail/" + topicComment.getTid()
                );
            }
        }else if (!Objects.equals(account.getId(), topic.getUid())) {
            notificationService.addNotification(topic.getUid(),
                    "您有一条新的帖子回复",
                    account.getUsername()+"回复了你发表的贴子:"+topic.getTitle()+",快去看看吧",
                    "success",
                    "/index/topic-detail/" + topic.getId()
            );
        }
        return null;
    }
    /**
     * @description: 获取评论
     * @param: [tid, page]
     * @return: java.util.List<com.example.entity.vo.response.CommentVO>
     * @author Ll
     * @date: 2024/7/25 下午2:33
     */
    @Override
    public List<CommentVO> getComments(int tid, int page) {
        Page<TopicComment> p = Page.of(page,10);
        topicCommentMapper.selectPage(p,Wrappers.<TopicComment>query().eq("tid",tid));
        return p.getRecords().stream().map((obj)->{
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(obj,vo);
            if(obj.getQuote()>0) {
                TopicComment comment = topicCommentMapper.selectOne(Wrappers.<TopicComment>query()
                        .eq("id", obj.getQuote()).orderByAsc("time"));
                if(comment != null) {
                    JSONObject text = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.decreaseContent(text.getJSONArray("ops"),builder,(ignore)->{});
                    vo.setQuote(builder.toString());
                }else
                    vo.setQuote("此评论已被删除");
            }
            CommentVO.User user = new CommentVO.User();
            vo.setUser(this.fillUserDetailsByPrivacy(user,obj.getUid()));
            return vo;
        }).toList();
    }

    /**
     * @description: 删除对应的评论
     * @param: [cid, uid]
     * @return: void
     * @author Ll
     * @date: 2024/7/25 下午2:57
     */
    @Override
    public void deleteComment(int cid, int uid) {
        topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("id",cid).eq("uid",uid));
    }

    private final Map<String,Boolean> state = new HashMap<>(); //存储状态
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);//具有两个线程池，用于调度任务

    /**
     * @description: 将缓存区的数据控制在3秒提交到数据库一次
     * @param: [type]
     * @return: void
     * @author Ll
     * @date: 2024/7/22 下午3:56
     */
    private void saveInteractSchedule(String type){
        if(!state.getOrDefault(type,false)){
            state.put(type,true);
            service.schedule(() -> {
                this.saveInteract(type);
                state.put(type,false);
            },3, TimeUnit.SECONDS);
        }
    }

    /**
     * @description: 从缓存中取出相应的状态放入数据库中
     * @param: [type]
     * @return: void
     * @author Ll
     * @date: 2024/7/22 下午3:43
     */
    private void saveInteract(String type){
        synchronized (type.intern()){
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            stringRedisTemplate.opsForHash().entries(type).forEach((k,v) -> {
                if(Boolean.parseBoolean(v.toString())){
                    check.add(Interact.parseInteract(k.toString(),type));
                }else{
                    uncheck.add(Interact.parseInteract(k.toString(),type));
                }
            });
            if(!check.isEmpty()){
                baseMapper.addInteract(check,type);
            }
            if(!uncheck.isEmpty()){
                baseMapper.deleteInteract(uncheck,type);
            }
            stringRedisTemplate.delete(type);
        }
    }

    /**
     * @description: 隐私过滤
     * @param: [target, uid]
     * @return: T
     * @author Ll
     * @date: 2024/7/21 下午3:48
     */
    private <T> T fillUserDetailsByPrivacy(T target , int uid){
        Account account = accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        AccountDetails accountDetails = accountDetailsMapper.selectById(uid);
        String[] hidden = null;
        if (accountPrivacy != null) {
            hidden = accountPrivacy.hiddenFields();
        }
        if (hidden != null) {
            BeanUtils.copyProperties(account,target,hidden);
            BeanUtils.copyProperties(accountDetails,target,hidden);
        }
        return target;
    }

    /**
     * @description: 将Topic类的对象转化成TopicPreviewVO
     * @param: [topic]
     * @return: com.example.entity.vo.response.TopicPreviewVO
     * @author Ll
     * @date: 2024/7/21 下午1:25
     */
    private TopicPreviewVO resolveToPreview(Topic topic){
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()),vo);
        BeanUtils.copyProperties(topic,vo);
        vo.setLike(baseMapper.interactCount("like",topic.getId()));
        vo.setCollect(baseMapper.interactCount("collect",topic.getId()));
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        this.decreaseContent(ops,previewText,(v) -> images.add(v.toString()));
        vo.setContent(previewText.length() > 300 ? previewText.substring(0,300) : String.valueOf(previewText));
        vo.setImage(images);
        return vo;
    }

    /**
     * @description:  减少展示的内容
     * @param: [ops, previewText, imageHandle]
     * @return: void
     * @author Ll
     * @date: 2024/7/25 下午12:30
     */
    private void decreaseContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandle){
        for(Object op : ops){
            Object insert = JSONObject.from(op).get("insert");
            if(insert instanceof String text){
                if(previewText.length() >= 300) continue;
                previewText.append(text);
            }else if(insert instanceof Map<?,?> map){
                Optional.ofNullable(map.get("image")).ifPresent(imageHandle);
            }
        }
    }

    /**
     * @description: 检查是否超出字符限制
     * @param: [text,max]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/19 下午3:10
     */
    private boolean textLimitCheck(JSONObject text ,int max){
        if(text == null) return true;
        long length = 0;
        for (Object op : text.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if(length>max) return true;
        }
        return false;
    }
}
