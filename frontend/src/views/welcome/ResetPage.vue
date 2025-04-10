<script setup>

import {computed, reactive, ref} from 'vue'
import {Check, CloseBold, EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import router from "@/router";
import {apiAuthAskCode, apiAuthResetConfirm, apiAuthResetPassword} from "@/net/api/user";
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== VerifyForm.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules = {
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ]
}

const active = ref(0)
const VerifyForm = reactive({
  email: "",
  code: '',
  password: '',
  password_repeat: ''
})
const formRef = ref();
const coldTime = ref(0);
const isEmailValid = computed(()=>/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(VerifyForm.email))

function askCode() {
  if (isEmailValid) {
    apiAuthAskCode(VerifyForm.email, "reset",coldTime)
  } else {
    ElMessage.warning('请输入正确的邮箱地址')
  }
}

function confirmReset(){
  formRef.value.validate((valid) => {
    if(valid) {
      apiAuthResetConfirm({
        email: VerifyForm.email,
        code: VerifyForm.code
      }, active)
    }
  })
}

function doReset(){
  formRef.value.validate((valid) => {
    if(valid) {
      apiAuthResetPassword({...VerifyForm})
    }
  })
}
</script>

<template>
  <div style="text-align: center;padding: 0 40px;width: 23%">
    <div style="text-align: right">
      <el-button :icon="CloseBold" @click="router.back" link></el-button>
    </div>
    <div style="margin-top: 15%">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件" />
        <el-step title="重新设定密码" />
      </el-steps>
    </div>
    <div v-if="active===0">
      <div style="margin-top: 28%">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 15px;margin-top: 2%;color: gray">请输入对应账户的电子邮箱地址</div>
      </div>
      <div style="margin-top: 13%">
        <el-form :model="VerifyForm" :rules="rules" ref="formRef">
          <el-form-item prop="email">
            <el-input v-model="VerifyForm.email" placeholder="邮箱" maxlength="255" type="text">
              <template #prefix>
                <el-icon style="font-weight: bolder"><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="6">
              <el-col :span="18">
                <el-input v-model="VerifyForm.code" type="text" placeholder="验证码">
                  <template #prefix>
                    <el-icon style="font-weight: bolder"><EditPen /></el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="5">
                <el-button type="success" @click="askCode" :disabled="!isEmailValid || coldTime>0" plain>
                  {{ coldTime>0 ? `请稍后${coldTime}` : "获取验证码" }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 13%">
        <el-button type="info" @click="confirmReset" style="width: 70%">一键验证</el-button>
      </div>
    </div>
    <div v-if="active===1">
      <div style="margin-top: 28%">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 15px;margin-top: 2%;color: gray">请输入您的新密码，请务必牢记，切勿泄露他人</div>
      </div>
      <div style="margin-top: 13%">
        <el-form :model="VerifyForm" :rules="rules" ref="formRef">
          <el-form-item prop="password">
            <el-input v-model="VerifyForm.password" placeholder="密码" maxlength="20" type="password">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password_repeat">
            <el-input v-model="VerifyForm.password_repeat" placeholder="确认密码" maxlength="20" type="password">
              <template #prefix>
                <el-icon><Check /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 13%">
        <el-button type="primary" @click="doReset" style="width: 70%">立即重置</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>