<script setup>

import Card from "@/components/Card.vue";
import {Hide, Setting, Lock, Unlock, Check, RefreshRight} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post, logout} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.new_password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}
const rules = {
  past_password: [
    {required: true, message: '请输入原来的密码', trigger: 'blur'},
  ],
  new_password:[
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  check_password:[
    { required: true, validator: validatePassword, trigger: ['blur', 'change'] }
  ]
}
const formRef = ref();
const valid = ref(false)
const form = reactive({
  past_password: '',
  new_password: '',
  check_password: ''
})
const onValidate = (prop , isValid) => valid.value = isValid
function resetPassword(){
  formRef.value.validate(valid =>{
    if(valid){
      post(`/api/user/change-password`, form ,()=>{
        ElMessage.success("成功更改密码，请重新登录")
        formRef.value.resetFields()
        logout(()=>{router.push('/')})
      },message => ElMessage.warning(message))
    }
  })
}
</script>

<template>
  <div style="margin: auto;max-width: 600px">
    <div style="margin-top: 20px">
      <card :icon="Hide" title="隐私设置" desc="在这里你可以选择隐藏哪些数据，当个未知和神秘的人">
        <div class="checkbox-list">
          <el-checkbox>公开展示我的手机号</el-checkbox>
          <el-checkbox>公开展示我的电子邮箱</el-checkbox>
          <el-checkbox>公开展示我的微信</el-checkbox>
          <el-checkbox>公开展示我的QQ</el-checkbox>
          <el-checkbox>公开展示我的性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0" :icon="Setting" title="更改密码" desc="更改账户密码在这里哦，请牢记您的密码哟！">
        <el-form @validate="onValidate" label-position="top" style="top: 15px" :model="form" ref="formRef" :rules="rules">
          <el-form-item label="当前密码" prop="past_password">
              <el-input :prefix-icon="Unlock" placeholder="当前密码" type="password" v-model="form.past_password"/>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
              <el-input :prefix-icon="Lock" placeholder="新密码" type="password" maxlength="16" v-model="form.new_password"/>
          </el-form-item>
          <el-form-item label="确认密码" prop="check_password">
              <el-input :prefix-icon="Check" placeholder="确认密码" type="password" maxlength="16" v-model="form.check_password"/>
          </el-form-item>
          <div style="margin: 10px 0;text-align: center">
            <el-button type="warning" :icon="RefreshRight" @click="resetPassword" plain>立即重置密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list{
  display: flex;
  flex-direction: column;
  margin: 15px 0 0 10px;
}
</style>