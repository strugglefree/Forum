<script setup>

import {
    Bell, Notification,
    ChatDotSquare, Location,
    School, Document,
    Umbrella, User, Files, Monitor, Collection, DataLine
} from "@element-plus/icons-vue";
import UserInfo from "@/components/UserInfo.vue";
import {inject, onMounted, ref} from "vue";
import router from "@/router";
import {useRoute} from "vue-router";

const  adminMenu = [
    {
        title: '校园论坛管理',icon: Location, sub: [
            {title: '用户管理', icon: User , index: '/admin/user' },
            {title: '帖子广场管理', icon: ChatDotSquare , index: '/admin/forum'},
            {title: '失物招领管理', icon: Bell},
            {title: '校园活动管理', icon: Notification},
            {title: '表白墙管理', icon: Umbrella},
            {title: '合作机构管理', icon: School},
        ]
    }, {
        title: '探索与发现管理',icon: Location, sub: [
            {title: '成绩管理', icon: Document},
            {title: '课程表管理', icon: Files},
            {title: '教务通知管理', icon: Monitor},
            {title: '在线图书管理', icon: Collection},
            {title: '预约教室管理', icon: DataLine}
        ]
    }
]

const pageTabs = ref([])
const route = useRoute()
const loading = inject("userLoading")
function addAdminTab(menu){
  if(!menu.index) return
  if(pageTabs.value.findIndex(tab => tab.name === menu.index) < 0){
    pageTabs.value.push({
      title: menu.title,
      name: menu.index,
    })
  }
}

function handleTabClick({ props }){
  router.push(props.name)
}

function handleTabClose(name){
  const index = pageTabs.value.findIndex(tab => tab.name === name);
  const isCurrent = name === route.fullPath
  pageTabs.value.splice(index, 1)
  if(pageTabs.value.length > 0){
    if(isCurrent){
      router.push(pageTabs.value[Math.max(0 , index -1)].name)
    }
  }else {
    router.push('/admin')
  }
}

onMounted(() => {
  const initPage = adminMenu
      .flatMap(menu => menu.sub)
      .find(index => index.index === route.fullPath)
  if(initPage){
    addAdminTab(initPage)
  }
})
</script>

<template>
    <div class="admin-content" v-loading="loading" element-loading-text="请稍后">
        <el-container style="height: 100%">
            <el-aside width="230px" class="admin-content-aside">
                <div class="logo-box">
                    <el-image class="logo" src="https://www.svgrepo.com/show/530377/chat-chat.svg"/>
                </div>
                <el-scrollbar style="height: calc(100vh - 57px)">
                    <el-menu
                        router
                        style="min-height: calc(100vh - 57px);border: none"
                        :default-openeds="['1','2']"
                        :default-active="$route.path">
                        <el-sub-menu :index="(index + 1).toString()"
                                     v-for="(menu, index) in adminMenu">
                            <template #title>
                                <el-icon>
                                    <component :is="menu.icon"></component>
                                </el-icon>
                                <span><b>{{ menu.title }}</b></span>
                            </template>
                            <el-menu-item :index="subMenu.index"
                                          v-for="subMenu in menu.sub"
                                          @click="addAdminTab(subMenu)">
                                <template #title>
                                    <el-icon><Component :is="subMenu.icon"/></el-icon>
                                    {{ subMenu.title}}
                                </template>
                            </el-menu-item>
                        </el-sub-menu>
                    </el-menu>
                </el-scrollbar>
            </el-aside>
            <el-container>
                <el-header class="admin-content-header">
                    <div style="flex: 1">
                      <el-tabs type="card"
                               :model-value="route.fullPath"
                               closable
                               @tab-remove="handleTabClose"
                               @tab-click="handleTabClick">
                        <el-tab-pane v-for="tab in pageTabs" :label="tab.title"
                                     :name="tab.name"
                                     :key="tab.name" />
                      </el-tabs>
                    </div>
                    <user-info/>
                </el-header>
                <el-main>
                  <router-view v-slot="{ Component }">
                      <keep-alive>
                        <component :is="Component"/>
                      </keep-alive>
                  </router-view>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style scoped>
.admin-content{
    height: 100vh;
    width: 100vw;

    .admin-content-aside {
        border-right: solid 1px var(--el-border-color);

        .logo-box {
            text-align: center;
            padding: 15px 0 10px;
            height: 32px;

            .logo{
                height: 32px;
        }
        }
    }

    .admin-content-header {
        border-bottom: solid 1px var(--el-border-color);
        height: 57px;
        display: flex;
        align-items: center;
        box-sizing: border-box;

      :deep(.el-tabs__header){
        height: 32px;
        margin-bottom: 0;
        border-bottom: 0;
      }

      :deep(.el-tabs__nav){
        border: none;
        gap: 10px
      }

      :deep(.el-tabs__item){
        height: 35px;
        padding: 0 15px;
        border-radius: 6px;
        border: solid 1px var(--el-border-color);
      }
    }
}
</style>