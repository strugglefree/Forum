<script setup>

import LightCard from "@/components/LightCard.vue";
import {Connection, EditPen, Guide, Sunrise} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

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
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <light-card style="height: 150px" v-for="item in 10">

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
    <topic-editor :show="editor" @close="editor = false" @created="editor = false"></topic-editor>
  </div>
</template>

<style lang="less" scoped>
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