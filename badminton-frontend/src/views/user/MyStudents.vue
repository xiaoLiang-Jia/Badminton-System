<template>
  <div class="my-students">
    <h2>我的学员</h2>

    <div class="student-list">
      <el-card v-for="item in students" :key="item.id" class="student-item">
        <div class="student-info">
          <el-avatar :size="60" :src="item.user?.avatar || '/avatar/default.png'" />
          <div class="student-details">
            <h3>{{ item.user?.realName || item.user?.username || '未知用户' }}</h3>
            <p>课程：{{ item.course?.name }}</p>
            <p>手机号：{{ item.user?.phone || '未填写' }}</p>
            <p>邮箱：{{ item.user?.email || '未填写' }}</p>
            <p>报名时间：{{ formatTime(item.createTime) }}</p>
            <el-tag :type="item.status === 1 ? 'success' : 'info'">
              {{ item.status === 1 ? '学习中' : '已退出' }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-empty v-if="students.length === 0" description="暂无学员" />
    </div>

    <el-pagination
      v-if="total > 0"
      :current-page="filters.pageNum"
      :page-size="filters.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadStudents"
      style="margin-top: 20px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCoachStudents } from '@/api/course'

const students = ref([])
const total = ref(0)

const filters = reactive({
  pageNum: 1,
  pageSize: 10
})

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const loadStudents = async (page = 1) => {
  filters.pageNum = page
  try {
    const res = await getCoachStudents(filters)
    students.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
.my-students {
  padding: 20px 0;
}

.my-students h2 {
  margin-bottom: 20px;
}

.student-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.student-item {
  cursor: pointer;
  transition: all 0.3s;
}

.student-item:hover {
  transform: translateX(5px);
}

.student-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.student-details {
  flex: 1;
}

.student-details h3 {
  margin: 0 0 10px;
}

.student-details p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}
</style>