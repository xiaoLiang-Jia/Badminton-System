<template>
  <el-container class="main-layout">
    <el-header class="header">
      <div class="header-left">
        <router-link to="/home" class="logo">
          <h1>羽毛球平台</h1>
        </router-link>
        <el-menu
          mode="horizontal"
          :router="true"
          :default-active="route.path"
          :ellipsis="false"
          class="nav-menu"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/courts">场地预约</el-menu-item>
          <el-menu-item index="/courses">课程培训</el-menu-item>
          <el-menu-item index="/forum">社区论坛</el-menu-item>
        </el-menu>
      </div>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
            <el-button :icon="Message" circle @click="$router.push('/messages')" />
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar || '/avatar/default.png'" />
              <span class="username">{{ userStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="messages">消息</el-dropdown-item>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isCoach" command="students">我的学员</el-dropdown-item>
                <el-dropdown-item v-if="!userStore.isCoach" command="bookings">我的预约</el-dropdown-item>
                <el-dropdown-item command="courses">我的课程</el-dropdown-item>
                <el-dropdown-item command="balance">余额充值</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="admin">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Message } from '@element-plus/icons-vue'
import { getUnreadCount } from '@/api/message'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  if (userStore.token && !userStore.user) {
    userStore.fetchUserInfo()
  }
  if (userStore.isLoggedIn) {
    loadUnreadCount()
  }
})

const handleCommand = (command) => {
  switch (command) {
    case 'messages':
      router.push('/messages')
      break
    case 'profile':
      router.push('/user/profile')
      break
    case 'bookings':
      router.push('/user/bookings')
      break
    case 'students':
      router.push('/user/students')
      break
    case 'courses':
      router.push('/user/courses')
      break
    case 'balance':
      router.push('/user/balance')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.logo {
  text-decoration: none;
  color: #409eff;
  margin-right: 20px;
}

.logo h1 {
  font-size: 20px;
  margin: 0;
}

.nav-menu {
  border-bottom: none;
  flex-wrap: wrap;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-badge {
  margin-right: 10px;
}

.message-badge .el-badge__content {
  background-color: #f56c6c;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 20px;
}
</style>
