<template>
  <div class="chat-container">
    <div class="chat-header">
      <el-button :icon="ArrowLeft" circle @click="$router.back()" />
      <div class="chat-user-info">
        <el-avatar :size="40" :src="targetUser?.avatar || '/avatar/default.png'" />
        <span class="username">{{ targetUser?.realName || targetUser?.username }}</span>
      </div>
    </div>

    <div class="chat-messages" ref="messageContainer">
      <div
        v-for="msg in messages"
        :key="msg.id"
        :class="['message-item', msg.senderId === currentUserId ? 'message-sent' : 'message-received']"
      >
        <el-avatar
          v-if="msg.senderId !== currentUserId"
          :size="36"
          :src="msg.senderAvatar || '/avatar/default.png'"
        />
        <div class="message-content">
          <div class="message-bubble">
            {{ msg.content }}
          </div>
          <div class="message-time">{{ formatTime(msg.createTime) }}</div>
        </div>
        <el-avatar
          v-if="msg.senderId === currentUserId"
          :size="36"
          :src="currentUser?.avatar || '/avatar/default.png'"
        />
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
      >
        <template #append>
          <el-button :icon="Promotion" @click="sendMessage" />
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMessages, sendMessage as sendMsg, markAsRead } from '@/api/message'
import { getUserInfo } from '@/api/user'
import { ArrowLeft, Promotion } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const targetId = route.params.userId
const currentUserId = userStore.user?.id
const messages = ref([])
const inputMessage = ref('')
const targetUser = ref(null)
const currentUser = ref(userStore.user)
const messageContainer = ref(null)

let ws = null

const loadMessages = async () => {
  try {
    const res = await getMessages(targetId)
    messages.value = res.data || []
    await markAsRead(targetId)
    scrollToBottom()
  } catch (error) {
    console.error(error)
  }
}

const loadTargetUser = async () => {
  try {
    const res = await getUserInfo(targetId)
    targetUser.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  try {
    await sendMsg({
      receiverId: targetId,
      content: inputMessage.value.trim()
    })
    inputMessage.value = ''
    await loadMessages()
  } catch (error) {
    console.error(error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const scrollToBottom = async () => {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

const connectWebSocket = () => {
  const wsUrl = `ws://${window.location.host}/ws/chat`
  ws = new WebSocket(wsUrl)

  ws.onopen = () => {
    console.log('WebSocket connected')
  }

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'chat' && data.senderId === targetId) {
        loadMessages()
      }
    } catch (error) {
      console.error(error)
    }
  }

  ws.onerror = (error) => {
    console.error('WebSocket error', error)
  }

  ws.onclose = () => {
    console.log('WebSocket closed')
  }
}

onMounted(() => {
  loadTargetUser()
  loadMessages()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 15px;
}

.chat-user-info .username {
  font-weight: bold;
  font-size: 16px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.message-sent {
  justify-content: flex-end;
}

.message-received {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  margin: 0 10px;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 8px;
  word-wrap: break-word;
}

.message-sent .message-bubble {
  background: #409eff;
  color: #fff;
}

.message-received .message-bubble {
  background: #f0f0f0;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  text-align: right;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #e4e7ed;
}

.chat-input .el-input {
  width: 100%;
}
</style>