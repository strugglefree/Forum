<script setup>
import {Postcard, Share} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import { ImageExtend, QuillWatch } from "quill-image-super-solution-module";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {ElMessage} from "element-plus";
import axios from "axios";
import {accessHeader, get, post} from "@/net";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store"
const store = useStore();
const props = defineProps({
  show: Boolean,
  defaultTitle: {
    default: '',
    type: String
  },
  defaultText: {
    default: '',
    type: String
  },
  defaultType: {
    default: null,
    type: Number
  },
  submitButton: {
    default: '立即发表主题',
    type: String
  },
  submit:{
    default: (editor,success) => {
      post(`/api/forum/create-topic`,{
        type: editor.type.id,
        title: editor.title,
        content: editor.text
      },()=>{
        ElMessage.success("发布成功！")
        success();
      })
    },
    type: Function
  }
})

const emit = defineEmits(['close','created'])

const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false,
})

const refEditor = ref()
function initEditor(){
  if(props.defaultText)
    editor.text = new Delta(JSON.parse(props.defaultText))
  else
    refEditor.value.setContents('','user')
  editor.title = props.defaultTitle
  editor.type = props.defaultType
}

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)
const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike","clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        { header: [1, 2, 3, 4, 5, 6, false] },
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        { indent: '-1' }, { indent: '+1' }
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: [ 'Resize', 'DisplaySize' ]
    },
    ImageExtend: {
      action:  axios.defaults.baseURL + '/api/images/annex',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if(resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.uploading = false
      }
    }
  }
}
function submitTopic(){
  const text = deltaToText(editor.text)
  if(text.length > 20000){
    ElMessage.warning("内容过多")
    return
  }
  if(!editor.type) {
    ElMessage.warning("您还没有选择类型")
    return;
  }
  if(!editor.title){
    ElMessage.warning("您还没有编写标题")
    return;
  }
  if(text.length === 0) {
    ElMessage.warning("您还没有输入内容")
    return;
  }
  props.submit(editor,()=>emit("created"))
}

function deltaToText(delta){
  if (!delta.ops) return ''
  let str = ""
  for(let op of delta.ops)
    str+=op.insert
  return str.replace(/\s/g,"")
}
const deltaLength = computed(() => deltaToText(editor.text).length)
</script>

<template>
  <div>
    <el-drawer :model-value="show" direction="btt" :size="625" :close-on-click-modal="false"
               @close="emit('close')" @open="initEditor">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 12px;color: grey">
            发表帖子时，请严格遵守国家相关法律条约，若有违反则封禁处理。
            同时为了营造良好的环境，请勿辱骂他人或传播虚假消息。
          </div>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 150px">
          <el-select placeholder="请选择帖子类型" v-model="editor.type" :disabled="!store.forum.types.length" value-key="id">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color"/>
                <span style="margin-left: 10px">{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input placeholder="请输入帖子标题" :prefix-icon="Postcard" v-model="editor.title" maxlength="30"/>
        </div>
      </div>
      <div style="font-size: 13px;margin-top: 5px;color: grey">
        <color-dot :color="editor.type ? editor.type.color : '#C3C3C3 '"/>
        <span style="margin-left: 6px">{{editor.type ? editor.type.desc : "帖子也是需要标签的，不要忘了选择帖子类型哟"}}</span>
      </div>
      <div style="margin-top: 10px;height: 422px;overflow: hidden;border-radius: 5px"
           v-loading="editor.uploading" element-loading-text="正在上传图片，请稍后......">
        <quill-editor placeholder="今日份新鲜事就由你来分享啦！"
                      v-model:content="editor.text" style="height: calc(100% - 45px)"
                      :options="editorOption" content-type="delta"
                      ref="refEditor"
        />
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 10px">
        <div style="font-size: 14px;color: grey;translate: 0 6px">
          当前字数{{deltaLength}}（最大支持20000个字）
        </div>
        <div>
          <el-button :icon="Share" @click="submitTopic" type="primary">{{props.submitButton}}</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer){
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}
:deep(.el-drawer__header){
  margin: 0;
}
</style>