<script setup>
import { useStore } from "@/store";

const user = useStore()
import { ref, computed } from 'vue';

// 获取主题
const theme = computed(() => {
  return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
});
</script>

<template>
  <div :class="['welcome-container', theme]">
    <div class="welcome-card">
      <div class="avatar">
        <el-avatar :size="40" :src="user.avatarUserUrl(user.user.avatar)" />
      </div>
      <h1>欢迎回来，{{ user.user.username }}!</h1>
      <p class="description">这是您的管理员控制面板</p>
    </div>
  </div>
</template>

<style lang="less" scoped>
/* 浅色模式和深色模式适配 */
.welcome-container {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Arial', sans-serif;
  overflow: hidden;
  transition: background 0.3s ease;
}

.welcome-container.light {
  background: #f9f9f9;
  color: #333;
}

.welcome-container.dark {
  background: #121212;
  color: #fff;
}

.welcome-card {
  background: rgba(154, 154, 154, 0.7);
  padding: 40px;
  border-radius: 20px;
  width: 350px;
  text-align: center;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  animation: fadeIn 1.2s ease-out;
  transition: background 0.3s ease, color 0.3s ease;
}

/* 浅色模式 */
.welcome-container.light .welcome-card {
  background: rgba(255, 255, 255, 0.7);
}

.welcome-container.light .btn {
  background-color: #007bff;
}

.welcome-container.light .secondary {
  background-color: #28a745;
}

/* 深色模式 */
.welcome-container.dark .welcome-card {
  background: rgba(0, 0, 0, 0.7);
}

.welcome-container.dark .btn {
  background-color: #1e90ff;
}

.welcome-container.dark .secondary {
  background-color: #32cd32;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.avatar img {
  border-radius: 50%;
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
  border: 4px solid #fff;
  animation: avatarBounce 1s ease-in-out infinite alternate;
}

@keyframes avatarBounce {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(-10px);
  }
}

h1 {
  font-size: 2.2rem;
  margin-bottom: 10px;
  font-weight: 600;
}

.description {
  font-size: 1.1rem;
  margin-bottom: 30px;
  font-weight: 400;
}

.action-btns {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.secondary {
  border-radius: 30px;
}

.secondary:hover {
  transform: translateY(-3px);
}
</style>
