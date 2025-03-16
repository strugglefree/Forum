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
    Notebook, Film, UserFilled, Tools, Lock, Position, Operation, Message, Back, Bell, Check, Star
} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import LightCard from "@/components/LightCard.vue";
import axios from "axios";

const loading = ref(true)
const searchInput = reactive({
  type: '1',
  text: ''
})
const store = useStore()
const notification = ref([])

const loadNotification = () => {
  get(`api/notification/list`, data=>notification.value = data)
}

loadNotification();
get('api/user/info',(data)=>{
  store.user = data
  loading.value = false
})
function userLogout(){
  logout(()=>{
    router.push('/')
    ElMessage.success(`退出登录成功，欢迎您再次使用`)
  })
}
function confirmNotification(id,url){
  get(`api/notification/delete?id=${id}`,()=>{
    loadNotification()
    window.open(url)
  })
}

function deleteAllNotifications(){
  get(`api/notification/delete-all`,()=>loadNotification())
}

const followList = ref([])
const followInfoList = ref([])
get(`/api/follow/getFollowList?uid=${store.user.id}`,(data)=>followList.value=data)
get(`/api/follow/getFollowInfo?uid=${store.user.id}`,(data)=>followInfoList.value=data)

function avatarUrl(avatar){
    if(avatar){
        return `${axios.defaults.baseURL}/images${avatar}`
    }else
        return "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
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
            <el-popover placement="bottom" :width="250" trigger="hover">
            <template #reference>
              <el-badge is-dot style="margin-right: 20px" :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Star/></el-icon>
                  <div style="font-size: 10px">关注</div>
                </div>
              </el-badge>
            </template>
            <el-empty :image-size="80" description="暂时没有关注的人哦~" v-if="!followInfoList.length"/>
            <el-scrollbar :max-height="500" v-else>
              <div v-for="item in followInfoList" style="display: flex;justify-content: space-between;" >
                  <span>{{item.username}}</span>
                  <el-avatar style="margin-left: auto" :size="30" :src="avatarUrl(item.avatar)"></el-avatar>
              </div>
            </el-scrollbar>
          </el-popover>
            <el-popover placement="bottom" :width="350" trigger="hover">
            <template #reference>
              <el-badge is-dot style="margin-right: 20px" :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell/></el-icon>
                  <div style="font-size: 10px">消息</div>
                </div>
              </el-badge>
            </template>
            <el-empty :image-size="80" description="暂时没有新消息哦~" v-if="!notification.length"/>
            <el-scrollbar :max-height="500" v-else>
              <light-card v-for="item in notification" class="notification-item" @click="confirmNotification(item.id,item.url)">
                <div>
                  <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                  <span style="font-weight: bold">{{item.title}}</span>
                </div>
                <el-divider style="margin: 7px 0 3px 0"/>
                <div style="font-size: 13px">
                  {{item.content}}
                </div>
              </light-card>
            </el-scrollbar>
            <div style="margin-top: 10px">
              <el-button size="small" type="info" :icon="Check" @click="deleteAllNotifications"
                         style="width: 100%" plain>清除全部未读消息</el-button>
            </div>
          </el-popover>
          <div class="profile">
            <div>{{ store.user.username }}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"/>
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
                :default-openeds="['1','3']"
                :default-active="$route.path">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="/index">
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
                <el-menu-item index="/index/privacy-setting">
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
.notification-item{
  transition: .3s;

  &:hover{
    cursor: pointer;
    opacity: 0.7;
  }
}

.notification{
  font-size: 22px;
  text-align: center;
  line-height: 14px;
  transition: color .3s;

  &:hover{
    cursor: pointer;
    color: grey;
  }
}

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