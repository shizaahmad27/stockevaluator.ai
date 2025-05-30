<script setup lang="ts">
import { computed } from 'vue'
import { Mail, AlertCircle } from 'lucide-vue-next'
import { useRoute, useRouter } from 'vue-router'
import { useLogin } from '@/api/generated/authentication/authentication'
import type { LoginResponse, LoginRequest } from '@/api/generated/model'
import { useToast } from '@/components/ui/toast/use-toast'

import { Button } from '@/components/ui/button'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import PasswordInput from '@/components/auth/PasswordInput.vue'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'

const route = useRoute()
const router = useRouter()
const redirectPath = computed(() => (route.query.redirect as string) || '/dashboard')
const { toast } = useToast()

// Login form schema
const loginSchema = z.object({
  email: z.string().email('Invalid email'),
  password: z.string().min(1, 'Password is required'),
})

const { handleSubmit, meta } = useForm({
  validationSchema: toTypedSchema(loginSchema),
})

// Use the generated login mutation
const { mutate: login, isPending: isLoading } = useLogin({
  mutation: {
    onSuccess: (response: LoginResponse) => {
      toast({
        title: 'Success',
        description: 'You are now logged in',
        variant: 'default',
      })
      router.push('/')
    },
    onError: (error: unknown) => {
      toast({
        title: 'Login Error',
        description: getErrorMessage(error as { response?: { data?: { message?: string }; status?: number } }),
        variant: 'destructive',
      })
    }
  }
})

// Function to parse error messages and provide specific user feedback
const getErrorMessage = (error: { response?: { data?: { message?: string }; status?: number } }) => {
  // Default message
  const message = 'Could not log in. Please try again.';

  // Extract error message from response if available
  const errorMessage = error?.response?.data?.message || '';

  if (error?.response?.status === 401) {
    return 'Incorrect email or password. Please try again.';
  }

  if (error?.response?.status === 429) {
    return 'Too many attempts. Please wait a while before trying again.';
  }

  if (error?.response?.status === 500) {
    return 'A server error occurred. Please try again later.';
  }

  return errorMessage || message;
}

const onSubmit = handleSubmit(async (values) => {
  if (isLoading.value) return
  login({ data: values })
})
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-white">
    <form
        @submit="onSubmit"
        class="w-full max-w-sm p-8 border border-gray-200 rounded-xl shadow-sm bg-white space-y-5"
    >
      <h1 class="text-3xl font-bold text-center">Login</h1>
      
      <!-- Email Field -->
      <FormField v-slot="{ componentField }" name="email">
        <FormItem>
          <FormLabel class="block text-sm font-medium text-gray-700 mb-1">Email</FormLabel>
          <FormControl>
            <div class="relative">
              <Mail
                  class="absolute left-2 top-1/2 transform -translate-y-1/2 text-gray-500 h-4 w-4"
              />
              <Input
                  type="email"
                  placeholder="name@example.com"
                  class="w-full px-3 py-2 pl-8 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  v-bind="componentField"
                  autocomplete="username"
              />
            </div>
          </FormControl>
          <FormMessage class="text-sm text-red-500" />
        </FormItem>
      </FormField>

      <!-- Password Field using component -->
      <FormField v-slot="{ componentField }" name="password">
        <PasswordInput
            name="password"
            label="Password"
            placeholder="********"
            :componentField="componentField"
            :showToggle="true"
            :showIcon="true"
            autocomplete="current-password"
        />
      </FormField>

      <!-- Submit Button -->
      <Button
          type="submit"
          :disabled="!meta.valid || isLoading"
          class="w-full hover:cursor-pointer bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white py-2 rounded-md text-sm font-medium"
      >
        <template v-if="isLoading">Logging in...</template>
        <template v-else>Log in</template>
      </Button>

      <!-- Bottom links -->
      <div class="text-sm text-center space-y-2">
        <div>
          <span class="text-gray-600">Don't have an account?</span>
          <a href="/signup" class="ml-1 text-blue-600 hover:underline">Sign up</a>
        </div>

        <a href="/forgot-password" class="block text-blue-500 hover:underline">
          Forgot your password?
        </a>
      </div>
    </form>
  </div>
</template>
