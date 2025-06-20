<template>
    <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-8 hover:bg-gray-800/60 transition-all duration-300">
      <div class="flex items-center gap-3 mb-6">
        <div class="w-10 h-10 bg-orange-500 rounded-lg flex items-center justify-center">
          <svg class="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 0A12 12 0 0 0 0 12a12 12 0 0 0 12 12 12 12 0 0 0 12-12A12 12 0 0 0 12 0zm5.01 4.744c.688 0 1.25.561 1.25 1.249a1.25 1.25 0 0 1-2.498.056l-2.597-.547-.8 3.747c1.824.07 3.48.632 4.674 1.488.308-.309.73-.491 1.207-.491.968 0 1.754.786 1.754 1.754 0 .716-.435 1.333-1.01 1.614a3.111 3.111 0 0 1 .042.52c0 2.694-3.13 4.87-7.004 4.87-3.874 0-7.004-2.176-7.004-4.87 0-.183.015-.366.043-.534A1.748 1.748 0 0 1 4.028 12c0-.968.786-1.754 1.754-1.754.463 0 .898.196 1.207.49 1.207-.883 2.878-1.43 4.744-1.487l.885-4.182a.342.342 0 0 1 .14-.197.35.35 0 0 1 .238-.042l2.906.617a1.214 1.214 0 0 1 1.108-.701zM9.25 12C8.561 12 8 12.562 8 13.25c0 .687.561 1.248 1.25 1.248.687 0 1.248-.561 1.248-1.249 0-.688-.561-1.249-1.249-1.249zm5.5 0c-.687 0-1.248.561-1.248 1.25 0 .687.561 1.248 1.249 1.248.688 0 1.249-.561 1.249-1.249 0-.687-.562-1.249-1.25-1.249zm-5.466 3.99a.327.327 0 0 0-.231.094.33.33 0 0 0 0 .463c.842.842 2.484.913 2.961.913.477 0 2.105-.056 2.961-.913a.361.361 0 0 0 .029-.463.33.33 0 0 0-.464 0c-.547.533-1.684.73-2.512.73-.828 0-1.979-.196-2.512-.73a.326.326 0 0 0-.232-.095z"/>
          </svg>
        </div>
        <h2 class="text-2xl font-bold">Reddit Sentiment</h2>
        <span class="ml-auto text-sm text-gray-400">r/wallstreetbets</span>
      </div>

      <div class="space-y-4">
        <!-- Loading State -->
        <div v-if="isLoading" class="flex justify-center py-8">
          <svg class="animate-spin h-8 w-8 text-orange-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>

        <!-- Error State -->
        <div v-else-if="isError" class="text-center py-8">
          <div class="text-red-400 mb-2">
            <svg class="w-8 h-8 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
          <p class="text-gray-400 text-sm">Unable to load Reddit trends</p>
          <button
              @click="() => refetch()"
              class="mt-2 text-orange-400 hover:text-orange-300 text-sm underline"
          >
            Try again
          </button>
        </div>

        <!-- No Data State -->
        <div v-else-if="!trendingTickers || trendingTickers.length === 0" class="text-center py-8 text-gray-400">
          <div class="mb-2">
            <svg class="w-8 h-8 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path>
            </svg>
          </div>
          <p class="text-sm">No Reddit data available yet</p>
          <p class="text-xs mt-1">The scraper is collecting data...</p>
        </div>

        <!-- Success State - Display Trending Tickers -->
        <div v-else>
        <div
            v-for="ticker in displayTickers"
            :key="ticker.symbol"
            class="flex items-center justify-between p-4 bg-gray-700/30 rounded-lg hover:bg-gray-700/50 transition-all duration-200 cursor-pointer"
            @click="$emit('tickerClick', ticker.symbol ?? '')"
          >            <div class="flex items-center gap-3">
              <span class="font-bold text-lg">{{ ticker.symbol }}</span>
              <span class="text-gray-300">{{ ticker.mentionCount || 0 }} mentions</span>
            </div>
            <div class="flex items-center gap-2">
              <span :class="getSentimentColor(ticker.sentimentScore)" class="font-medium">
                {{ formatSentiment(ticker.sentimentScore) }}
              </span>
              <svg v-if="(ticker.sentimentScore || 0) > 0" class="w-4 h-4 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 14l9-9 3 3L9 18l-4.5-4.5z"></path>
              </svg>
              <svg v-else-if="(ticker.sentimentScore || 0) < 0" class="w-4 h-4 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
              <svg v-else class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <button
        class="w-full mt-6 py-3 bg-orange-500/20 border border-orange-500/30 text-orange-400 rounded-lg hover:bg-orange-500/30 transition-all duration-300 font-medium"
        @click="$emit('viewFullAnalysis')"
      >
        View Full Analysis
      </button>
    </div>
  </template>

  <script setup lang="ts">
  import { computed } from 'vue'
  import { useGetTrendingTickers } from '@/api/generated/reddit-controller/reddit-controller'

  // Define emits
  defineEmits<{
    tickerClick: [ticker: string]
    viewFullAnalysis: []
  }>()

  // Use the Orval-generated hook
  const { data: trendingTickers, isLoading, isError, refetch } = useGetTrendingTickers({
    query: {
      staleTime: 30000, // Cache for 30 seconds
      refetchInterval: 60000, // Refetch every minute
    }
  })

  // Computed properties
  const displayTickers = computed(() => {
    if (!trendingTickers.value) return []
    return trendingTickers.value.slice(0, 5) // Show top 5 tickers
  })

  // Helper functions
  const getSentimentColor = (sentiment?: number | null): string => {
    if (!sentiment) return 'text-gray-400'
    return sentiment > 0 ? 'text-green-400' : sentiment < 0 ? 'text-red-400' : 'text-gray-400'
  }

  const formatSentiment = (sentiment?: number | null): string => {
    if (!sentiment) return '0.0%'
    const formatted = Math.abs(sentiment).toFixed(1)
    return sentiment > 0 ? `+${formatted}%` : sentiment < 0 ? `-${formatted}%` : '0.0%'
  }
  </script>
