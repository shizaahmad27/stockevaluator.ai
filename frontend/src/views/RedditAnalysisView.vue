<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white overflow-x-hidden">
    <!-- Navigation -->
    <nav class="flex justify-between items-center px-8 py-6 bg-gray-900/80 backdrop-blur-sm border-b border-gray-700/50">
      <div class="flex items-center gap-4">
        <button @click="router.go(-1)" class="p-2 rounded-lg hover:bg-gray-700/50 transition-colors">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
        </button>
        <div class="text-3xl font-bold bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent cursor-pointer" @click="navigateTo('/')">
          StockEvaluator
        </div>
      </div>
      <div class="flex gap-4">
        <button
            class="px-6 py-2 rounded-lg font-medium transition-all duration-300 border border-blue-500/50 text-blue-400 hover:bg-blue-500/20 hover:border-blue-400"
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

    <!-- Header Section -->
    <section class="px-8 py-12">
      <div class="max-w-7.5xl mx-auto">
        <div class="text-center mb-12">
          <h1 class="text-4xl md:text-6xl font-bold mb-4 bg-gradient-to-r from-white via-blue-200 to-purple-200 bg-clip-text text-transparent">
            Reddit Analysis Dashboard
          </h1>
          <p class="text-xl text-gray-300 mb-6">
            Comprehensive analysis of r/wallstreetbets sentiment and activity
          </p>
          <div class="flex items-center justify-center gap-4 text-sm text-gray-400">
            <div class="flex items-center gap-2">
              <div class="w-3 h-3 bg-green-400 rounded-full animate-pulse"></div>
              Live Data
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              Last updated: {{ lastUpdated }}
            </div>
          </div>
        </div>

        <!-- Key Metrics Overview -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
          <MetricCard
              title="Total Posts"
              :value="totalPosts"
              :loading="isLoadingPosts"
              icon="document-text"
              color="blue"
              description="Posts from r/wallstreetbets"
          />
          <MetricCard
              title="Total Comments"
              :value="totalComments"
              :loading="isLoadingComments"
              icon="chat-bubble"
              color="green"
              description="Comments analyzed for sentiment"
          />
          <MetricCard
              title="Trending Tickers"
              :value="trendingCount"
              :loading="isLoadingTickers"
              icon="trending-up"
              color="purple"
              description="Active stock mentions"
          />
          <MetricCard
              title="Avg Sentiment"
              :value="averageSentiment"
              :loading="isLoadingTickers"
              icon="heart"
              color="orange"
              description="Overall market mood"
              :is-percentage="true"
          />
        </div>

        <!-- Main Content Grid -->
        <div class="grid grid-cols-1 xl:grid-cols-3 gap-8">

          <!-- Left Column - Trending Tickers & Sentiment -->
          <div class="xl:col-span-1 space-y-8">

            <!-- Trending Tickers Section -->
            <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6">
              <div class="flex items-center justify-between mb-6">
                <h2 class="text-2xl font-bold flex items-center gap-3">
                  <div class="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                    </svg>
                  </div>
                  Trending Tickers
                </h2>
                <button @click="refreshTickers" class="p-2 rounded-lg hover:bg-gray-700/50 transition-colors">
                  <svg class="w-5 h-5" :class="{ 'animate-spin': isLoadingTickers }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                  </svg>
                </button>
              </div>

              <div v-if="isLoadingTickers" class="space-y-3 max-h-96 overflow-y-auto">
                <div v-for="i in 5" :key="i" class="animate-pulse">
                  <div class="flex items-center justify-between p-4 bg-gray-700/30 rounded-lg">
                    <div class="flex items-center gap-3">
                      <div class="w-8 h-8 bg-gray-600 rounded-full"></div>
                      <div class="w-16 h-4 bg-gray-600 rounded"></div>
                    </div>
                    <div class="w-20 h-4 bg-gray-600 rounded"></div>
                  </div>
                </div>
              </div>

              <div v-else-if="trendingTickers?.length" class="space-y-3 max-h-80 overflow-y-auto scrollbar-thin scrollbar-thumb-gray-600 scrollbar-track-gray-800">
                <div
                    v-for="(ticker, index) in trendingTickers"
                    :key="ticker.symbol"
                    class="flex items-center justify-between p-4 bg-gray-700/30 rounded-lg hover:bg-gray-700/50 transition-all duration-200 cursor-pointer"
                    @click="navigateToTicker(ticker.symbol ?? '')"
                >
                  <div class="flex items-center gap-3">
                    <div class="flex items-center justify-center w-8 h-8 rounded-full text-sm font-bold"
                         :class="getRankColor(index + 1)">
                      {{ index + 1 }}
                    </div>
                    <div>
                      <div class="font-bold text-lg">{{ ticker.symbol }}</div>
                      <div class="text-sm text-gray-400">{{ ticker.mentionCount || 0 }} mentions</div>
                    </div>
                  </div>
                  <div class="text-right">
                    <div :class="getSentimentColor(ticker.sentimentScore)" class="font-bold">
                      {{ formatSentiment(ticker.sentimentScore) }}
                    </div>
                    <div class="text-xs text-gray-400">
                      Trend: {{ (ticker.trendingScore || 0).toFixed(1) }}
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="text-center py-8 text-gray-400">
                <svg class="w-12 h-12 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3"></path>
                </svg>
                <p>No trending data available</p>
                <p class="text-xs mt-1">Data collection in progress...</p>
              </div>
            </div>

            <!-- Data Explanation Panel -->
            <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6">
              <h3 class="text-xl font-bold mb-4 flex items-center gap-2">
                <svg class="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                Understanding the Data
              </h3>
              <div class="space-y-4 text-sm">
                <div>
                  <h4 class="font-semibold text-blue-400 mb-2">Mention Count</h4>
                  <p class="text-gray-300">Number of times a ticker symbol appears in posts and comments. Higher counts indicate more community interest.</p>
                </div>
                <div>
                  <h4 class="font-semibold text-green-400 mb-2">Sentiment Score</h4>
                  <p class="text-gray-300">Calculated based on positive/negative language around ticker mentions. +100% = very bullish, -100% = very bearish.</p>
                </div>
                <div>
                  <h4 class="font-semibold text-purple-400 mb-2">Trending Score</h4>
                  <p class="text-gray-300">Combines mention frequency with recent activity. Higher scores indicate stocks gaining momentum.</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Middle & Right Columns - Posts and Comments -->
          <div class="xl:col-span-2 space-y-8">

            <!-- Recent Posts Section -->
            <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6">
              <div class="flex items-center justify-between mb-6">
                <h2 class="text-2xl font-bold flex items-center gap-3">
                  <div class="w-8 h-8 bg-blue-500 rounded-lg flex items-center justify-center">
                    <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                    </svg>
                  </div>
                  Recent Posts
                </h2>
                <div class="flex items-center gap-2">
                  <select v-model="postSortOrder" class="bg-gray-700 border border-gray-600 rounded-lg px-3 py-1 text-sm">
                    <option value="recent">Most Recent</option>
                    <option value="popular">Most Popular</option>
                    <option value="commented">Most Commented</option>
                  </select>
                  <button @click="refreshPosts" class="p-2 rounded-lg hover:bg-gray-700/50 transition-colors">
                    <svg class="w-5 h-5" :class="{ 'animate-spin': isLoadingPosts }" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                    </svg>
                  </button>
                </div>
              </div>

              <div v-if="isLoadingPosts" class="space-y-4">
                <div v-for="i in 3" :key="i" class="animate-pulse">
                  <div class="p-4 bg-gray-700/30 rounded-lg">
                    <div class="flex items-center gap-3 mb-3">
                      <div class="w-8 h-8 bg-gray-600 rounded-full"></div>
                      <div class="w-24 h-4 bg-gray-600 rounded"></div>
                      <div class="w-16 h-4 bg-gray-600 rounded"></div>
                    </div>
                    <div class="w-3/4 h-6 bg-gray-600 rounded mb-2"></div>
                    <div class="w-full h-4 bg-gray-600 rounded mb-2"></div>
                    <div class="w-2/3 h-4 bg-gray-600 rounded"></div>
                  </div>
                </div>
              </div>

              <div v-else-if="recentPosts?.length" class="space-y-4 max-h-96 overflow-y-auto">
                <div v-for="post in sortedPosts" :key="post.id"
                     class="p-4 bg-gray-700/30 rounded-lg hover:bg-gray-700/50 transition-all duration-200">
                  <div class="flex items-center justify-between mb-3">
                    <div class="flex items-center gap-3">
                      <div class="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center text-sm font-bold">
                        {{ post.author?.charAt(0).toUpperCase() || 'A' }}
                      </div>
                      <div>
                        <div class="font-semibold">u/{{ post.author || 'Anonymous' }}</div>
                        <div class="text-xs text-gray-400">{{ formatDate(post.createdDate) }}</div>
                      </div>
                    </div>
                    <div class="flex items-center gap-4 text-sm text-gray-400">
                      <div class="flex items-center gap-1">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12"></path>
                        </svg>
                        {{ post.score || 0 }}
                      </div>
                      <div class="flex items-center gap-1">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
                        </svg>
                        {{ post.commentsCount || 0 }}
                      </div>
                    </div>
                  </div>

                  <h3 class="font-bold text-lg mb-2 line-clamp-2">{{ post.title }}</h3>

                  <p v-if="post.content" class="text-gray-300 text-sm mb-3 line-clamp-3">
                    {{ post.content }}
                  </p>

                  <div v-if="post.tickerMentions?.length" class="flex flex-wrap gap-2">
                    <span v-for="ticker in post.tickerMentions" :key="ticker"
                          class="px-2 py-1 bg-blue-500/20 text-blue-400 rounded-md text-xs font-medium cursor-pointer hover:bg-blue-500/30 transition-colors"
                          @click="navigateToTicker(ticker)">
                      ${{ ticker }}
                    </span>
                  </div>
                </div>
              </div>

              <div v-else class="text-center py-8 text-gray-400">
                <svg class="w-12 h-12 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <p>No posts available</p>
                <p class="text-xs mt-1">Data collection in progress...</p>
              </div>
            </div>

            <!-- Activity Timeline -->
            <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-6">
              <h2 class="text-2xl font-bold mb-6 flex items-center gap-3">
                <div class="w-8 h-8 bg-green-500 rounded-lg flex items-center justify-center">
                  <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                </div>
                Activity Timeline
              </h2>

              <div class="text-center py-8 text-gray-400">
                <svg class="w-12 h-12 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
                </svg>
                <p>Activity charts coming soon</p>
                <p class="text-xs mt-1">Will show posting frequency and sentiment over time</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGetTrendingTickers, useGetRecentPosts } from '@/api/generated/reddit-controller/reddit-controller'
