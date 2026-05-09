/**
 * @file eslint.config.js
 * @description 前端 ESLint 設定 / Frontend ESLint configuration
 * @description_en Configures TypeScript, React Hooks, and Vite refresh linting rules
 * @description_zh 設定 TypeScript、React Hooks 與 Vite refresh 程式碼檢查規則
 */

import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import { defineConfig, globalIgnores } from 'eslint/config'

export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    rules: {
      // ========================================
      // React 19 相容規則 / React 19 Compatibility Rules
      // ========================================
      'react-hooks/set-state-in-effect': 'off',
      'react-hooks/refs': 'off',
      'react-hooks/purity': 'off',
    },
  },
])
