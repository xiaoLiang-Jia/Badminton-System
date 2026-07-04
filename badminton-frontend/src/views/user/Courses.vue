<template>
  <div class="courses-page">
    <div class="filter-bar">
      <el-select v-model="filters.level" placeholder="难度等级" clearable @change="loadCourses">
        <el-option label="初级" :value="1" />
        <el-option label="中级" :value="2" />
        <el-option label="高级" :value="3" />
      </el-select>
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
            <p class="description">{{ course.description }}</p>
            <div class="course-meta">
              <span class="price">¥{{ course.price }}</span>
              <el-tag :type="course.level === 1 ? 'success' : course.level === 2 ? 'warning' : 'danger'">
                {{ ['初级', '中级', '高级'][course.level - 1] }}
              </el-tag>
            </div>
            <p class="meta-info">
              <span v-if="course.lessonCount">课时：{{ course.lessonCount }}次</span>
              <span v-if="course.frequency"> | {{ course.frequency }}</span>
            </p>
            <p class="students">{{ course.currentNum }}/{{ course.maxStudents }}人已报名</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-if="total > 0"
      :current-page="filters.pageNum"
      :page-size="filters.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadCourses"
      style="margin-top: 20px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCourseList } from '@/api/course'

const courses = ref([])
const total = ref(0)

const filters = reactive({
  pageNum: 1,
  pageSize: 9,
  level: null,
  status: 1
})

const loadCourses = async (page = 1) => {
  filters.pageNum = page
  try {
    const res = await getCourseList(filters)
    courses.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.courses-page {
  padding: 20px 0;
}

.filter-bar {
  margin-bottom: 20px;
}

.course-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.course-image {
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
}

.course-info h3 {
  margin: 10px 0 5px;
}

.coach {
  color: #999;
  font-size: 14px;
  margin-bottom: 5px;
}

.description {
  color: #666;
  font-size: 13px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.students {
  color: #999;
  font-size: 13px;
  margin-top: 5px;
}

.meta-info {
  color: #409eff;
  font-size: 13px;
  margin-top: 5px;
}
</style>
