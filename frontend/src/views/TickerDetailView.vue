<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white overflow-x-hidden">
    <!-- Navigation -->
    <nav class="flex justify-between items-center px-8 py-6 bg-gray-900/80 backdrop-blur-sm border-b border-gray-700/50">
      <div class="flex items-center gap-4">
        <button @click="router.go(-1)" class="p-2 rounded-lg hover:bg-gray-700/50 transition-colors">          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
        </button>
        <div class="text-3xl font-bold bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent cursor-pointer" @click="navigateTo('/')">
          StockEvaluator
        </div>
      </div>
      <div class="flex gap-4">
        <button
            class="px-6 py-2 rounded-lg font-medium transition-all duration-300 border border-blue-500/50 text-blue-400 hover:bg-blue-500/20"
            @click="navigateTo('/login')"
        >
          Login
        </button>
        <button
            class="px-6 py-2 rounded-lg font-medium transition-all duration-300 bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:from-blue-600 hover:to-purple-700"
            @click="navigateTo('/signup')"
        >
          Sign Up
        </button>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-8 py-12">
      <!-- Loading State -->
      <div v-if="isLoading" class="text-center py-16">
        <svg class="animate-spin h-12 w-12 text-blue-400 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <p class="text-xl text-gray-300">Loading {{ tickerSymbol }} data...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="isError" class="text-center py-16">
        <svg class="w-16 h-16 text-red-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
        <h1 class="text-3xl font-bold mb-4">Ticker Not Found</h1>
        <p class="text-xl text-gray-300 mb-6">
          No Reddit data found for <span class="text-blue-400 font-mono">${{ tickerSymbol }}</span>
        </p>
        <div class="space-x-4">
          <button
              @click="router.go(-1)"
              class="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors"
          >
            Go Back
          </button>
          <button
              @click="navigateTo('/')"
              class="px-6 py-3 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors"
          >
            Home
          </button>
        </div>
      </div>

      <!-- Success State -->
      <div v-else-if="tickerData">
        <!-- Header -->
        <div class="text-center mb-12">
          <h1 class="text-5xl md:text-7xl font-bold mb-4">
            <span class="text-blue-400 font-mono">${{ tickerData.symbol }}</span>
          </h1>
          <h2 v-if="tickerData.companyName" class="text-2xl text-gray-300 mb-6">
            {{ tickerData.companyName }}
          </h2>
          <div class="flex items-center justify-center gap-8 text-lg">
            <div class="flex items-center gap-2">
              <svg class="w-6 h-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
              </svg>
              <span class="text-gray-300">{{ tickerData.mentionCount || 0 }} mentions</span>
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-6 h-6" :class="getSentimentColor(tickerData.sentimentScore)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
              </svg>
              <span :class="getSentimentColor(tickerData.sentimentScore)">
                {{ formatSentiment(tickerData.sentimentScore) }} sentiment
              </span>
            </div>
          </div>
        </div>

        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
          <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6 text-center">
            <div class="text-3xl font-bold text-orange-400 mb-2">{{ tickerData.mentionCount || 0 }}</div>
            <div class="text-gray-300">Total Mentions</div>
            <div class="text-sm text-gray-500 mt-1">Across posts and comments</div>
          </div>
          <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6 text-center">
            <div class="text-3xl font-bold mb-2" :class="getSentimentColor(tickerData.sentimentScore)">
              {{ formatSentiment(tickerData.sentimentScore) }}
            </div>
            <div class="text-gray-300">Sentiment Score</div>
            <div class="text-sm text-gray-500 mt-1">Community mood</div>
          </div>
          <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6 text-center">
            <div class="text-3xl font-bold text-purple-400 mb-2">{{ (tickerData.trendingScore || 0).toFixed(1) }}</div>
            <div class="text-gray-300">Trending Score</div>
            <div class="text-sm text-gray-500 mt-1">Momentum indicator</div>
          </div>
        </div>

        <!-- Content Grid -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">

          <!-- Posts Section -->
          <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6">
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-2xl font-bold flex items-center gap-3">
                <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                Recent Posts
              </h3>
              <button
                  class="p-2 rounded-lg hover:bg-gray-700/50 transition-colors"
                  @click="() => refetchPosts()"
              >
                Refresh Posts
              </button>
            </div>

            <div v-if="isLoadingPosts" class="space-y-4">
              <div v-for="i in 3" :key="i" class="animate-pulse bg-gray-700/30 rounded-lg p-4 h-24"></div>
            </div>

            <div v-else-if="posts?.length" class="space-y-4 max-h-96 overflow-y-auto">
              <div v-for="post in posts" :key="post.id" class="bg-gray-700/30 rounded-lg p-4 hover:bg-gray-700/50 transition-colors">
                <div class="flex items-center gap-3 mb-2">
                  <div class="w-6 h-6 bg-orange-500 rounded-full flex items-center justify-center text-xs font-bold">
                    {{ post.author?.charAt(0).toUpperCase() || 'A' }}
                  </div>
                  <span class="text-sm text-gray-400">u/{{ post.author || 'Anonymous' }}</span>
                  <span class="text-xs text-gray-500">{{ formatDate(post.createdDate) }}</span>
                </div>
                <h4 class="font-bold mb-2 line-clamp-2">{{ post.title }}</h4>
                <p v-if="post.content" class="text-sm text-gray-300 line-clamp-2">{{ post.content }}</p>
                <div class="flex items-center gap-4 mt-2 text-xs text-gray-400">
                  <span>↑ {{ post.score || 0 }}</span>
                  <span>💬 {{ post.commentsCount || 0 }}</span>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-8 text-gray-400">
              <svg class="w-12 h-12 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
              <p>No posts found mentioning {{ tickerSymbol }}</p>
            </div>
          </div>

          <!-- Comments Section -->
          <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6">
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-2xl font-bold flex items-center gap-3">
                <svg class="w-8 h-8 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                </svg>
                Recent Comments
              </h3>
              <button
                  class="p-2 rounded-lg hover:bg-gray-700/50 transition-colors"
                  @click="() => refetchComments()"
              >
                Refresh Comments
              </button>
            </div>

            <div v-if="isLoadingComments" class="space-y-4">
              <div v-for="i in 5" :key="i" class="animate-pulse bg-gray-700/30 rounded-lg p-3 h-16"></div>
            </div>

            <div v-else-if="comments?.length" class="space-y-3 max-h-96 overflow-y-auto">
              <div v-for="comment in comments" :key="comment.id" class="bg-gray-700/30 rounded-lg p-3 hover:bg-gray-700/50 transition-colors">
                <div class="flex items-center gap-3 mb-2">
                  <div class="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center text-xs font-bold">
                    {{ comment.author?.charAt(0).toUpperCase() || 'A' }}
                  </div>
                  <span class="text-sm text-gray-400">u/{{ comment.author || 'Anonymous' }}</span>
                  <span class="text-xs text-gray-500">{{ formatDate(comment.createdDate) }}</span>
                </div>
                <p class="text-sm text-gray-300 line-clamp-3">{{ comment.content }}</p>
                <div class="flex items-center gap-2 mt-2 text-xs text-gray-400">
                  <span>↑ {{ comment.score || 0 }}</span>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-8 text-gray-400">
              <svg class="w-12 h-12 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
              </svg>
              <p>No comments found mentioning {{ tickerSymbol }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useGetTicker, useGetPostsByTicker, useGetCommentsByTicker } from '@/api/generated/reddit-controller/reddit-controller'

