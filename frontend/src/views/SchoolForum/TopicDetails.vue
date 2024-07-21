<script setup>
import {useRoute} from "vue-router";
import {get} from "@/net";
import axios from "axios";
import {reactive} from "vue";
import {ArrowLeft, Female, Male} from "@element-plus/icons-vue";
import { QuillDeltaToHtmlConverter } from 'quill-delta-to-html';
import Card from "@/components/Card.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";

const route = useRoute()
const tid = route.params.tid
const topic = reactive({
  data:null,
  comment:[]
})

get(`/api/forum/topic?tid=${tid}`,data=>{
  topic.data=data
})

function convertToHtml(content) {
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, { inlineStyles: true });
  return converter.convert();
}

</script>

<template>
  <div class="topic-page" v-if="topic.data">
    <div class="topic-main" style="position: sticky;top: 0;z-index: 5;">
      <card style="display: flex;width: 100%">
        <el-button :icon="ArrowLeft" type="info" @click="router.push('/index')"
                   size="small" plain round>返回主页面</el-button>
        <div style="flex: 1;text-align: right">
          <topic-tag :type="topic.data.type"/>
          <span style="font-size: 15px;color: var(--el-text-color);margin-left: 5px;
          font-weight: bold;font-family: 仿宋,serif">{{topic.data.title}}</span>
        </div>
      </card>
    </div>
    <div class="topic-main">
      <div class="topic-main-left">
        <el-avatar :size="60" :src="`${axios.defaults.baseURL}/images${topic.data.user.avatar}`"></el-avatar>
        <div>
          <div style="font-size: 18px;font-weight: bold">
            {{topic.data.user.username}}
            <span style="color: hotpink" v-if="topic.data.user.gender === 0"
            ><el-icon><Female/></el-icon></span>
            <span style="color: dodgerblue" v-if="topic.data.user.gender === 1"
            ><el-icon><Male/></el-icon></span>
          </div>
          <div class="desc">{{topic.data.user.email}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="text-align: left;margin: 0 5px">
          <div class="desc">微信(wechat):{{topic.data.user.wx}}</div>
          <div class="desc">QQ:{{topic.data.user.qq}}</div>
          <div class="desc">手机号:{{topic.data.user.phone}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="desc" style="margin: 0 5px">
          <div style="font-weight: bold;font-size: 14px">个人简介：</div>
          <span style="text-align: center">&nbsp;&nbsp;&nbsp;&nbsp;{{topic.data.user.intro}}</span>
        </div>
      </div>
      <div class="topic-main-right">
        <div class="topic-content" v-html="convertToHtml(topic.data.content)">

        </div>
      </div>
    </div>
    <div>

    </div>
  </div>
</template>

<style scoped>
.topic-page{
  display: flex;
  flex-direction: column;
  padding: 10px 0;
  gap: 10px;
}
.topic-main{
  display: flex;
  margin: 0 auto;
  border-radius: 7px;
  background-color: var(--el-bg-color);
  width: 800px;

  .topic-main-left{
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);

    .desc{
      font-size: 12px;
      color: grey;
    }
  }
  .topic-main-right{
    padding: 10px 20px;
    width: 600px;

    .topic-content{
      font-size: 14px;
      line-height: 22px;
      opacity: 0.8;
    }
  }
}
</style>