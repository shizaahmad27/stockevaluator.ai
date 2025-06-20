import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import RedditAnalysisView from '@/views/RedditAnalysisView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignupView.vue'),
    },
    {
      path: '/reddit-analysis',
      name: 'RedditAnalysis',
      component: RedditAnalysisView,
      meta: {
        title: 'Reddit Analysis Dashboard - StockEvaluator'
      }
    },
    {
      path: '/ticker/:symbol',
      name: 'TickerDetail',
      component: () => import('../views/TickerDetailView.vue'),
      meta: {
        title: 'Ticker Analysis - StockEvaluator'
      }
    },

  ],
})

export default router
