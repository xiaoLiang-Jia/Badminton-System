<template>
  <div class="course-detail">
    <el-button @click="$router.back()" style="margin-bottom: 20px">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-card v-if="course">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-image :src="course.imageUrl || '/images/course-default.jpg'" fit="cover" style="width: 100%" />
        </el-col>
        <el-col :span="12">
          <h2>{{ course.name }}</h2>
          <div class="tags">
            <el-tag :type="course.level === 1 ? 'success' : course.level === 2 ? 'warning' : 'danger'">
              {{ ['初级', '中级', '高级'][course.level - 1] }}
            </el-tag>
            <el-tag v-if="course.lessonCount" type="info">共{{ course.lessonCount }}次课</el-tag>
          </div>
          <p class="coach">教练：{{ course.coach?.realName || course.coach?.username }}</p>
          <p class="price">¥{{ course.price }}</p>
          <p class="duration">课时时长：{{ course.duration }}分钟/次</p>
          <p v-if="course.frequency" class="frequency">训练频率：{{ course.frequency }}</p>
          <p class="schedule">课程安排：{{ course.schedule }}</p>
          <p class="students">已报名：{{ course.currentNum }}/{{ course.maxStudents }}人</p>
          <p class="description">{{ course.description }}</p>

          <el-divider />

          <el-button type="primary" size="large" :disabled="course.currentNum >= course.maxStudents" @click="handleEnroll">
            {{ course.currentNum >= course.maxStudents ? '已满员' : '立即报名' }}
          </el-button>
          <el-button type="success" size="large" @click="handleChat" v-if="userStore.isLoggedIn && course.coachId">
            联系教练
          </el-button>
        </el-col>
      </el-row>

      <!-- 课程详细信息 -->
      <el-divider />

      <div class="course-detail-info">
        <el-row :gutter="20">
          <el-col :span="12" v-if="course.targetAudience">
            <h4><el-icon><User /></el-icon> 适合人群</h4>
            <p>{{ course.targetAudience }}</p>
          </el-col>
          <el-col :span="12" v-if="course.trainingFocus">
            <h4><el-icon><Aim /></el-icon> 训练重点</h4>
            <p>{{ course.trainingFocus }}</p>
          </el-col>
        </el-row>

        <el-row v-if="course.graduationStandard">
          <el-col :span="24">
            <h4><el-icon><Medal /></el-icon> 结业标准</h4>
            <p>{{ course.graduationStandard }}</p>
          </el-col>
        </el-row>

        <!-- 课程大纲 -->
        <div v-if="course.curriculum" class="curriculum-section">
          <h4><el-icon><List /></el-icon> 课程大纲</h4>
          <el-timeline>
            <el-timeline-item
              v-for="item in parseCurriculum(course.curriculum)"
              :key="item.lesson"
              :timestamp="'第' + item.lesson + '课'"
              placement="top"
            >
              <el-card>
                <h5>{{ item.title }}</h5>
                <p>{{ item.content }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCourseById, enrollCourse } from '@/api/course'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Aim, Medal, List } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const course = ref(null)

const loadCourse = async () => {
  try {
    const res = await getCourseById(route.params.id)
    course.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const parseCurriculum = (curriculum) => {
  try {
    return JSON.parse(curriculum)
  } catch (e) {
    return []
  }
}

const handleEnroll = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  try {
    await enrollCourse(course.value.id)
    ElMessage.success('报名成功')
    router.push('/user/courses')
  } catch (error) {
    console.error(error)
  }
}

const handleChat = () => {
  if (course.value.coachId) {
    router.push(`/chat/${course.value.coachId}`)
  }
}

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
.course-detail {
  padding: 20px 0;
}

.course-detail h2 {
  margin-bottom: 10px;
}

.tags {
  margin-bottom: 15px;
}

.tags .el-tag {
  margin-right: 10px;
}

.coach, .duration, .frequency, .schedule, .students {
  margin-bottom: 10px;
  color: #666;
}

.frequency {
  color: #409eff;
}

.price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 15px;
}

.description {
  color: #666;
  line-height: 1.6;
}

.course-detail-info {
  padding: 20px;
}

.course-detail-info h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #303133;
}

.course-detail-info p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.curriculum-section {
  margin-top: 20px;
}

.curriculum-section h5 {
  margin: 0 0 5px;
  color: #303133;
}

.curriculum-section p {
  color: #606266;
  font-size: 14px;
  margin: 0;
}
</style>
