<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white overflow-x-hidden">
    <!-- Navigation -->
    <nav class="flex justify-between items-center px-8 py-6 bg-gray-900/80 backdrop-blur-sm border-b border-gray-700/50">
      <div class="text-3xl font-bold bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent cursor-pointer" @click="navigateTo('/')">
        StockEvaluator
      </div>
      <div class="flex gap-4">
        <button
            class="px-6 py-2 rounded-lg font-medium transition-all duration-300 border border-blue-500/50 text-blue-400 hover:bg-blue-500/20 hover:border-blue-400 hover:shadow-lg hover:shadow-blue-500/25"
            @click="navigateTo('/login')"
        >
          Login
        </button>
        <button
            class="px-6 py-2 rounded-lg font-medium transition-all duration-300 bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:from-blue-600 hover:to-purple-700 hover:shadow-lg hover:shadow-purple-500/25 transform hover:scale-105"
            @click="navigateTo('/signup')"
        >
          Sign Up
        </button>
      </div>
    </nav>

    <!-- Hero Section with Search -->
    <section class="px-8 py-16 text-center">
      <div class="max-w-4xl mx-auto">
        <h1 class="text-5xl md:text-7xl font-bold mb-6 bg-gradient-to-r from-white via-blue-200 to-purple-200 bg-clip-text text-transparent">
          Stock Market Intelligence
        </h1>
        <p class="text-xl md:text-2xl text-gray-300 mb-12 leading-relaxed">
          Get real-time Reddit sentiment analysis and financial data for any stock
        </p>

        <!-- Search Bar -->
        <div class="relative max-w-2xl mx-auto mb-8">
          <div class="relative">
            <input
                v-model="searchQuery"
                type="text"
                placeholder="Search for stocks (e.g., AAPL, TSLA, GME)..."
                class="w-full px-6 py-4 pl-12 text-lg bg-gray-800/50 border border-gray-600/50 rounded-xl focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 backdrop-blur-sm transition-all duration-300"
                @keyup.enter="searchStock"
            />
            <svg class="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
            </svg>
          </div>
          <button
              @click="searchStock"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 px-6 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:from-blue-600 hover:to-purple-700 transition-all duration-300 font-medium"
          >
            Search
          </button>
        </div>

        <!-- Quick Actions -->
        <div class="flex flex-wrap gap-3 justify-center">
          <button
              v-for="ticker in popularTickers"
              :key="ticker"
              @click="quickSearch(ticker)"
              class="px-4 py-2 bg-gray-800/60 border border-gray-600/30 rounded-lg hover:bg-gray-700/60 hover:border-gray-500 transition-all duration-300 text-sm font-medium"
          >
            {{ ticker }}
          </button>
        </div>
      </div>
    </section>

    <!-- Main Content Grid -->
    <section class="px-8 py-12">
      <div class="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-8">

        <!-- Reddit Trends Card -->
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
            <div v-for="trend in redditTrends" :key="trend.ticker" class="flex items-center justify-between p-4 bg-gray-700/30 rounded-lg">
              <div class="flex items-center gap-3">
                <span class="font-bold text-lg">{{ trend.ticker }}</span>
                <span class="text-gray-300">{{ trend.mentions }} mentions</span>
              </div>
              <div class="flex items-center gap-2">
                <span :class="trend.sentiment > 0 ? 'text-green-400' : 'text-red-400'" class="font-medium">
                  {{ trend.sentiment > 0 ? '+' : '' }}{{ trend.sentiment }}%
                </span>
                <svg v-if="trend.sentiment > 0" class="w-4 h-4 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 14l9-9 3 3L9 18l-4.5-4.5z"></path>
                </svg>
                <svg v-else class="w-4 h-4 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </div>
            </div>
          </div>

          <button class="w-full mt-6 py-3 bg-orange-500/20 border border-orange-500/30 text-orange-400 rounded-lg hover:bg-orange-500/30 transition-all duration-300 font-medium">
            View Full Analysis
          </button>
        </div>

        <!-- Market Overview Card -->
        <div class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl p-8 hover:bg-gray-800/60 transition-all duration-300">
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
              </svg>
            </div>
            <h2 class="text-2xl font-bold">Market Overview</h2>
            <span class="ml-auto text-sm text-gray-400">Live Data</span>
          </div>

          <div class="grid grid-cols-2 gap-4 mb-6">
            <div class="text-center p-4 bg-gray-700/30 rounded-lg">
              <div class="text-2xl font-bold text-green-400">+1.2%</div>
              <div class="text-sm text-gray-400">S&P 500</div>
            </div>
            <div class="text-center p-4 bg-gray-700/30 rounded-lg">
              <div class="text-2xl font-bold text-red-400">-0.8%</div>
              <div class="text-sm text-gray-400">NASDAQ</div>
            </div>
          </div>

          <div class="space-y-3">
            <div v-for="stock in topStocks" :key="stock.ticker" class="flex items-center justify-between p-3 bg-gray-700/30 rounded-lg hover:bg-gray-700/50 transition-all duration-200 cursor-pointer">
              <div>
                <div class="font-bold">{{ stock.ticker }}</div>
                <div class="text-sm text-gray-400">{{ stock.name }}</div>
              </div>
              <div class="text-right">
                <div class="font-bold">${{ stock.price }}</div>
                <div :class="stock.change > 0 ? 'text-green-400' : 'text-red-400'" class="text-sm">
                  {{ stock.change > 0 ? '+' : '' }}{{ stock.change }}%
                </div>
              </div>
            </div>
          </div>

          <button class="w-full mt-6 py-3 bg-blue-500/20 border border-blue-500/30 text-blue-400 rounded-lg hover:bg-blue-500/30 transition-all duration-300 font-medium">
            View All Markets
          </button>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="px-8 py-16 bg-gray-900/50">
      <div class="max-w-6xl mx-auto">
        <h2 class="text-4xl font-bold text-center mb-12 bg-gradient-to-r from-white to-gray-300 bg-clip-text text-transparent">
          Why Choose StockEvaluator?
        </h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="text-center p-6 bg-gray-800/30 rounded-xl border border-gray-700/30 hover:bg-gray-800/50 transition-all duration-300">
            <div class="w-16 h-16 bg-purple-500/20 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-bold mb-3">Real-time Analysis</h3>
            <p class="text-gray-400">Get instant sentiment analysis from Reddit communities and financial data</p>
          </div>
          <div class="text-center p-6 bg-gray-800/30 rounded-xl border border-gray-700/30 hover:bg-gray-800/50 transition-all duration-300">
            <div class="w-16 h-16 bg-green-500/20 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-bold mb-3">Smart Insights</h3>
            <p class="text-gray-400">AI-powered analysis of market trends and social sentiment</p>
          </div>
          <div class="text-center p-6 bg-gray-800/30 rounded-xl border border-gray-700/30 hover:bg-gray-800/50 transition-all duration-300">
            <div class="w-16 h-16 bg-blue-500/20 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a1 1 0 01-1 1H5a1 1 0 01-1-1V4z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-bold mb-3">Easy to Use</h3>
            <p class="text-gray-400">Simple interface for complex financial analysis and decision making</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// Remove any default margins/padding on body and html
