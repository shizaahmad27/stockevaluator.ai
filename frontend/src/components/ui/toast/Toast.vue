<script setup lang="ts">
import { useToast } from './use-toast'
import { cn } from '@/lib/utils'

const { toasts } = useToast()
</script>

<template>
  <div class="fixed top-0 right-0 z-50 p-4 space-y-4">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="
          cn(
            'p-4 rounded-lg shadow-lg max-w-sm w-full bg-white border',
            toast.variant === 'destructive' ? 'border-red-500 text-red-500' : 'border-gray-200'
          )
        "
      >
        <div v-if="toast.title" class="font-semibold">{{ toast.title }}</div>
        <div v-if="toast.description" class="text-sm mt-1">{{ toast.description }}</div>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 