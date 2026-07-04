<template>
  <div class="my-bookings">
    <h2>我的预约</h2>

    <el-tabs v-model="activeTab" @tab-change="loadBookings">
      <el-tab-pane label="全部" :name="null" />
      <el-tab-pane label="待支付" :name="1" />
      <el-tab-pane label="已支付" :name="2" />
      <el-tab-pane label="已完成" :name="3" />
      <el-tab-pane label="已取消" :name="4" />
    </el-tabs>

    <div class="booking-list">
      <el-card v-for="booking in bookings" :key="booking.id" class="booking-item">
        <div class="booking-header">
          <span class="court-name">{{ booking.court?.name }}</span>
          <el-tag :type="getStatusType(booking.status)">
            {{ ['待支付', '已支付', '已完成', '已取消', '已退款'][booking.status - 1] }}
          </el-tag>
        </div>
        <div class="booking-info">
          <p>预约日期：{{ booking.bookingDate }}</p>
          <p>预约时间：{{ booking.startTime }} - {{ booking.endTime }}</p>
          <p class="price">总价：¥{{ booking.totalPrice }}</p>
        </div>
        <div class="booking-actions">
          <el-button v-if="booking.status === 1" type="primary" @click="handlePay(booking.id)">支付</el-button>
          <el-button v-if="booking.status === 1" type="danger" @click="handleCancel(booking.id)">取消</el-button>
        </div>
      </el-card>

      <el-empty v-if="bookings.length === 0" description="暂无预约" />
    </div>

    <el-pagination
      v-if="total > 0"
      :current-page="filters.pageNum"
      :page-size="filters.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadBookings"
      style="margin-top: 20px; justify-content: center"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBookingList, cancelBooking, payBooking } from '@/api/booking'
import { ElMessage, ElMessageBox } from 'element-plus'

const bookings = ref([])
const total = ref(0)
const activeTab = ref(null)

const filters = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null
})

const getStatusType = (status) => {
  const types = ['', 'warning', 'success', 'success', 'info', 'info']
  return types[status]
}

const loadBookings = async (page = 1) => {
  filters.pageNum = page
  filters.status = activeTab.value
  try {
    const res = await getBookingList(filters)
    bookings.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handlePay = async (id) => {
  try {
    await payBooking(id)
    ElMessage.success('支付成功')
    loadBookings()
  } catch (error) {
    console.error(error)
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      type: 'warning'
    })
    await cancelBooking(id)
    ElMessage.success('取消成功')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadBookings()
})
</script>

<style scoped>
.my-bookings {
  padding: 20px 0;
}

.my-bookings h2 {
  margin-bottom: 20px;
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.booking-item {
  margin-bottom: 10px;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.court-name {
  font-size: 16px;
  font-weight: bold;
}

.booking-info p {
  margin: 5px 0;
  color: #666;
}

.booking-info .price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.booking-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}
</style>
