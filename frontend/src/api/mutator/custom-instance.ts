import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'

const customInstance = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add a request interceptor for authentication
customInstance.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Add a response interceptor for error handling and token refresh
customInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // If the error is 401 and we haven't tried to refresh the token yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) {
          throw new Error('No refresh token available')
        }

        // Call the refresh token endpoint
        const response = await axios.post('http://localhost:8080/auth/refresh', {
          refreshToken
        })

        const { accessToken, refreshToken: newRefreshToken } = response.data

        // Update tokens in localStorage
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', newRefreshToken)

        // Update the original request with the new access token
        originalRequest.headers.Authorization = `Bearer ${accessToken}`

        // Retry the original request
        return customInstance(originalRequest)
      } catch (refreshError) {
        // If refresh token fails, clear tokens and redirect to login
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)

// Export the custom instance function that Orval expects
export const customInstanceFn = async <T>({
  url,
  method,
  params,
  data,
  headers,
}: AxiosRequestConfig): Promise<T> => {
  const response = await customInstance({
    url,
    method,
    params,
    data,
    headers,
  })
  return response.data
}

// Also export the instance itself in case it's needed
export { customInstance } 