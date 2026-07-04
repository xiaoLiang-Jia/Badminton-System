import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { getUserInfo, login, register } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 3)
  const isCoach = computed(() => user.value?.role === 2)

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      user.value = res.data
    } catch (error) {
      console.error('获取用户信息失败', error)
      logout()
    }
  }

  async function loginAction(loginData) {
    const res = await login(loginData)
    token.value = res.data.token
    user.value = res.data.user
    setToken(res.data.token)
    return res
  }

  async function registerAction(registerData) {
    const res = await register(registerData)
    return res
  }

  function logout() {
    token.value = ''
    user.value = null
    removeToken()
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    isCoach,
    fetchUserInfo,
    loginAction,
    registerAction,
    logout
  }
})
