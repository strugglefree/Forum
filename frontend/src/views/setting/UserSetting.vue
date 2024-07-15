<script setup>

import Card from "@/components/Card.vue";
import {Message, Plus, Refresh, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed} from "vue";

const store = useStore();
const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())
</script>

<template>
  <div style="display: flex">
    <div class="settings-left">
      <card :icon="User" title="账号信息设置" desc="在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息">
        <el-form style="margin: 0 10px 10px 10px" label-position="top">
          <el-form-item label="用户名">
            <el-input/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group>
              <el-radio label="1">男(male)</el-radio>
              <el-radio label="2">女(female)</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input/>
          </el-form-item>
          <el-form-item label="QQ">
            <el-input/>
          </el-form-item>
          <el-form-item label="微信">
            <el-input/>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input type="textarea" :rows="6"/>
          </el-form-item>
          <div>
            <el-button :icon="Plus" type="primary">保存用户信息</el-button>
          </div>
        </el-form>

      </card>
      <card :icon="Message" title="电子邮箱设置" desc="在这里可以更改您绑定的电子邮箱地址" style="margin-top: 15px">
        <el-form label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮箱">
            <el-input/>
          </el-form-item>
          <el-form-item label="电子邮箱">
            <el-row :gutter="10" style="width: 100%">
              <el-col :span="18">
                <el-input placeholder="请先获取验证码后，再填入"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" plain style="width: 100%">获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" type="danger">更新电子邮箱</el-button>
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
            Vue.js 是一个用于构建用户界面的渐进式JavaScript框架。
            它采用自底向上增量开发设计，核心库专注于视图层，易于与其他库或现有项目整合。
            其特点包括响应式的数据绑定和组合式的组件系统，使开发更高效和灵活。
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