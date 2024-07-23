<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  Clock,
  Compass,
  Connection,
  Document,
  Edit,
  EditPen,
  Guide,
  Microphone,
  Sunrise,
  Picture,
  Sugar, StarFilled, Star, ArrowRightBold
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import CollectedTopic from "@/components/CollectedTopic.vue";

const store = useStore()
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false,
  tops: []
})
const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth()+1} 月 ${date.getDate()} 日`
})

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})

const editor = ref(false)

get(`/api/forum/top-topic`,data=> topics.tops=data)

watch(() => topics.type ,() => {
  resetList()
},{immediate:true})
function updateList(){
  if(topics.end) return;
  get(`/api/forum/list-topic?type=${topics.type}&page=${topics.page}`,(data) => {
    if(data){
      data.forEach((item)=>{topics.list.push(item)});
      topics.page++
    }
    if(!data || data.length < 10){}
     topics.end=true
  })
}

function onTopicCreate(){
  editor.value = false
  resetList()
}

function resetList(){
  topics.page = 0
  topics.list = [];
  topics.end = false
  updateList()
}

function avatarUrl(avatar){
  if(avatar){
    return `${axios.defaults.baseURL}/images${avatar}`
  }else
    return "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
}

navigator.geolocation.getCurrentPosition(position => {
      const lon = position.coords.longitude
      const lat = position.coords.latitude
      get(`/api/forum/weather?longitude=${lon}&latitude=${lat}`,(data)=>{
        Object.assign(weather,data)
        weather.success = true
      },message => ElMessage.error(message))
    }, error => {
      console.warn(error)
      ElMessage.warning("位置信息获取超时，请检查网络设置")
      get(`/api/forum/weather?longitude=116.3912757&latitude=39.906217`,(data)=>{
        Object.assign(weather,data)
        weather.success = true
      },message => ElMessage.error(message))
    },
    {
      timeout: 3000,
      enableHighAccuracy: true
    }
)
const ip = ref("")
get(`/api/forum/get-ip`,(data)=>ip.value=data)

const collects = ref(false)
</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 1000px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen /></el-icon> 点击发表主题...
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon><Edit /></el-icon>
          <el-icon><Document /></el-icon>
          <el-icon><Compass /></el-icon>
          <el-icon><Picture /></el-icon>
          <el-icon><Microphone /></el-icon>
        </div>
      </light-card>
      <light-card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px;">
        <div class="top-topic" v-for="item in topics.tops"  @click="router.push('/index/topic-detail/'+item.id)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleString()}}</div>
        </div>
      </light-card>
      <light-card style="margin-top: 10px;display: flex;gap: 7px">
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types"
             @click="topics.type = item.id">
          <color-dot :color="item.color"/>
          <span style="margin-left: 5px">{{item.name}}</span>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list">
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
               v-if="store.forum.types" v-infinite-scroll="updateList">
            <light-card v-for="item in topics.list" class="topic-card"
                        @click="router.push('/index/topic-detail/'+item.id)">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="avatarUrl(item.avatar)"></el-avatar>
                </div>
                <div style="margin-left: 7px;transform: translateY(-2px)">
                  <div>{{item.username}}</div>
                  <div style="font-size: 12px;color: grey" >
                    <el-icon><Clock/></el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">{{new Date(item.time).toLocaleString()}}</div>
                  </div>
                </div>
              </div>
              <div>
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold;margin-left: 5px">{{item.title}}</span>
              </div>
              <div class="topic-content">{{item.content}}</div>
              <div style="display: grid;grid-template-columns: repeat(3,1fr);grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.image" :src="img" fit="cover"></el-image>
              </div>
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle"><Sugar /></el-icon>{{item.like}}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><StarFilled /></el-icon>{{item.collect}}收藏
                </div>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 300px">
      <div style="position: sticky;top: 20px">
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <div>
              <el-icon><Star/></el-icon>
              <span style="margin-left: 5px;">查看我的收藏</span>
            </div>
            <el-icon><ArrowRightBold /></el-icon>
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold;">
            <el-icon><Guide /></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;color: grey;margin: 10px">
            Spring Boot 是一个开源的 Java-based 框架，用于创建易于开发、快速运行和易于维护的微服务应用。
            它是 Spring 框架的一个扩展，旨在简化 Spring 应用程序的配置和部署过程。
            Spring Boot 通过为应用提供默认配置（"约定优于配置"的原则），使开发人员能够快速启动和运行新项目，同时还能根据项目需求进行灵活的定制。
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold;">
            <el-icon><Sunrise /></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <weather :data="weather"/>
        </light-card>
        <light-card style="margin-top: 10px">
          <div>
            <div class="info-text">
              <div>当前日期</div>
              <div>{{today}}</div>
            </div>
            <div class="info-text">
              <div>当前IP</div>
              <div>{{ip}}</div>
            </div>
          </div>
        </light-card>
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <div>
            <el-icon><Connection /></el-icon>
            推荐链接
          </div>
          <el-divider style="margin: 10px auto"/>
          <div style="margin: 10px;display: grid;grid-template-columns: repeat(2,1fr);grid-gap: 10px ">
            <el-link type="primary" :underline="false" href="https://element-plus.org">Element-Plus</el-link>
            <el-link type="success" :underline="false" href="https://cn.vuejs.org/">Vue.js</el-link>
            <el-link type="warning" :underline="false" href="https://spring.io/projects/spring-boot">SpringBoot</el-link>
            <el-link type="danger" :underline="false" href="https://github.com">Github</el-link>
          </div>
        </div>
      </div>
    </div>
    <topic-editor :show="editor" @close="editor = false" @created="onTopicCreate"></topic-editor>
    <collected-topic :show="collects" @close="collects = false"></collected-topic>
  </div>
</template>

<style lang="less" scoped>
.collect-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover{
    cursor: pointer;
    opacity: 0.6;
  }
}

.top-topic{
  display: flex;

  div:first-of-type{
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: background-color .3s;

    &:hover{
      color: grey;
    }
  }

  div:nth-of-type(2){
    flex: 1;
    font-size: 13px;
    text-align: right;
    color: grey;
  }

  &:hover{
    cursor: pointer;
  }
}

.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;

  &.active {
    border: solid 1px #ead4c4;
  }

  &:hover {
    cursor: pointer;
    background-color: #dadada;
  }
}


.topic-card{
  padding: 15px;
  transition: scale .3s;
  &:hover{
    scale: 1.02;
    cursor: pointer;
  }
  .topic-content{
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .topic-image{
    height: 100%;
    width: 100%;
    max-height: 120px;
    border-radius: 5px;
  }
}
.info-text{
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: grey;
}
.create-topic{
  background-color: #ececec;
  border-radius: 5px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover{
    cursor: pointer;
  }
}

.dark {
  .create-topic{
    background-color: #222222;
  }
  .type-select-card{
    background-color: #282828;

    &:active{
      border: solid 1px #51493d;
    }

    &:hover{
      background-color: #5e5e5e;
    }
  }
}

</style>