import { ref } from 'vue'

    export type ToastProps = {
      title?: string
      description?: string
      variant?: 'default' | 'destructive'
    }

    // Create a separate type that extends ToastProps and includes id
    export interface Toast extends ToastProps {
      id: number
    }

    const toasts = ref<Toast[]>([])

    export function useToast() {
      const toast = (props: ToastProps) => {
        const id = Date.now()
        toasts.value.push({ ...props, id })

        // Auto remove toast after 5 seconds
        setTimeout(() => {
          toasts.value = toasts.value.filter(t => t.id !== id)
        }, 5000)
      }

      return {
        toast,
        toasts
      }
    }
