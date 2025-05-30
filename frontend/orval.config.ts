import { defineConfig } from 'orval'

export default defineConfig({
  stockevaluator: {
    input: {
      target: 'http://localhost:8080/v3/api-docs',
    },
    output: {
      mode: 'tags-split',
      target: './src/api/generated',
      schemas: './src/api/generated/model',
      client: 'vue-query',
      prettier: true,
      override: {
        mutator: {
          path: './src/api/mutator/custom-instance.ts',
          name: 'customInstanceFn',
        },
        query: {
          useQuery: true,
          useInfinite: true,
          useInfiniteQueryParam: 'page',
          options: {
            staleTime: 10000,
          },
        },
      },
    },
    hooks: {
      afterAllFilesWrite: 'prettier --write',
    },
  },
}) 