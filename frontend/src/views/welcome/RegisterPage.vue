<script setup>
import {computed, reactive, ref} from "vue";
import router from "@/router";
import {Check, CloseBold, EditPen, Lock, Message, UserFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {apiAuthAskCode, apiAuthRegister} from "@/net/api/user";

const coldTime = ref(0);

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const form = reactive({
  username:'',
  password:'',
  password_repeat:'',
  email:'',
  code:''
})

const formRef = ref();

const rules = {
  username: [
    { validator: validateUsername, trigger: ['blur', 'change'] },
    { min: 2, max: 10, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change'] },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
  ]
}

function askCode(){
  if(isEmailValid){
    apiAuthAskCode(form.email, "register",coldTime)
  }else{
    ElMessage.warning('请输入正确的邮箱地址')
  }
}

const isEmailValid = computed(()=>/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email))

function register(){
  formRef.value.validate((valid) => {
    if(valid){
      apiAuthRegister({
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      })
    }else {
      ElMessage.warning("请完整填写表单内容")
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px;width: 22%">
    <div style="text-align: right">
      <el-button :icon="CloseBold" @click="router.back" link></el-button>
    </div>
    <div style="margin-top: 30%">
      <div style="font-size: 20px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;margin-top: 2%;color: gray">欢迎注册我们的论坛，请在下方填写相关信息</div>
      <div style="margin-top: 15%">
        <el-form :rules="rules" :model="form" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" maxlength="15" type="text">
              <template #prefix>
                <el-icon><UserFilled /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="密码" maxlength="20" type="password">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" placeholder="确认密码" maxlength="20" type="password">
              <template #prefix>
                <el-icon><Check /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="邮箱" maxlength="255" type="text">
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="6">
              <el-col :span="17">
                <el-input v-model="form.code" type="text" placeholder="验证码">
                  <template #prefix>
                    <el-icon><EditPen /></el-icon>
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
      <div style="margin-top: 10%">
        <el-button type="warning" @click="register" plain style="width: 70%">立即注册</el-button>
      </div>
      <div style="margin-top: 7%">
        <span style="font-size: 14px;color: gray">已有账号？</span>
        <el-link @click="router.push('/')" style="translate: 0 -2.5px;font-weight: bold">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>