<script setup>

import {useDark, useToggle} from "@vueuse/core";
import {onMounted, provide, ref} from "vue";
import {isUnauthorized} from "@/net";
import {getUserInfo} from "@/net/api/user";

useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: "dark",
  valueLight: 'light'
})
useDark({
  onChanged(dark){
    useToggle(dark)
  }
})

const loading = ref(false)
provide('userLoading',loading)

onMounted(() => {
    if (!isUnauthorized()){
        getUserInfo(loading)
    }
})
</script>

<template>
  <div>
    <router-view/>
  </div>
</template>

<style scoped>

</style>
