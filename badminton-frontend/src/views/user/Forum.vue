<template>
  <div class="forum-page">
    <div class="forum-header">
      <h2>社区论坛</h2>
      <el-button type="primary" @click="handlePublish" v-if="userStore.isLoggedIn">发布帖子</el-button>
    </div>

    <div class="filter-bar">
      <el-radio-group v-model="filters.category" @change="loadForums">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="1">技术讨论</el-radio-button>
        <el-radio-button :label="2">约球</el-radio-button>
        <el-radio-button :label="3">比赛心得</el-radio-button>
        <el-radio-button :label="4">器材交流</el-radio-button>
        <el-radio-button :label="5">新手求助</el-radio-button>
      </el-radio-group>
    </div>

    <div class="forum-list">
      <el-card v-for="forum in forums" :key="forum.id" class="forum-item" @click="$router.push(`/forum/${forum.id}`)">
        <div class="forum-item-header">
          <h3>{{ forum.title }}</h3>
          <el-tag size="small" :type="['success', 'warning', 'danger', 'info', ''][forum.category - 1]">
            {{ ['技术讨论', '约球', '比赛心得', '器材交流', '新手求助'][forum.category - 1] }}
          </el-tag>
        </div>
        <p class="forum-content">{{ forum.content }}</p>
        <div class="forum-meta">
          <span class="author">{{ forum.user?.username }}</span>
          <span class="views"><el-icon><View /></el-icon> {{ forum.views }}</span>
          <span class="likes"><el-icon><Star /></el-icon> {{ forum.likes }}</span>
          <span class="comments"><el-icon><ChatDotRound /></el-icon> {{ forum.commentCount }}</span>
          <span class="time">{{ formatTime(forum.createTime) }}</span>
        </div>
      </el-card>
    </div>

    <el-pagination
      v-if="total > 0"
      :current-page="filters.pageNum"
      :page-size="filters.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadForums"
      style="margin-top: 20px; justify-content: center"
    />

    <!-- 发布帖子对话框 -->
    <el-dialog v-model="publishDialogVisible" title="发布帖子" width="600px">
      <el-form :model="publishForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="publishForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="publishForm.category" placeholder="选择分类">
            <el-option label="技术讨论" :value="1" />
            <el-option label="约球" :value="2" />
            <el-option label="比赛心得" :value="3" />
            <el-option label="器材交流" :value="4" />
            <el-option label="新手求助" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="publishForm.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublishSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getForumList, publishForum } from '@/api/forum'
import { ElMessage } from 'element-plus'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const forums = ref([])
const total = ref(0)
const publishDialogVisible = ref(false)

const filters = reactive({
  pageNum: 1,
  pageSize: 10,
  category: null,
  status: 1
})

const publishForm = reactive({
  title: '',
  category: 1,
  content: ''
})

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const loadForums = async (page = 1) => {
  filters.pageNum = page
  try {
    const res = await getForumList(filters)
    forums.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handlePublish = () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  publishDialogVisible.value = true
}

const handlePublishSubmit = async () => {
  if (!publishForm.title || !publishForm.content) {
    ElMessage.warning('请填写完整的帖子信息')
    return
  }

  try {
    await publishForum(publishForm)
    ElMessage.success('发布成功')
    publishDialogVisible.value = false
    publishForm.title = ''
    publishForm.content = ''
    loadForums()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadForums()
})
</script>

<style scoped>
.forum-page {
  padding: 20px 0;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forum-header h2 {
  margin: 0;
}

.filter-bar {
  margin-bottom: 20px;
}

.forum-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.forum-item {
  cursor: pointer;
  transition: all 0.3s;
}

.forum-item:hover {
  transform: translateX(5px);
}

.forum-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.forum-item-header h3 {
  margin: 0;
}

.forum-content {
  color: #666;
  margin-bottom: 10px;
}

.forum-meta {
  display: flex;
  gap: 15px;
  color: #999;
  font-size: 13px;
}

.forum-meta .el-icon {
  margin-right: 3px;
}

.forum-meta .author {
  font-weight: bold;
}
</style>
