<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<template>
  <nav class="flex justify-between items-center px-8 py-4 bg-white shadow-md">
    <div class="text-2xl font-bold text-gray-800 cursor-pointer" @click="navigateTo('/')">
      StockEvaluator
    </div>
    <div class="flex gap-4">
      <button
        class="px-4 py-2 rounded-md font-medium transition-colors duration-200 border border-blue-500 text-blue-500 hover:bg-blue-500 hover:text-white"
        @click="navigateTo('/login')"
      >
        Login
      </button>
      <button
        class="px-4 py-2 rounded-md font-medium transition-colors duration-200 bg-blue-500 text-white hover:bg-blue-600"
        @click="navigateTo('/signup')"
      >
        Sign Up
      </button>
    </div>
  </nav>
</template>