import MetricCard from '@/components/reddit/MetricCard.vue'

const router = useRouter()

// Reactive data
const lastUpdated = ref(new Date().toLocaleString())
const postSortOrder = ref('recent')

// API calls using Orval-generated hooks
const {
  data: trendingTickers,
  isLoading: isLoadingTickers,
  refetch: refetchTickers
} = useGetTrendingTickers({
  query: {
    staleTime: 30000,
    refetchInterval: 60000,
  }
})

const {
  data: recentPosts,
  isLoading: isLoadingPosts,
  refetch: refetchPosts
} = useGetRecentPosts({
  query: {
    staleTime: 30000,
    refetchInterval: 60000,
  }
})

// Computed properties
const totalPosts = computed(() => recentPosts.value?.length || 0)
const totalComments = computed(() => {
  return recentPosts.value?.reduce((sum, post) => sum + (post.commentsCount || 0), 0) || 0
})
const trendingCount = computed(() => trendingTickers.value?.length || 0)
const averageSentiment = computed(() => {
  if (!trendingTickers.value?.length) return 0
  const sum = trendingTickers.value.reduce((acc, ticker) => acc + (ticker.sentimentScore || 0), 0)
  return sum / trendingTickers.value.length
})

const sortedPosts = computed(() => {
  if (!recentPosts.value) return []

  const posts = [...recentPosts.value]
  switch (postSortOrder.value) {
    case 'popular':
      return posts.sort((a, b) => (b.score || 0) - (a.score || 0))
    case 'commented':
      return posts.sort((a, b) => (b.commentsCount || 0) - (a.commentsCount || 0))
    default:
      return posts.sort((a, b) => new Date(b.createdDate || 0).getTime() - new Date(a.createdDate || 0).getTime())
  }
})

// Methods
const navigateTo = (path: string) => {
  router.push(path)
}

const navigateToTicker = (ticker: string) => {
  router.push(`/ticker/${ticker}`)
}

const refreshTickers = async () => {
  await refetchTickers()
  lastUpdated.value = new Date().toLocaleString()
}

const refreshPosts = async () => {
  await refetchPosts()
  lastUpdated.value = new Date().toLocaleString()
}

const getRankColor = (rank: number): string => {
  switch (rank) {
    case 1: return 'bg-yellow-500 text-yellow-900'
    case 2: return 'bg-gray-400 text-gray-900'
    case 3: return 'bg-amber-600 text-amber-100'
    default: return 'bg-gray-600 text-gray-200'
  }
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

// Lifecycle
onMounted(() => {
  // Auto-refresh every 5 minutes
  setInterval(() => {
    refreshTickers()
    refreshPosts()
  }, 300000)
})

// For loading states when we don't have the actual comment data
const isLoadingComments = computed(() => isLoadingPosts.value)
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
