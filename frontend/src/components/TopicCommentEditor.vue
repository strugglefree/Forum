<script setup>
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const props = defineProps({
  show: Boolean,
  tid: String,
  quote:Number
})

const emit = defineEmits(['close'])
const content = ref()

function comment(){
  post(`/api/forum/add-comment`,{
    tid: props.tid,
    quote: props.quote,
    content: JSON.stringify(content.value)
  },()=>{
    ElMessage.success("发表评论成功")
    emit("close")
  })
}
</script>

<template>
  <div>
    <el-drawer :model-value="show"
               title="评论"
               @close="emit('close')"
               direction="btt" :size="270"
               :close-on-click-modal="false"
    >
      <div>
        <div>
          <quill-editor style="height: 120px;" v-model:content="content" placeholder="发言时注意文明用语哦~"/>
        </div>
        <div style="margin-top: 10px;text-align: right">
          <el-button type="success" @click="comment">发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer){
  width: 800px;
  margin: 20px auto;
  border-radius: 10px;
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.el-drawer__body){
  padding: 10px;
}
</style>