<script setup lang="ts">
  import { useToast } from './use-toast'
  import { cn } from '@/lib/utils'
  import { computed } from 'vue'

  // Define interface based on what useToast actually returns
  interface ToastItem {
    title?: string
    description?: string
    variant?: 'default' | 'destructive'
  }

  // Get the original toast data
  const { toast, toasts } = useToast()

  // Create a computed property that adds IDs to the toasts
  const toastsWithIds = computed(() =>
    toasts.value.map((item, index) => ({
      ...item,
      id: index // Use index as ID if none exists
    }))
  )
  </script>

  <template>
    <div class="fixed top-0 right-0 z-50 p-4 space-y-4">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toastsWithIds"
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
