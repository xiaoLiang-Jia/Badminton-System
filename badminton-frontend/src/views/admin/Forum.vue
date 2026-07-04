<template>
  <div class="admin-forum">
    <h2>论坛管理</h2>
    <el-table :data="forums" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="user" label="作者">
        <template #default="{ row }">
          {{ row.user?.username }}
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类">
        <template #default="{ row }">
          {{ ['技术讨论', '约球', '比赛心得', '器材交流', '新手求助'][row.category - 1] }}
        </template>
      </el-table-column>
      <el-table-column prop="likes" label="点赞" />
      <el-table-column prop="views" label="浏览" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'warning' : 'info'">
            {{ ['待审核', '已发布', '已删除'][row.status] }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const forums = ref([])

const loadForums = async () => {
  // 模拟数据
  forums.value = []
}

onMounted(() => {
  loadForums()
})
</script>

<style scoped>
.admin-forum {
  padding: 20px 0;
}
</style>
