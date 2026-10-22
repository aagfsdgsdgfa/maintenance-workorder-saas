/**
 * 通用节流工具
 *
 * @param {Function} fn - 待节流函数
 * @param {number} wait - 间隔 ms
 * @returns {Function} 节流后的函数
 */
export function throttle(fn, wait = 200) {
  let last = 0
  let timer = null
  return function (...args) {
    const now = Date.now()
    const remaining = wait - (now - last)
    if (remaining <= 0) {
      if (timer) {
        clearTimeout(timer)
        timer = null
      }
      last = now
      fn.apply(this, args)
    } else if (!timer) {
      timer = setTimeout(() => {
        last = Date.now()
        timer = null
        fn.apply(this, args)
      }, remaining)
    }
  }
}

/**
 * 防抖工具（与节流区分：最后一次触发为准）
 *
 * @param {Function} fn - 待防抖函数
 * @param {number} wait - 延迟 ms
 * @returns {Function} 防抖后的函数
 */
export function debounce(fn, wait = 200) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), wait)
  }
}