onMounted(() => {
  document.body.style.margin = '0'
  document.body.style.padding = '0'
  document.documentElement.style.margin = '0'
  document.documentElement.style.padding = '0'
})

const router = useRouter()

const searchQuery = ref('')
const popularTickers = ['AAPL', 'TSLA', 'GME', 'AMC', 'NVDA', 'MSFT']

const redditTrends = ref([
  { ticker: 'GME', mentions: 847, sentiment: 15.2 },
  { ticker: 'TSLA', mentions: 623, sentiment: -3.8 },
  { ticker: 'AAPL', mentions: 456, sentiment: 8.7 },
  { ticker: 'AMC', mentions: 321, sentiment: 22.1 },
  { ticker: 'NVDA', mentions: 289, sentiment: 12.5 }
])

const topStocks = ref([
  { ticker: 'AAPL', name: 'Apple Inc.', price: '182.52', change: 2.1 },
  { ticker: 'MSFT', name: 'Microsoft Corp.', price: '378.85', change: 1.5 },
  { ticker: 'GOOGL', name: 'Alphabet Inc.', price: '138.21', change: -0.8 },
  { ticker: 'AMZN', name: 'Amazon.com Inc.', price: '153.40', change: 0.9 }
])

const navigateTo = (path: string) => {
  router.push(path)
}

const searchStock = () => {
  if (searchQuery.value.trim()) {
    // Navigate to search results or trigger search
    console.log('Searching for:', searchQuery.value)
    // router.push(`/search?q=${searchQuery.value}`)
  }
}

const quickSearch = (ticker: string) => {
  searchQuery.value = ticker
  searchStock()
}
</script>
