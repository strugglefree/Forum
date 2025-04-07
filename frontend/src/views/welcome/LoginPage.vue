<script setup>

import {inject, reactive, ref} from "vue";
import {Lock, User} from "@element-plus/icons-vue";
import {login} from "@/net";
import router from "@/router";
import {getUserInfo} from "@/net/api/user";

const formRef = ref();

const form = reactive({
  username:"",
  password:"",
  rememberMe: false,
})

const rule ={
  username:[
    {required:true ,message:"请输入用户名或邮箱"}
  ],
  password:[
    {required:true ,message:'请输入密码'}
  ]
}

const loading = inject('userLoading')

function userLogin(){
  formRef.value.validate((valid)=>{
    if(valid){
      login(form.username,form.password,form.rememberMe,()=>{
          getUserInfo(loading)
          router.push("/index")
      })
    }
  })
}
</script>

<template>
  <div class="right-card" style="text-align: center;margin: 0 20px;height: 75%;">
    <div style="margin-top: 20%;font-weight:bolder;font-size: 25px">
      账&nbsp;号&nbsp;登&nbsp;录
    </div>
    <div style="font-size: 15px;margin-top:3%">在进入系统之前，请先输入用户名和密码进行登录</div>
    <div style="margin-top: 10%" >
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="255" type="text" placeholder="用户名或邮箱">
            <template #prefix>
              <el-icon style="font-weight: bolder"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input show-password v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prefix>
              <el-icon style="font-weight: bolder"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="rememberMe">
              <el-checkbox style="color: gray" label="记住我" v-model="form.rememberMe"></el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
              <el-link @click="router.push('/reset')">忘记密码?</el-link>
          </el-col>
        </el-row>
      </el-form>
      <div style="margin-top: 5%">
        <el-button @click="userLogin" style="width: 75%;" type="primary" plain>登&nbsp;&nbsp;录</el-button>
      </div>
      <el-divider>
        <span style="font-size: 13px;color: gray">没有账号</span>
      </el-divider>
      <div>
        <el-button @click="router.push('/register')" style="width: 75%;" type="warning">立即注册</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.right-card {
  position: absolute;
  top:50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 25%;
  z-index: 1;
  background-color: var(--el-bg-color);
  border-radius: 10px; /* 可选：圆角 */
  padding: 20px; /* 可选：内边距 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 可选：阴影 */
}
</style>