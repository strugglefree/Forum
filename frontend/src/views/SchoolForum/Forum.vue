<script setup>
import {useStore} from "@/store";
import {ElMessage} from "element-plus";
import {onMounted} from "vue";
import {apiForumGet} from "@/net/api/forum";

const store = useStore()
apiForumGet((data)=>{
  const array = [];
  array.push({name:"全部",id:0,color:'linear-gradient(45deg,white,red,orange,gold,green,blue)'})
  data.forEach((item)=>{array.push(item)})
  store.forum.types = array
},message => ElMessage.error(message))

onMounted(() => {

})
</script>

<template>
  <div>
    <router-view v-slot="{Component}">
      <transition name="el-fade-in-linear" mode="out-in">
        <keep-alive include="PostList">
          <component :is="Component"></component>
        </keep-alive>
      </transition>
    </router-view>
    <el-backtop target=".main-content-page .el-scrollbar__wrap" :right="20" :bottom="70"></el-backtop>
  </div>
</template>

<style scoped>

</style>