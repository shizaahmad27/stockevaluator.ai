<template>
  <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6 hover:bg-gray-800/60 transition-all duration-300">
    <div class="flex items-center justify-between mb-4">
      <div :class="iconColorClass" class="w-12 h-12 rounded-xl flex items-center justify-center">
        <component :is="iconComponent" class="w-6 h-6 text-white" />
      </div>
      <div v-if="loading" class="animate-spin">
        <svg class="w-5 h-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
    </div>

    <div class="space-y-1">
      <p class="text-sm text-gray-400">{{ title }}</p>
      <div v-if="loading" class="animate-pulse">
        <div class="h-8 bg-gray-600 rounded w-20"></div>
      </div>
      <div v-else class="text-3xl font-bold text-white">
        {{ formattedValue }}
      </div>
      <p class="text-xs text-gray-500">{{ description }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'

interface Props {
  title: string
  value: number
  loading?: boolean
  icon: string
  color: 'blue' | 'green' | 'purple' | 'orange' | 'red'
  description: string
  isPercentage?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  isPercentage: false
})

// Icon components map
const iconComponents = {
  'document-text': () => h('svg', {
    fill: 'none',
    stroke: 'currentColor',
    viewBox: '0 0 24 24'
  }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z'
    })
  ]),
  'chat-bubble': () => h('svg', {
    fill: 'none',
    stroke: 'currentColor',
    viewBox: '0 0 24 24'
  }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z'
    })
  ]),
  'trending-up': () => h('svg', {
    fill: 'none',
    stroke: 'currentColor',
    viewBox: '0 0 24 24'
  }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6'
    })
  ]),
  'heart': () => h('svg', {
    fill: 'none',
    stroke: 'currentColor',
    viewBox: '0 0 24 24'
  }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: 'M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z'
    })
  ])
}

// Color classes map
const colorClasses = {
  blue: 'bg-blue-500',
  green: 'bg-green-500',
  purple: 'bg-purple-500',
  orange: 'bg-orange-500',
  red: 'bg-red-500'
}

// Computed properties
const iconComponent = computed(() => {
  return iconComponents[props.icon as keyof typeof iconComponents] || iconComponents['document-text']
})

const iconColorClass = computed(() => {
  return colorClasses[props.color]
})

const formattedValue = computed(() => {
  if (props.loading) return '...'

  if (props.isPercentage) {
    const sign = props.value >= 0 ? '+' : ''
    return `${sign}${props.value.toFixed(1)}%`
  }

  // Format large numbers with commas
  return props.value.toLocaleString()
})
</script>
