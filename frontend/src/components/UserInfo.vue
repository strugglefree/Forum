<script setup>

import {Back, Message, Operation, Right} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {isRoleAdmin, logout} from "@/net";
import router from "@/router";
import {ElMessage} from "element-plus";
import {computed} from "vue";
import {useRoute} from "vue-router";

const route = useRoute()
const store = useStore();

const isAdminPage = computed(()=> route.fullPath.startsWith('/admin'))

function userLogout(){
    logout(()=>{
        router.push('/')
        ElMessage.success(`退出登录成功，欢迎您再次使用`)
    })
}
</script>

<template>
    <div class="user-info">
        <template v-if="isRoleAdmin()" >
            <el-button type="primary" v-if="isAdminPage" size="small"
                       @click="router.push('/index')"
                       style="margin-right: 20px">
                回到用户端
                <el-icon style="margin-left: 5px">
                    <Right/>
                </el-icon>
            </el-button>
            <el-button type="danger" v-else size="small"
                       @click="router.push('/admin')"
                       style="margin-right: 20px">
                回到管理端
                <el-icon style="margin-left: 5px">
                    <Right/>
                </el-icon>
            </el-button>
        </template>

        <slot/>
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
</template>

<style scoped>
.user-info{
    width: 320px;
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
</style>