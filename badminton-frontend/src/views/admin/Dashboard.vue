<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="30"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <p class="label">总预约数</p>
              <p class="value">{{ stats.totalBookings }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="30"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <p class="label">待支付</p>
              <p class="value">{{ stats.pendingBookings }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="30"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <p class="label">已完成</p>
              <p class="value">{{ stats.completedBookings }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="30"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <p class="label">今日预约</p>
              <p class="value">{{ stats.todayBookings }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Calendar, Clock, CircleCheck, Tickets } from '@element-plus/icons-vue'

const stats = reactive({
  totalBookings: 0,
  pendingBookings: 0,
  completedBookings: 0,
  todayBookings: 0
})

const loadStats = async () => {
  // 模拟数据，实际需要调用API
  stats.totalBookings = 156
  stats.pendingBookings = 12
  stats.completedBookings = 98
  stats.todayBookings = 23
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info .label {
  color: #999;
  font-size: 14px;
  margin-bottom: 5px;
}

.stat-info .value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}
</style>
