<script setup>

import {logout} from "@/net";
import router from "@/router";

function userLogout(){
  logout(()=>{router.push('/')})
}

</script>

<template>
  <div>
    欢迎来到我们的论坛系统
  </div>
  <el-button @click="userLogout" type="danger" plain>退出登录</el-button>
</template>

<style scoped>

</style>