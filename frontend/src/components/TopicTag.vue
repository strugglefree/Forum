<script setup>
import {useStore} from "@/store";

const store = useStore()

defineProps({
  type: Number
})
</script>

<template>
  <div class="topic-type" :style="{
              'background': store.findTypeById(type)?.color
            }">
    {{store.findTypeById(type)?.name}}
  </div>
</template>

<style scoped>
.topic-type{
  display: inline-block;
  font-size: 12px;
  border: solid 0.5px var(--el-border-color);
  border-radius: 3px;
  padding: 0 5px;
  height: 18px;
  color: var(--el-text-color);
  font-weight: bold;
  align-content: center;
}
</style>