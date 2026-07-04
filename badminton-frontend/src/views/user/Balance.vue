<template>
  <div class="balance-page">
    <h2>余额充值</h2>

    <el-card>
      <div class="balance-info">
        <p class="label">当前余额</p>
        <p class="amount">¥{{ balance }}</p>
      </div>

      <el-divider />

      <div class="recharge-form">
        <p class="label">选择充值金额</p>
        <el-radio-group v-model="amount">
          <el-radio-button :label="10">10元</el-radio-button>
          <el-radio-button :label="20">20元</el-radio-button>
          <el-radio-button :label="50">50元</el-radio-button>
          <el-radio-button :label="100">100元</el-radio-button>
          <el-radio-button :label="200">200元</el-radio-button>
        </el-radio-group>

        <el-input-number v-model="amount" :min="1" :max="1000" style="margin-top: 20px" />

        <el-button type="primary" size="large" @click="handleRecharge" style="margin-top: 20px; width: 100%">
          立即充值
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBalance, recharge } from '@/api/user'
import { ElMessage } from 'element-plus'

const balance = ref(0)
const amount = ref(50)

const loadBalance = async () => {
  try {
    const res = await getBalance()
    balance.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleRecharge = async () => {
  if (amount.value <= 0) {
    ElMessage.warning('请输入有效的充值金额')
    return
  }

  try {
    await recharge(amount.value)
    ElMessage.success('充值成功')
    loadBalance()
    amount.value = 50
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadBalance()
})
</script>

<style scoped>
.balance-page {
  padding: 20px 0;
  max-width: 500px;
}

.balance-page h2 {
  margin-bottom: 20px;
}

.balance-info {
  text-align: center;
  padding: 20px 0;
}

.balance-info .label {
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.balance-info .amount {
  color: #f56c6c;
  font-size: 36px;
  font-weight: bold;
  margin: 0;
}

.recharge-form {
  padding: 20px 0;
}

.recharge-form .label {
  margin-bottom: 15px;
  color: #666;
}
</style>
