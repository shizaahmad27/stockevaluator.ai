<script setup lang="ts">
import { ref } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { User, Mail } from 'lucide-vue-next'
import { useToast } from '@/components/ui/toast/use-toast'
import { useRegister } from '@/api/generated/authentication/authentication'
import type { RegisterResponse, RegisterRequest } from '@/api/generated/model'
import { useRouter } from 'vue-router'

import { Button } from '@/components/ui/button'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import PasswordInput from '@/components/auth/PasswordInput.vue'

const router = useRouter()

// Schema for the registration form
const rawSchema = z
    .object({
      firstName: z.string({ required_error: 'Firstname is required' }).min(5, 'Firstname must be minimum 5 letter'),
      lastName: z.string({ required_error: 'Lastname is required' }).min(5, 'Lastname must be minimum 5 letter'),
      email: z.string({ required_error: 'Email is required' }).email('Invalid email').min(5, 'Email must be minimum 5 letters'),
      password: z
          .string({ required_error: 'Password is required' })
          .min(8, 'Password must be minimum 8 letters')
          .regex(/[A-Z]/, 'Must contain at least one uppercase letter')
          .regex(/[a-z]/, 'Must contain at least one lowercase letter')
          .regex(/[0-9]/, 'Must contain at least one number')
          .regex(/[^A-Za-z0-9]/, 'Must contain at least one special character'),
      confirmPassword: z.string({ required_error: 'Confirm password is required' }),
      acceptedPrivacyPolicy: z.literal(true, {
        errorMap: () => ({ message: 'You must accept the privacy policy' }),
      }),
    })
    .refine((data) => data.password === data.confirmPassword, {
      message: 'Passwords do not match',
      path: ['confirmPassword'],
    })

const { handleSubmit, meta } = useForm({
  validationSchema: toTypedSchema(rawSchema),
})

// Get auth store and toast
const { toast } = useToast()

// Use the generated register mutation
const { mutate: register, isPending: isLoading } = useRegister({
  mutation: {
    onSuccess: (response: RegisterResponse) => {
      toast({
        title: 'Success',
        description: 'Your account has been created. Please log in to continue.',
        variant: 'default',
      })
      router.push('/login')
    },
    onError: (error: unknown) => {
      toast({
        title: 'Registration Error',
        description: getErrorMessage(error as { response?: { data?: { message?: string }; status?: number } }),
        variant: 'destructive',
      })
    }
  }
})

// Function to parse error messages and provide specific user feedback
const getErrorMessage = (error: { response?: { data?: { message?: string }; status?: number } }) => {
  // Default message
  const message = 'Could not register. Please try again.';

  const errorMessage = error?.response?.data?.message || '';

  if (error?.response?.status === 429) {
    return 'Too many attempts. Please wait a while before trying again.';
  }

  if (error?.response?.status === 409) {
    return 'Email address is already registered. Please use a different email or try logging in.';
  }

  if (error?.response?.status === 500) {
    return 'A server error occurred. Please try again later.';
  }

  return errorMessage || message;
}

const onSubmit = handleSubmit(async (values) => {
  if (isLoading.value) return

  const { confirmPassword, acceptedPrivacyPolicy, ...registrationData } = values
  register({ data: registrationData })
})

