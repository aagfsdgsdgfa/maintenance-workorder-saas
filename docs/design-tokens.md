# 智维云服 - 设计令牌（Design Tokens）

> 本文档是 PC 端与移动端共享的设计令牌数值表。所有运行时主题切换、亮/暗色模式、品牌色替换都基于此表扩展。

**Token 文件**：
- PC：`ruoyi-ui-customer/src/styles/customer.css` (`:root` CSS 变量) + `tokens.scss` (SCSS 编译期变量)
- 移动端：`maintenance-app/uni.scss` (SCSS 变量，编译到 `wxss` / `css`)

---

## 1. 品牌色（Brand Colors）

| 名称 | 数值 | CSS 变量 | SCSS 变量 | 用途 |
|---|---|---|---|---|
| primary-deep | `#1B4B8E` | `--primary` | `$color-primary-deep` | PC 端主品牌色 |
| primary | `#2563EB` | `--primary-light` | `$color-primary` | PC + 移动端标准主色 |
| primary-dark | `#1d4ed8` | `--primary-dark` | `$color-primary-dark` | 主色按压态 |
| primary-light-bg | `#eff6ff` | `--bg-soft` (PC) / `$color-primary-light-bg` (移动) | — | 主色淡背景 |
| accent | `#00C9A7` | `--accent` | `$color-accent` | 强调色（青绿） |
| accent-light | `#07D9C4` | `--accent-light` | — | 强调色浅态 |

## 2. 语义色（Semantic Colors）

| 语义 | 数值 | CSS 变量 | SCSS 变量 | 应用场景 |
|---|---|---|---|---|
| success | `#10b981` | `--success` / `--el-color-success` | `$color-success` / `$uni-color-success` | 成功 / 完成 |
| warning | `#f59e0b` | `--warning` / `--el-color-warning` | `$color-warning` / `$uni-color-warning` | 警告 / 处理中 |
| danger | `#ef4444` | `--danger` / `--el-color-danger` | `$color-error` / `$uni-color-error` | 错误 / 故障 |
| info | `#6366f1` (PC) / `#6b7280` (移动) | `--info` / `--el-color-info` | `$uni-text-color-grey` | 信息 / 次要 |

> 注：移动端 `$uni-color-info` 未显式定义，复用文字灰。

## 3. 文字色（Text Colors）

| 名称 | 数值 | CSS 变量 | SCSS 变量 |
|---|---|---|---|
| title | `#0f172a` / `#1f2937` | `--text-primary` | `$uni-text-color` |
| body | `#475569` / `#4b5563` | `--text-secondary` | `$color-text-body` |
| hint | `#94a3b8` / `#9ca3af` | `--text-muted` | `$uni-text-color-placeholder` |
| inverse | `#ffffff` | `--text-inverse` | `$uni-text-color-inverse` |

## 4. 背景色（Background Colors）

| 名称 | 数值 | CSS 变量 | SCSS 变量 |
|---|---|---|---|
| page | `#f0f4f8` (PC) / `#f8fafc` (移动) | `--bg-page` | `$color-bg-page` |
| card | `#ffffff` | `--bg-card` | `$color-bg-card` |
| hover | `#f1f5f9` | `--bg-hover` | `$color-bg-hover` |
| mask | `rgba(0, 0, 0, 0.5)` | — | `$uni-bg-color-mask` |

## 5. 边框色（Border Colors）

| 名称 | 数值 | CSS 变量 | SCSS 变量 |
|---|---|---|---|
| base | `#e2e8f0` / `#e5e7eb` | `--border` | `$color-border` / `$uni-border-color` |
| light | `#f1f5f9` / `#f3f4f6` | `--border-light` | `$color-border-light` |

## 6. 字号阶梯（Font Size Scale）

| Token | PC (px) | 移动 (rpx) | CSS 变量 | SCSS 变量 |
|---|---|---|---|---|
| xs | 12 | 22 | `--fs-xs` | `$font-size-xs` |
| sm | 13 | 24 | `--fs-sm` | `$font-size-sm` |
| base | 14 | 28 | `--fs-base` / `--el-font-size-base` | `$font-size-base` / `$uni-font-size-base` |
| md | 15 | — | `--fs-md` | — |
| lg | 16 | 32 | `--fs-lg` | `$font-size-lg` |
| xl | 18 | 36 | `--fs-xl` | `$font-size-xl` |
| 2xl | 20 | — | `--fs-2xl` | — |
| 3xl | 24 | 44 | `--fs-3xl` | `$font-size-xxl` |
| 4xl | 30 | 52 | `--fs-4xl` | `$font-size-big` |

> 移动端 rpx 基准 = 750（即 14rpx × 2 ≈ 1px）。PC 端 px 直用。

## 7. 圆角（Border Radius）

| Token | PC (px) | 移动 (rpx) | CSS 变量 | SCSS 变量 |
|---|---|---|---|---|
| sm | 6 | 8 | `--radius-sm` | `$radius-sm` / `$uni-border-radius-sm` |
| md | 10 | 16 | `--radius-md` | `$radius-md` |
| lg | 16 | 24 | `--radius-lg` / `--el-border-radius-base` | `$radius-lg` / `$uni-border-radius-lg` |
| xl | 20 | 32 | `--radius-xl` | `$radius-xl` |
| full | 9999 | 9999 | `--radius-full` | `$radius-full` / `$uni-border-radius-circle` |

