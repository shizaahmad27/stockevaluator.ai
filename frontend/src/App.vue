<script setup lang="ts">
import Footer from './layout/Footer.vue'
import { Toast } from './components/ui/toast'
</script>

<template>

  <router-view />
  <Toast />
  <Footer />

</template>

<style>
#app {
  font-family: Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>


