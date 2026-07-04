<template>
  <div class="admin-courses">
    <div class="header">
      <h2>课程管理</h2>
      <el-button type="primary" @click="handleAdd">添加课程</el-button>
    </div>

    <el-table :data="courses" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="课程名称" />
      <el-table-column prop="coach" label="教练">
        <template #default="{ row }">
          {{ row.coach?.realName || row.coach?.username }}
        </template>
      </el-table-column>
      <el-table-column prop="level" label="等级">
        <template #default="{ row }">
          <el-tag :type="row.level === 1 ? 'success' : row.level === 2 ? 'warning' : 'danger'">
            {{ ['初级', '中级', '高级'][row.level - 1] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lessonCount" label="课时数">
        <template #default="{ row }">
          {{ row.lessonCount || '-' }}次
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="currentNum" label="已报名">
        <template #default="{ row }">
          {{ row.currentNum }}/{{ row.maxStudents }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '可报名' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '添加课程'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="form.coachId" placeholder="选择教练">
            <el-option v-for="coach in coaches" :key="coach.id" :label="coach.realName || coach.username" :value="coach.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="场地" prop="courtId">
          <el-select v-model="form.courtId" placeholder="选择场地">
            <el-option v-for="court in courts" :key="court.id" :label="court.name" :value="court.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="level">
          <el-select v-model="form.level">
            <el-option label="初级" :value="1" />
            <el-option label="中级" :value="2" />
            <el-option label="高级" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="课时数" prop="lessonCount">
          <el-input-number v-model="form.lessonCount" :min="1" />
        </el-form-item>
        <el-form-item label="训练频率" prop="frequency">
          <el-input v-model="form.frequency" placeholder="如：每周2次，共4周" />
        </el-form-item>
        <el-form-item label="课程价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="form.maxStudents" :min="1" />
        </el-form-item>
        <el-form-item label="课时时长" prop="duration">
          <el-input-number v-model="form.duration" :min="30" :step="30" />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>
        <el-form-item label="课程安排" prop="schedule">
          <el-input v-model="form.schedule" placeholder="如：每周二、四 19:00-20:30" />
        </el-form-item>
        <el-form-item label="适合人群" prop="targetAudience">
          <el-input v-model="form.targetAudience" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="训练重点" prop="trainingFocus">
          <el-input v-model="form.trainingFocus" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="结业标准" prop="graduationStandard">
          <el-input v-model="form.graduationStandard" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="课程大纲" prop="curriculum">
          <el-input v-model="form.curriculum" type="textarea" :rows="6" placeholder="JSON格式，如：[{'lesson':1,'title':'内容','content':'描述'}]" />
        </el-form-item>
        <el-form-item label="课程图片" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCourseList, addCourse, updateCourse, deleteCourse } from '@/api/course'
import { getUserList } from '@/api/user'
import { getCourtList } from '@/api/court'
import { ElMessage, ElMessageBox } from 'element-plus'

const courses = ref([])
const coaches = ref([])
const courts = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  coachId: null,
  courtId: null,
  level: 1,
  lessonCount: 8,
  frequency: '',
  price: 0,
  maxStudents: 10,
  duration: 60,
  schedule: '',
  targetAudience: '',
  trainingFocus: '',
  graduationStandard: '',
  curriculum: '',
  imageUrl: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  courtId: [{ required: true, message: '请选择场地', trigger: 'change' }],
  level: [{ required: true, message: '请选择难度等级', trigger: 'change' }],
  price: [{ required: true, message: '请输入课程价格', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
}

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await getCourseList({ pageNum: 1, pageSize: 100 })
    courses.value = res.data.records
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCoaches = async () => {
  try {
    const res = await getUserList({ role: 2, pageNum: 1, pageSize: 100 })
    coaches.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const loadCourts = async () => {
  try {
    const res = await getCourtList({ pageNum: 1, pageSize: 100 })
    courts.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.coachId = null
  form.courtId = null
  form.level = 1
  form.lessonCount = 8
  form.frequency = ''
  form.price = 0
  form.maxStudents = 10
  form.duration = 60
  form.schedule = ''
  form.targetAudience = ''
  form.trainingFocus = ''
  form.graduationStandard = ''
  form.curriculum = ''
  form.imageUrl = ''
  form.description = ''
  form.status = 1
}

const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateCourse(form)
      ElMessage.success('更新成功')
    } else {
      await addCourse(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCourses()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？', '提示', { type: 'warning' })
    await deleteCourse(id)
    ElMessage.success('删除成功')
    loadCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadCourses()
  loadCoaches()
  loadCourts()
})
</script>

<style scoped>
.admin-courses {
  padding: 20px 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}
</style>