## 8. 间距阶梯（Spacing Scale）

| Token | PC (px) | 移动 (rpx) | CSS 变量 | SCSS 变量 |
|---|---|---|---|---|
| xs | 4 | — | `--sp-xs` | — |
| sm | 8 | 8 | `--sp-sm` | `$spacing-xs` |
| md | 12 | 12 | `--sp-md` | `$spacing-sm` |
| base | 16 | 16 | `--sp-base` | `$spacing-md` |
| lg | 20 | 24 | `--sp-lg` | `$spacing-lg` |
| xl | 24 | 32 | `--sp-xl` | `$spacing-xl` |
| 2xl | 32 | 48 | `--sp-2xl` | `$spacing-xxl` |
| 3xl | 40 | — | `--sp-3xl` | — |

## 9. 阴影（Shadow）

| Token | PC | 移动 |
|---|---|---|
| sm | `0 1px 4px rgba(27, 75, 142, 0.06)` | `0 1rpx 2rpx rgba(0, 0, 0, 0.05)` |
| card | `0 2px 12px rgba(27, 75, 142, 0.08)` | `0 2rpx 16rpx rgba(37, 99, 235, 0.08)` |
| md | `0 4px 16px rgba(27, 75, 142, 0.10)` | `0 4rpx 12rpx rgba(0, 0, 0, 0.08)` |
| lg | `0 8px 32px rgba(27, 75, 142, 0.14)` | `0 8rpx 24rpx rgba(0, 0, 0, 0.12)` |
| glow | `0 0 20px rgba(37, 99, 235, 0.2)` | — |

## 10. Z-index 层级

| 层 | 数值 | CSS 变量 | SCSS 变量 |
|---|---|---|---|
| dropdown | 1000 | `--zi-dropdown` | `$zi-dropdown` |
| sticky | 1020 | `--zi-sticky` | `$zi-sticky` |
| fixed | 1030 | `--zi-fixed` | `$zi-fixed` |
| modal-backdrop | 1040 | `--zi-modal-backdrop` | `$zi-modal-backdrop` |
| modal | 1050 | `--zi-modal` | `$zi-modal` |
| popover | 1060 | `--zi-popover` | `$zi-popover` |
| tooltip | 1070 | `--zi-tooltip` | `$zi-tooltip` |
| toast | 1080 | `--zi-toast` | `$zi-toast` |

## 11. 动效（Motion）

| Token | 时长 | CSS 变量 | SCSS 变量 |
|---|---|---|---|
| fast | 150ms | `--motion-fast` | `$motion-fast` |
| base | 250ms | `--motion-base` | `$motion-base` |
| slow | 400ms | `--motion-slow` | `$motion-slow` |
| ease-out | `cubic-bezier(0.16, 1, 0.3, 1)` | `--ease-out` | `$ease-out` |
| ease-in-out | `cubic-bezier(0.4, 0, 0.2, 1)` | `--ease-in-out` | `$ease-in-out` |

## 12. 媒体查询断点（仅 PC）

| 断点 | 数值 | SCSS 变量 | 用途 |
|---|---|---|---|
| sm | 640px | `$bp-sm` | 手机横屏 |
| md | 768px | `$bp-md` | 平板竖屏 |
| lg | 1024px | `$bp-lg` | 平板横屏 |
| xl | 1280px | `$bp-xl` | 笔电（PC 主流） |
| 2xl | 1536px | `$bp-2xl` | 中型桌面 |
| 3xl | 1920px | `$bp-3xl` | 大屏桌面 |

配套 mixin：`media-down($bp)` / `media-up($bp)` / `media-between($min, $max)`。

## 13. Element Plus 主题映射

PC `customer.css` `:root` 已将 EP 根 token 映射到品牌色：

```css
--el-color-primary: #2563EB;
--el-color-success: #10b981;
--el-color-warning: #f59e0b;
--el-color-danger: #ef4444;
--el-color-info: #6366f1;
--el-border-radius-base: 8px;
--el-border-radius-small: 6px;
--el-font-size-base: 14px;
```

效果：未在 `customer.css` 第 79-161 行显式覆盖的 EP 组件（如 Tag.success、Pagination、Radio）也会自动使用品牌色，不再泄露 EP 默认 `#409EFF`。

---

## 14. 使用约束

1. **新增 UI 一律使用 Token**：禁止在 `.vue` / `.scss` 中硬编码 `#2563EB`、`14px`、`16px`、`border-radius: 8px` 等数值，全部走变量。
2. **新增 Token 必须双向登记**：PC `customer.css` + 移动端 `uni.scss` 同步添加，并在本表登记。
3. **断点只能用 SCSS 变量**：`@media` 内 CSS 变量不生效，必须 `@use` 引入 `tokens.scss` 后用 `$bp-xl`。
4. **响应式**：PC 优先用 `$bp-xl`（1280px）+ `$bp-3xl`（1920px）两档；移动端无需断点（uni-app 自适应）。