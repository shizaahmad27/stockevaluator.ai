import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTicker } from '@/api/generated/reddit-controller/reddit-controller'

export function useTickerSearch() {
  const router = useRouter()
  const searchLoading = ref(false)
  const searchError = ref<string | null>(null)

  const searchTicker = async (tickerSymbol: string): Promise<boolean> => {
    if (!tickerSymbol.trim()) {
      searchError.value = 'Please enter a ticker symbol'
      return false
    }

    try {
      searchLoading.value = true
      searchError.value = null

      const ticker = tickerSymbol.toUpperCase().trim()

      // Try to get ticker data from our Reddit API
      const data = await getTicker(ticker)

      if (data && data.symbol) {
        // Ticker found, navigate to ticker page
        await router.push(`/ticker/${ticker}`)
        return true
      } else {
        searchError.value = `No Reddit data found for ${ticker}`
        return false
      }
    } catch (error: any) {
      console.error('Search error:', error)

      if (error.response?.status === 404) {
        searchError.value = `Ticker ${tickerSymbol.toUpperCase()} not found in Reddit data`
      } else if (error.response?.status >= 500) {
        searchError.value = 'Server error. Please try again later.'
      } else {
        searchError.value = 'Search failed. Please check your connection and try again.'
      }
      return false
    } finally {
      searchLoading.value = false
    }
  }

  const navigateToTicker = (ticker: string) => {
    router.push(`/ticker/${ticker}`)
  }

  const clearError = () => {
    searchError.value = null
  }

  return {
    searchLoading,
    searchError,
    searchTicker,
    navigateToTicker,
    clearError
  }
}
