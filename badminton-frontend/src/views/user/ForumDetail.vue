<template>
  <div class="forum-detail">
    <el-button @click="$router.back()" style="margin-bottom: 20px">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-card v-if="forum">
      <div class="forum-header">
        <h1>{{ forum.title }}</h1>
        <div class="forum-meta">
          <el-avatar :size="32" :src="forum.user?.avatar" />
          <span class="author">{{ forum.user?.username }}</span>
          <span class="time">{{ formatTime(forum.createTime) }}</span>
          <el-tag :type="['success', 'warning', 'danger', 'info', ''][forum.category - 1]">
            {{ ['技术讨论', '约球', '比赛心得', '器材交流', '新手求助'][forum.category - 1] }}
          </el-tag>
        </div>
      </div>

      <el-divider />

      <div class="forum-content">
        <p>{{ forum.content }}</p>
      </div>

      <div class="forum-actions">
        <el-button :type="forum.liked ? 'primary' : 'default'" @click="handleLike">
          <el-icon><Star /></el-icon> {{ forum.likes }} 点赞
        </el-button>
        <el-button>
          <el-icon><ChatDotRound /></el-icon> {{ comments.length }} 评论
        </el-button>
      </div>

      <el-divider />

      <!-- 评论区域 -->
      <div class="comments-section">
        <h3>评论</h3>
        <div v-if="userStore.isLoggedIn" class="comment-form">
          <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="写下你的评论..." />
          <el-button type="primary" @click="handleComment" style="margin-top: 10px">发表评论</el-button>
        </div>

        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <el-avatar :size="28" :src="comment.user?.avatar" />
              <span class="author">{{ comment.user?.username }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getForumById, getForumComments, likeForum, unlikeForum, addComment } from '@/api/forum'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star, ChatDotRound } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const forum = ref(null)
const comments = ref([])
const commentContent = ref('')

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const loadForum = async () => {
  try {
    const res = await getForumById(route.params.id)
    forum.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadComments = async () => {
  try {
    const res = await getForumComments(route.params.id)
    comments.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    if (forum.value.liked) {
      await unlikeForum(forum.value.id)
    } else {
      await likeForum(forum.value.id)
    }
    loadForum()
  } catch (error) {
    console.error(error)
  }
}

const handleComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await addComment({
      forumId: forum.value.id,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadForum()
  loadComments()
})
</script>

<style scoped>
.forum-detail {
  padding: 20px 0;
}

.forum-header h1 {
  margin-bottom: 15px;
}

.forum-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.forum-meta .author {
  font-weight: bold;
}

.forum-meta .time {
  color: #999;
}

.forum-content {
  padding: 20px 0;
  line-height: 1.8;
  font-size: 15px;
}

.forum-actions {
  display: flex;
  gap: 10px;
}

.comments-section h3 {
  margin-bottom: 15px;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-header .author {
  font-weight: bold;
}

.comment-header .time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  margin: 0;
  line-height: 1.6;
}
</style>
