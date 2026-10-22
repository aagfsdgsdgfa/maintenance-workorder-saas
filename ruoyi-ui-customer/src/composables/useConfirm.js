/**
 * 二次确认 Composable
 *
 * 包装 ElMessageBox.confirm，返回 Promise<bool>，避免业务页到处写 try/catch。
 *
 * 用法：
 *   const confirm = useConfirm()
 *   if (await confirm({ title: '删除设备', message: '此操作不可撤销', type: 'warning' })) {
 *     await doDelete()
 *   }
 */
import { ElMessageBox } from 'element-plus'

export function useConfirm() {
  return function confirm({ title = '提示', message = '', type = 'warning', confirmText = '确定', cancelText = '取消' } = {}) {
    return ElMessageBox.confirm(message, title, {
      confirmButtonText: confirmText,
      cancelButtonText: cancelText,
      type,
      closeOnClickModal: false
    })
      .then(() => true)
      .catch(() => false)
  }
}