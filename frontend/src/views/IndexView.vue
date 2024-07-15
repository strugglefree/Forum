<script setup>

import {logout , get} from "@/net";
import router from "@/router";
import {useStore} from "@/store";
import {ref} from "vue";
import {
  Bowl,
  ChatLineRound,
  Location,
  MoreFilled,
  Promotion,
  School,
  ShoppingTrolley,
  Sunny,
  Search,
  Notebook, Film, UserFilled, Tools, Lock
} from "@element-plus/icons-vue";

const loading = ref(true)

function userLogout(){
  logout(()=>{router.push('/')})
}

const store = useStore()

get('api/user/info',(data)=>{
  store.user = data
  loading.value = false
})

</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="请稍后">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://www.svgrepo.com/show/530377/chat-chat.svg"/>
        <div style="flex: 1" class="user-info">
          <div class="profile">
            <div>{{ store.user.username }}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
        </div>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu style="min-height: calc(100vh - 55px)" default-active="1-1">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="1-1">
                  <template #title>
                    <el-icon><ChatLineRound /></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notebook /></el-icon>
                    分享好书
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><School /></el-icon>
                    学校活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Promotion /></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><MoreFilled /></el-icon>
                    广告位出租
                    <el-tag size="small" style="margin-left: 10px">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Search /></el-icon>
                  探索发现
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><Sunny /></el-icon>
                    自然风景
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Bowl /></el-icon>
                    美食打卡
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><ShoppingTrolley /></el-icon>
                    逛街必去
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Film /></el-icon>
                    影视经典
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Tools /></el-icon>
                  设置
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><UserFilled /></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Lock /></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>
.main-content{
  height: 100vh;
  width: 100vw;
}
.main-content-header{
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  .logo{
    height: 40px;
  }
  .user-info{
    display: flex;
    justify-content: flex-end;
    align-items: center;
    .profile{
      text-align: right;
      margin-right: 20px;
      :first-child{
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }
      :last-child{
        font-size: 12px;
        color: gray;
      }
    }
  }
}
</style>