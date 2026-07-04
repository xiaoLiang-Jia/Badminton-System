<template>
  <div class="admin-courts">
    <div class="header">
      <h2>场地管理</h2>
      <el-button type="primary" @click="showDialog = true">添加场地</el-button>
    </div>

    <el-table :data="courts" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="场地名称" />
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="type" label="类型">
        <template #default="{ row }">
          {{ ['室内木地', '室内地胶', '室外'][row.type - 1] }}
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格">
        <template #default="{ row }">
          ¥{{ row.price }}/小时
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '可用' : '不可用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="editForm.id ? '编辑场地' : '添加场地'" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="editForm.location" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.type">
            <el-option label="室内木地" :value="1" />
            <el-option label="室内地胶" :value="2" />
            <el-option label="室外" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="editForm.price" :min="0" />
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="editForm.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCourtList, addCourt, updateCourt, deleteCourt } from '@/api/court'
import { ElMessage, ElMessageBox } from 'element-plus'

const courts = ref([])
const showDialog = ref(false)

const editForm = reactive({
  id: null,
  name: '',
  location: '',
  type: 1,
  price: 30,
  capacity: 4,
  status: 1,
  description: ''
})

const loadCourts = async () => {
  try {
    const res = await getCourtList({ pageNum: 1, pageSize: 100 })
    courts.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const handleEdit = (row) => {
  Object.assign(editForm, row)
  showDialog.value = true
}

const handleSubmit = async () => {
  try {
    if (editForm.id) {
      await updateCourt(editForm)
      ElMessage.success('更新成功')
    } else {
      await addCourt(editForm)
      ElMessage.success('添加成功')
    }
    showDialog.value = false
    loadCourts()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该场地吗？', '提示', { type: 'warning' })
    await deleteCourt(id)
    ElMessage.success('删除成功')
    loadCourts()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => {
  loadCourts()
})
</script>

<style scoped>
.admin-courts {
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