const route = useRoute()
const router = useRouter()

// Get ticker symbol from route params
const tickerSymbol = computed(() => (route.params.symbol as string)?.toUpperCase())

// API calls
const {
  data: tickerData,
  isLoading,
  isError
} = useGetTicker(tickerSymbol, {
  query: {
    enabled: computed(() => !!tickerSymbol.value)
  }
})

const {
  data: posts,
  isLoading: isLoadingPosts,
  refetch: refetchPosts
} = useGetPostsByTicker(tickerSymbol, {
  query: {
    enabled: computed(() => !!tickerSymbol.value)
  }
})

const {
  data: comments,
  isLoading: isLoadingComments,
  refetch: refetchComments
} = useGetCommentsByTicker(tickerSymbol, {
  query: {
    enabled: computed(() => !!tickerSymbol.value)
  }
})

// Methods
const navigateTo = (path: string) => {
  router.push(path)
}

const getSentimentColor = (sentiment?: number | null): string => {
  if (!sentiment) return 'text-gray-400'
  return sentiment > 0 ? 'text-green-400' : sentiment < 0 ? 'text-red-400' : 'text-gray-400'
}

const formatSentiment = (sentiment?: number | null): string => {
  if (!sentiment) return '0.0%'
  const formatted = Math.abs(sentiment).toFixed(1)
  return sentiment > 0 ? `+${formatted}%` : sentiment < 0 ? `-${formatted}%` : '0.0%'
}

const formatDate = (dateString?: string | null): string => {
  if (!dateString) return 'Unknown'
  return new Date(dateString).toLocaleString()
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
