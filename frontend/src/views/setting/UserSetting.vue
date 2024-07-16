<script setup>

import Card from "@/components/Card.vue";
import {Message, Plus, Refresh, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";

const store = useStore();
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())

const baseFormRef = ref()
const emailFormRef = ref()
const baseForm = reactive({
  username:'',
  gender: 1,
  phone: '',
  qq: '',
  wx: '',
  intro: ''
})
const emailForm = reactive({
  email:'',
  code:''
})
const desc = ref('')

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  } else {
    callback()
  }
}
const rules = {
  username: [
    { validator: validateUsername, trigger: ['blur', 'change'] },
    { min: 2, max: 10, message: '用户名的长度必须在2-10个字符之间', trigger: ['blur', 'change'] },
  ],
  email: [
    { required: true, message: '请输入邮件地址', trigger: 'blur' },
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    { message: '请输入获取的验证码', trigger: 'blur' },
  ]
}

const loading = reactive({
  form: true,
  base: false
})
function saveDetails(){
  baseFormRef.value.validate(isValid => {
    if(isValid){
      loading.base = true
      post(`api/user/save-details`, baseForm, () =>{
        ElMessage.success(`用户信息保存成功`)
        store.user.username = baseForm.username
        desc.value = baseForm.intro
        loading.base = false
      },message => {
        ElMessage.warning(message)
        loading.base = false
      })
    }
  })
}

get("/api/user/details",(data) => {
  baseForm.username = store.user.username
  baseForm.gender = data.gender
  baseForm.phone = data.phone
  baseForm.qq = data.qq
  baseForm.wx = data.wx
  baseForm.intro = desc.value = data.intro
  emailForm.email = store.user.email
  emailForm.code = null
  loading.form = false
})

const coldTime = ref(0)
const isEmail = ref(true)
const onValidate = (prop,isValid) => {
  if(prop === 'email')
    isEmail.value = isValid
}
function sendEmailCode(){
  emailFormRef.value.validate( isValid => {
    if(isValid){
      coldTime.value = 60
      get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`,() => {
        ElMessage.success(`验证码已发送到邮箱:${emailForm.email},请注意查收`)
        const handle = setInterval(() => {
          coldTime.value--;
          if (coldTime.value === 0) clearInterval(handle);
        },1000)
      },message => {
        ElMessage.warning(message)
        coldTime.value = 0
      })
    }
  })
}

function modifyEmail(){
  emailFormRef.value.validate( isValid => {
    if (isValid){
      post(`/api/user/modify-email`,emailForm,() => {
        ElMessage.success("邮箱更改成功")
        store.user.email = emailForm.email
        location.reload()
      },message => ElMessage.warning(message))
    }
  })
}
</script>

<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :icon="User" title="账号信息设置" desc="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息" v-loading="loading.form">
        <el-form style="margin: 0 10px 10px 10px" label-position="top" :model="baseForm" :rules="rules" ref="baseFormRef">
          <el-form-item label="用户名(username)" prop="username">
            <el-input v-model="baseForm.username"/>
          </el-form-item>
          <el-form-item label="性别(sex)" prop="gender">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="1">男(male)</el-radio>
              <el-radio :label="0">女(female)</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号(mobile-phone)" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"/>
          </el-form-item>
          <el-form-item label="QQ" prop="qq">
            <el-input v-model="baseForm.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="微信(wechat)" prop="wx">
            <el-input v-model="baseForm.wx"/>
          </el-form-item>
          <el-form-item label="个人简介(description)" prop="intro">
            <el-input type="textarea" :rows="6" v-model="baseForm.intro" maxlength="200"/>
          </el-form-item>
          <div>
            <el-button :icon="Plus" type="primary" :loading="loading.base" @click="saveDetails">保存用户信息</el-button>
          </div>
        </el-form>
      </card>
      <card :icon="Message" title="电子邮箱设置" desc="在这里可以更改您绑定的电子邮箱地址" style="margin-top: 15px">
        <el-form label-position="top" @validate="onValidate" style="margin: 0 10px 10px 10px" :model="emailForm" :rules="rules" ref="emailFormRef">
          <el-form-item label="电子邮箱" prop="email">
            <el-input v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="18">
                <el-input placeholder="请先获取验证码后，再填入" v-model="emailForm.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" @click="sendEmailCode" plain style="width: 100%" :disabled="coldTime>0 || !isEmail">
                  {{ coldTime > 0 ? `请稍后${coldTime}秒` : "获取验证码" }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" @click="modifyEmail" type="danger">更新电子邮箱</el-button>
          </div>
        </el-form>
      </card>
    </div>
    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar :size="70"
                src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
            />
            <div style="font-weight: bold">你好，{{ store.user.username }}</div>
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;color: gray;padding: 10px">
            {{ desc || "这个用户很神秘，不想透露自己的简介"}}
          </div>
        </card>
        <card style="font-size: 14px;margin-top: 10px">
          <div style="">账号注册时间:{{registerTime}}</div>
          <div style="color: grey">欢迎加入我们的这个基础论坛！</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.settings-left{
  flex: 1;
  margin: 20px;
}

.settings-right{
  width: 300px;
  margin: 20px 30px 20px 0;
}
</style>