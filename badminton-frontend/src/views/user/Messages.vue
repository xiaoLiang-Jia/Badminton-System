<template>
  <div class="messages-container">
    <div class="header">
      <h2>消息</h2>
    </div>

    <div class="conversation-list" v-if="conversations.length > 0">
      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="conversation-item"
        @click="goToChat(conv.targetId)"
      >
        <el-avatar
          :size="50"
          :src="conv.targetAvatar || '/avatar/default.png'"
        />
        <div class="conversation-info">
          <div class="conversation-header">
            <span class="username">{{ conv.targetRealName || conv.targetUsername }}</span>
            <span class="time">{{ formatTime(conv.lastTime) }}</span>
          </div>
          <div class="last-message">{{ conv.lastMessage }}</div>
        </div>
        <el-badge
          v-if="conv.unreadCount > 0"
          :value="conv.unreadCount"
          class="unread-badge"
        />
      </div>
    </div>

    <el-empty v-else description="暂无消息" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '@/api/message'

const router = useRouter()
const conversations = ref([])

const loadConversations = async () => {
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const goToChat = (userId) => {
  router.push(`/chat/${userId}`)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return `${date.getMonth() + 1}-${date.getDate()}`
  }
}

onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.messages-container {
  max-width: 800px;
  margin: 0 auto;
}

.header {
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.conversation-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  transition: background 0.3s;
  border-bottom: 1px solid #f0f0f0;
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background: #f5f7fa;
}

.conversation-info {
  flex: 1;
  margin-left: 15px;
  overflow: hidden;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.username {
  font-weight: bold;
  font-size: 15px;
}

.time {
  color: #999;
  font-size: 12px;
}

.last-message {
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.unread-badge {
  margin-left: 10px;
}
</style>