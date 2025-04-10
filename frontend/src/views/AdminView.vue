<script setup>

import {
    Bell, Notification,
    ChatDotSquare, Location,
    School, Document,
    Umbrella, User, Files, Monitor, Collection, DataLine
} from "@element-plus/icons-vue";
import UserInfo from "@/components/UserInfo.vue";
import {inject} from "vue";

const  adminMenu = [
    {
        title: '校园论坛管理',icon: Location, sub: [
            {title: '用户管理', icon: User},
            {title: '帖子广场管理', icon: ChatDotSquare},
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

const loading = inject("userLoading")
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
                            <el-menu-item :index="menu.index" v-for="subMenu in menu.sub">
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
                    <div style="flex: 1"></div>
                    <user-info/>
                </el-header>
                <el-main>Main</el-main>
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
    }
}
</style>