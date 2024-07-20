<script setup>

import LightCard from "@/components/LightCard.vue";
import {Clock, Connection, EditPen, Guide, Sunrise} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import axios from "axios";

const store = useStore()

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
const list = ref(null)
get(`api/forum/types`,(data)=>{
  store.forum.types = data
},message => ElMessage.error(message))
function updateList(){
  get('/api/forum/list-topic?type=0&page=0',(data) => {list.value = data})
}
updateList();
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
</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 1000px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen /></el-icon> 点击发表主题...
        </div>
      </light-card>
      <light-card style="height: 30px;margin-top: 10px"/>
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px" v-if="store.forum.types">
        <light-card v-for="item in list" class="topic-card">
          <div style="display: flex">
            <div>
              <el-avatar :size="30" :src="`${axios.defaults.baseURL}/images${item.avatar}`"></el-avatar>
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
            <div class="topic-type" :style="{
              color: store.findTypeById(item.type)?.color + 'EE',
              'border-color': store.findTypeById(item.type)?.color + '77',
              'background': store.findTypeById(item.type)?.color + '22',
            }">
              {{store.findTypeById(item.type)?.name}}
            </div>
            <span style="font-weight: bold;margin-left: 5px">{{item.title}}</span>
          </div>
          <div class="topic-content">{{item.content}}</div>
          <div style="display: grid;grid-template-columns: repeat(3,1fr);grid-gap: 10px">
            <el-image class="topic-image" v-for="img in item.image" :src="img" fit="cover"></el-image>
          </div>
        </light-card>
      </div>
    </div>
    <div style="width: 300px">
      <div style="position: sticky;top: 20px">
        <light-card>
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
    <topic-editor :show="editor" @close="editor = false" @created="editor = false;updateList()"></topic-editor>
  </div>
</template>

<style lang="less" scoped>
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
  .topic-type{
    display: inline-block;
    font-size: 12px;
    border: solid 0.5px grey;
    border-radius: 3px;
    padding: 0 5px;
    height: 18px;
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

.dark .create-topic{
  background-color: #222222;
}
</style>