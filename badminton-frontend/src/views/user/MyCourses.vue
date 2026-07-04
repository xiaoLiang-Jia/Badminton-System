<template>
  <div class="my-courses">
    <h2>我的课程</h2>

    <div class="course-list">
      <el-card v-for="course in courses" :key="course.id" class="course-item">
        <div class="course-header">
          <el-image :src="course.imageUrl || '/images/course-default.jpg'" fit="cover" class="course-image" />
          <div class="course-info">
            <h3>{{ course.name }}</h3>
            <p>教练：{{ course.coach?.realName || course.coach?.username }}</p>
            <p>课程安排：{{ course.schedule }}</p>
            <el-button type="danger" size="small" @click="handleCancel(course.id)">取消报名</el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="courses.length === 0" description="暂无课程" />
    </div>

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
import { getMyCourses, cancelCourse } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'

const courses = ref([])
const total = ref(0)

const filters = reactive({
  pageNum: 1,
  pageSize: 10
})

const loadCourses = async (page = 1) => {
  filters.pageNum = page
  try {
    const res = await getMyCourses(filters)
    courses.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该课程报名吗？', '提示', {
      type: 'warning'
    })
    await cancelCourse(id)
    ElMessage.success('取消成功')
    loadCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.my-courses {
  padding: 20px 0;
}

.my-courses h2 {
  margin-bottom: 20px;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.course-item {
  margin-bottom: 10px;
}

.course-header {
  display: flex;
  gap: 15px;
}

.course-image {
  width: 200px;
  height: 120px;
  border-radius: 4px;
}

.course-info h3 {
  margin: 0 0 10px 0;
}

.course-info p {
  margin: 5px 0;
  color: #666;
}
</style>
