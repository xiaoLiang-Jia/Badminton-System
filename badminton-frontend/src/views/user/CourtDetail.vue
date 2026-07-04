<template>
  <div class="court-detail">
    <el-button @click="$router.back()" style="margin-bottom: 20px">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-card v-if="court">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-image :src="court.imageUrl || '/images/court-default.jpg'" fit="cover" style="width: 100%" />
        </el-col>
        <el-col :span="12">
          <h2>{{ court.name }}</h2>
          <p class="location">{{ court.location }}</p>
          <div class="tags">
            <el-tag :type="court.type === 1 ? 'success' : court.type === 2 ? 'warning' : 'info'">
              {{ ['室内木地', '室内地胶', '室外'][court.type - 1] }}
            </el-tag>
            <el-tag :type="court.status === 1 ? 'success' : 'danger'">
              {{ court.status === 1 ? '可用' : '不可用' }}
            </el-tag>
          </div>
          <p class="price">¥{{ court.price }} <span>/小时</span></p>
          <p class="capacity">容纳人数：{{ court.capacity }}人</p>
          <p class="open-time">营业时间：{{ court.openTime }}</p>
          <p class="description">{{ court.description }}</p>

          <el-divider />

          <div class="booking-form">
            <h3>预约场地</h3>
            <el-form :model="bookingForm" label-width="80px">
              <el-form-item label="预约日期">
                <el-date-picker
                  v-model="bookingForm.date"
                  type="date"
                  placeholder="选择日期"
                  :disabled-date="disabledDate"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item label="开始时间">
                <el-select v-model="bookingForm.startTime" placeholder="选择开始时间" style="width: 100%">
                  <el-option v-for="slot in timeSlots" :key="slot.time" :label="slot.time" :value="slot.time" :disabled="!slot.available" />
                </el-select>
              </el-form-item>
              <el-form-item label="结束时间">
                <el-select v-model="bookingForm.endTime" placeholder="选择结束时间" style="width: 100%">
                  <el-option v-for="slot in timeSlots" :key="slot.time" :label="slot.time" :value="slot.time" :disabled="!slot.available" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :disabled="court.status !== 1" @click="handleBooking" style="width: 100%">
                  立即预约
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCourtById, getAvailableTimeSlots } from '@/api/court'
import { createBooking } from '@/api/booking'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const court = ref(null)
const timeSlots = ref([])

const bookingForm = reactive({
  date: null,
  startTime: '',
  endTime: ''
})

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000
}

const loadCourt = async () => {
  try {
    const res = await getCourtById(route.params.id)
    court.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadTimeSlots = async () => {
  if (!bookingForm.date) return
  const date = new Date(bookingForm.date).toISOString().split('T')[0]
  try {
    const res = await getAvailableTimeSlots(court.value.id, date)
    timeSlots.value = res.data
  } catch (error) {
    console.error(error)
  }
}

watch(() => bookingForm.date, loadTimeSlots)

const handleBooking = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  if (!bookingForm.date || !bookingForm.startTime || !bookingForm.endTime) {
    ElMessage.warning('请填写完整的预约信息')
    return
  }

  try {
    const date = new Date(bookingForm.date).toISOString().split('T')[0]
    await createBooking({
      courtId: court.value.id,
      bookingDate: date,
      startTime: bookingForm.startTime,
      endTime: bookingForm.endTime
    })
    ElMessage.success('预约成功')
    router.push('/user/bookings')
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadCourt()
})
</script>

<style scoped>
.court-detail {
  padding: 20px 0;
}

.court-detail h2 {
  margin-bottom: 10px;
}

.location {
  color: #999;
  margin-bottom: 15px;
}

.tags {
  margin-bottom: 15px;
}

.tags .el-tag {
  margin-right: 10px;
}

.price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}

.price span {
  font-size: 14px;
  font-weight: normal;
  color: #999;
}

.capacity, .open-time, .description {
  margin-bottom: 10px;
  color: #666;
}

.booking-form h3 {
  margin-bottom: 15px;
}
</style>
