<template>
  <div class="home">
    <div class="banner">
      <el-carousel height="400px">
        <el-carousel-item>
          <div class="banner-content banner-1">
            <h2>羽毛球平台</h2>
            <p>专业场地预约 | 精品课程培训 | 活跃社区交流</p>
          </div>
        </el-carousel-item>
        <el-carousel-item>
          <div class="banner-content banner-2">
            <h2>场地预约</h2>
            <p>在线预约场地，轻松享受运动</p>
          </div>
        </el-carousel-item>
        <el-carousel-item>
          <div class="banner-content banner-3">
            <h2>专业课程</h2>
            <p>资深教练指导，提升球技</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="quick-entry">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="entry-card" @click="$router.push('/courts')">
            <el-icon :size="40"><Calendar /></el-icon>
            <h3>场地预约</h3>
            <p>在线预订羽毛球场</p>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="entry-card" @click="$router.push('/courses')">
            <el-icon :size="40"><Reading /></el-icon>
            <h3>课程培训</h3>
            <p>专业教练指导</p>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="entry-card" @click="$router.push('/forum')">
            <el-icon :size="40"><ChatDotRound /></el-icon>
            <h3>社区论坛</h3>
            <p>交流球友心得</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <div class="section-header">
        <h2>热门场地</h2>
        <el-link type="primary" @click="$router.push('/courts')">查看全部</el-link>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="court in courts" :key="court.id">
          <el-card class="court-card" @click="$router.push(`/courts/${court.id}`)">
            <div class="court-image">
              <el-image :src="court.imageUrl || '/images/court-default.jpg'" fit="cover" />
            </div>
            <div class="court-info">
              <h3>{{ court.name }}</h3>
              <p class="location">{{ court.location }}</p>
              <div class="court-meta">
                <span class="price">¥{{ court.price }}/小时</span>
                <el-tag :type="court.status === 1 ? 'success' : 'danger'">
                  {{ court.status === 1 ? '可用' : '不可用' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <div class="section-header">
        <h2>热门课程</h2>
        <el-link type="primary" @click="$router.push('/courses')">查看全部</el-link>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="course in courses" :key="course.id">
          <el-card class="course-card" @click="$router.push(`/courses/${course.id}`)">
            <div class="course-image">
              <el-image :src="course.imageUrl || '/images/course-default.jpg'" fit="cover" />
            </div>
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p class="coach">教练：{{ course.coach?.realName || course.coach?.username }}</p>
              <div class="course-meta">
                <span class="price">¥{{ course.price }}</span>
                <span class="students">{{ course.currentNum }}/{{ course.maxStudents }}人</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCourtList } from '@/api/court'
import { getCourseList } from '@/api/course'
import { Calendar, Reading, ChatDotRound } from '@element-plus/icons-vue'

const courts = ref([])
const courses = ref([])

const loadData = async () => {
  try {
    const courtRes = await getCourtList({ pageNum: 1, pageSize: 3, status: 1 })
    courts.value = courtRes.data.records

    const courseRes = await getCourseList({ pageNum: 1, pageSize: 3, status: 1 })
    courses.value = courseRes.data.records
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.banner {
  margin-bottom: 30px;
}

.banner-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
}

.banner-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.banner-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.banner-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }

.banner-content h2 {
  font-size: 48px;
  margin-bottom: 10px;
}

.banner-content p {
  font-size: 20px;
}

.quick-entry {
  margin-bottom: 40px;
}

.entry-card {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.entry-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.entry-card h3 {
  margin: 15px 0 5px;
}

.entry-card p {
  color: #999;
}

.section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
}

.court-card,
.course-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.court-card:hover,
.course-card:hover {
  transform: translateY(-5px);
}

.court-image,
.course-image {
  height: 150px;
  overflow: hidden;
  border-radius: 4px;
}

.court-info h3,
.course-info h3 {
  margin: 10px 0 5px;
  font-size: 16px;
}

.location,
.coach {
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.court-meta,
.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.students {
  color: #999;
  font-size: 14px;
}
</style>
