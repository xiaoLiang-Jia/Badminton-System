<template>
  <div class="courts-page">
    <div class="filter-bar">
      <el-select v-model="filters.type" placeholder="场地类型" clearable @change="loadCourts">
        <el-option label="室内木地" :value="1" />
        <el-option label="室内地胶" :value="2" />
        <el-option label="室外" :value="3" />
      </el-select>
      <el-select v-model="filters.status" placeholder="场地状态" clearable @change="loadCourts">
        <el-option label="可用" :value="1" />
        <el-option label="不可用" :value="0" />
      </el-select>
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
            <p class="description">{{ court.description }}</p>
            <div class="court-meta">
              <span class="price">¥{{ court.price }}/小时</span>
              <div>
                <el-tag :type="court.type === 1 ? 'success' : court.type === 2 ? 'warning' : 'info'">
                  {{ ['室内木地', '室内地胶', '室外'][court.type - 1] }}
                </el-tag>
                <el-tag :type="court.status === 1 ? 'success' : 'danger'" style="margin-left: 5px">
                  {{ court.status === 1 ? '可用' : '不可用' }}
                </el-tag>
              </div>
            </div>
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
      @current-change="loadCourts"
      style="margin-top: 20px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCourtList } from '@/api/court'

const courts = ref([])
const total = ref(0)

const filters = reactive({
  pageNum: 1,
  pageSize: 9,
  type: null,
  status: 1
})

const loadCourts = async (page = 1) => {
  filters.pageNum = page
  try {
    const res = await getCourtList(filters)
    courts.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCourts()
})
</script>

<style scoped>
.courts-page {
  padding: 20px 0;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.court-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.court-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.court-image {
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
}

.court-info h3 {
  margin: 10px 0 5px;
}

.location {
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

.court-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>