</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 py-12">
    <div class="w-full max-w-md">
      <!-- Header with logo -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent mb-2">
          StockEvaluator
        </h1>
        <p class="text-gray-400">Create your account to get started</p>
      </div>

      <form
          @submit="onSubmit"
          class="bg-gray-800/40 backdrop-blur-sm border border-gray-700/50 rounded-2xl shadow-2xl p-8 space-y-6"
      >
        <h2 class="text-2xl font-bold text-center text-white mb-6">Sign Up</h2>

        <!-- First Name -->
        <FormField v-slot="{ componentField }" name="firstName">
          <FormItem>
            <FormLabel class="block text-sm font-medium text-gray-300 mb-2">First Name</FormLabel>
            <FormControl>
              <div class="relative">
                <User
                    class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5"
                />
                <Input
                    type="text"
                    placeholder="John"
                    class="w-full px-4 py-3 pl-11 bg-gray-700/50 border border-gray-600/50 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all duration-300"
                    v-bind="componentField"
                />
              </div>
            </FormControl>
            <FormMessage class="text-sm text-red-400 mt-1" />
          </FormItem>
        </FormField>

        <!-- Last Name -->
        <FormField v-slot="{ componentField }" name="lastName">
          <FormItem>
            <FormLabel class="block text-sm font-medium text-gray-300 mb-2">Last Name</FormLabel>
            <FormControl>
              <div class="relative">
                <User
                    class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5"
                />
                <Input
                    type="text"
                    placeholder="Doe"
                    class="w-full px-4 py-3 pl-11 bg-gray-700/50 border border-gray-600/50 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all duration-300"
                    v-bind="componentField"
                />
              </div>
            </FormControl>
            <FormMessage class="text-sm text-red-400 mt-1" />
          </FormItem>
        </FormField>

        <!-- Email -->
        <FormField v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel class="block text-sm font-medium text-gray-300 mb-2">Email</FormLabel>
            <FormControl>
              <div class="relative">
                <Mail
                    class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5"
                />
                <Input
                    type="email"
                    placeholder="name@example.com"
                    class="w-full px-4 py-3 pl-11 bg-gray-700/50 border border-gray-600/50 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all duration-300"
                    v-bind="componentField"
                />
              </div>
            </FormControl>
            <FormMessage class="text-sm text-red-400 mt-1" />
          </FormItem>
        </FormField>

        <!-- Password field using component -->
        <FormField v-slot="{ componentField }" name="password">
          <FormItem>
            <FormLabel class="block text-sm font-medium text-gray-300 mb-2">Password</FormLabel>
            <FormControl>
              <div class="relative">
                <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                </svg>
                <Input
                    type="password"
                    placeholder="********"
                    class="w-full px-4 py-3 pl-11 bg-gray-700/50 border border-gray-600/50 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all duration-300"
                    v-bind="componentField"
                />
              </div>
            </FormControl>
            <FormMessage class="text-sm text-red-400 mt-1" />
          </FormItem>
        </FormField>

        <!-- Confirm Password -->
        <FormField v-slot="{ componentField }" name="confirmPassword">
          <FormItem>
            <FormLabel class="block text-sm font-medium text-gray-300 mb-2">Confirm Password</FormLabel>
            <FormControl>
              <div class="relative">
                <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                </svg>
                <Input
                    type="password"
                    placeholder="********"
                    class="w-full px-4 py-3 pl-11 bg-gray-700/50 border border-gray-600/50 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 transition-all duration-300"
                    v-bind="componentField"
                />
              </div>
            </FormControl>
            <FormMessage class="text-sm text-red-400 mt-1" />
          </FormItem>
        </FormField>

        <!-- Accept Privacy Policy -->
        <FormField v-slot="{ value, handleChange }" name="acceptedPrivacyPolicy" type="checkbox">
          <FormItem>
            <div class="flex items-start space-x-3">
              <FormControl>
                <input
                    type="checkbox"
                    :checked="value"
                    @change="handleChange(($event.target as HTMLInputElement)?.checked ?? false)"
                    @keydown.enter.prevent="handleChange(!value)"
                    @keydown.space.prevent="handleChange(!value)"
                    id="acceptedPrivacyPolicy"
                    class="mt-1 h-4 w-4 rounded border-gray-600 bg-gray-700 text-blue-600 focus:ring-blue-500 focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800"
                    tabindex="0"
                    role="checkbox"
                    :aria-checked="value"
                    aria-label="Accept privacy policy"
                />
              </FormControl>
              <label for="acceptedPrivacyPolicy" class="text-sm text-gray-300 cursor-pointer select-none">
                I accept the
                <router-link
                    to="/privacy"
                    class="text-blue-400 hover:text-blue-300 hover:underline focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-gray-800 rounded-sm px-1 transition-colors duration-200"
                    tabindex="0"
                    aria-label="Open privacy policy"
                >
                  privacy policy
                </router-link>
              </label>
            </div>
            <FormMessage class="text-sm text-red-400 mt-1" />
          </FormItem>
        </FormField>

        <!-- Submit button -->
        <Button
            type="submit"
            :disabled="!meta.valid || isLoading"
            class="w-full bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 disabled:from-gray-600 disabled:to-gray-700 disabled:cursor-not-allowed text-white py-3 rounded-lg text-sm font-medium transition-all duration-300 transform hover:scale-105 disabled:hover:scale-100 shadow-lg hover:shadow-xl"
        >
          <template v-if="isLoading">
            <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Creating account...
          </template>
          <template v-else>Sign Up</template>
        </Button>

        <!-- Login link -->
        <div class="text-sm text-center pt-4 border-t border-gray-700/50">
          <span class="text-gray-400">Already have an account?</span>
          <router-link
              to="/login"
              class="ml-2 text-blue-400 hover:text-blue-300 hover:underline transition-colors duration-200 font-medium"
          >
            Log in
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>
