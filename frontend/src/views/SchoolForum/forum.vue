<script setup>
import {useStore} from "@/store";
import {get} from "@/net";
import {ElMessage} from "element-plus";

const store = useStore()
get(`api/forum/types`,(data)=>{
  const array = [];
  array.push({name:"全部",id:0,color:'linear-gradient(45deg,white,red,orange,gold,green,blue)'})
  data.forEach((item)=>{array.push(item)})
  store.forum.types = array
},message => ElMessage.error(message))
</script>

<template>
  <router-view v-slot="{Component}">
    <transition name="el-fade-in-linear" mode="out-in">
      <keep-alive include="PostList">
        <component :is="Component"></component>
      </keep-alive>
    </transition>
  </router-view>
</template>

<style scoped>

</style>