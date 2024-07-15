<script setup>

import {logout , get} from "@/net";
import router from "@/router";
import {useStore} from "@/store";
import {reactive, ref} from "vue";
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
  Notebook, Film, UserFilled, Tools, Lock, Position, Operation, Message, Back
} from "@element-plus/icons-vue";

const loading = ref(true)
const searchInput = reactive({
  type: '1',
  text: ''
})
const store = useStore()

get('api/user/info',(data)=>{
  store.user = data
  loading.value = false
})
function userLogout(){
  logout(()=>{router.push('/')})
}
</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="请稍后">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
       <div>
         <el-row :gutter="6">
           <el-col :span="10"><el-image class="logo" src="https://www.svgrepo.com/show/530377/chat-chat.svg"/></el-col>
           <el-col :span="14"><div style="translate: 0 10px">x&nbsp;x论坛</div></el-col>
         </el-row>
       </div>
        <div style="flex: 1;padding: 0 40px 0 100px;text-align: center">
          <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="搜索论坛相关内容...">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
            <template #append>
              <el-select v-model="searchInput.type" style="width: 100px">
                <el-option value="1" label="帖子广场"/>
                <el-option value="2" label="分享好书"/>
                <el-option value="3" label="学校活动"/>
                <el-option value="4" label="表白墙"/>
              </el-select>
            </template>
          </el-input>
        </div>
        <div class="user-info">
          <div class="profile">
            <div>{{ store.user.username }}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-dropdown>
            <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
            <template #dropdown>
              <el-dropdown-item>
                <el-icon><Operation /></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Message /></el-icon>
                消息列表
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon><Back /></el-icon>
                退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu
                router
                style="min-height: calc(100vh - 55px)"
                :default-active="$route.path">
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
                  <el-icon><Position /></el-icon>
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
                <el-menu-item index="/index/user-setting">
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
        <el-main class="main-content-page">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <router-view v-slot="{Component}">
              <transition name="el-fade-in-linear" mode="out-in">
                <component :is="Component" style="height: 100%"></component>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less" scoped>

.main-content-page{
  padding: 0;
  background-color: #f2f3f6;
}

.dark .main-content-page{
  background-color: #1b1a1a;
}

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

    .el-avatar:hover{
      cursor: pointer;
    }
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