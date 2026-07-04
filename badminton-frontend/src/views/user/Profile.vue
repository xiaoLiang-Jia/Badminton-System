<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    <el-card>
      <el-form :model="form" label-width="80px">
        <el-form-item label="头像">
          <el-avatar :size="80" :src="form.avatar" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="余额">
          <span style="color: #f56c6c; font-size: 20px">¥{{ form.balance }}</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate">保存修改</el-button>
          <el-button @click="showPasswordDialog = true">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUser, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const form = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  balance: 0
})

const showPasswordDialog = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    Object.assign(form, res.data)
  } catch (error) {
    console.error(error)
  }
}

const handleUpdate = async () => {
  try {
    await updateUser({
      realName: form.realName,
      phone: form.phone,
      email: form.email
    })
    ElMessage.success('更新成功')
    userStore.fetchUserInfo()
  } catch (error) {
    console.error(error)
  }
}

const handleChangePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }

  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  padding: 20px 0;
  max-width: 600px;
}

.profile-page h2 {
  margin-bottom: 20px;
}
</style>
