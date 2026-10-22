# 待 user 确认事项：移动端 manifest.json 权限审查

> 阶段四（移动端清理）发现的可疑权限，等待用户确认是否真有业务需要。
> 位置：`maintenance-app/manifest.json` → `app-plus.distribute.android.permissions`

## 待确认权限

| 权限 | 候选用途 | 状态 |
|------|----------|------|
| `android.permission.READ_LOGS` | 调试期查看系统日志 | ❓ 待确认 |
| `android.permission.WRITE_SETTINGS` | 修改系统设置（亮度、音量等） | ❓ 待确认 |
| `android.permission.READ_PHONE_STATE` | 获取设备 IMEI/IMSI（设备绑定、风控） | ⚠️ 当前业务未明显使用 |
| `android.permission.GET_ACCOUNTS` | 读取账号列表（第三方登录关联） | ⚠️ 当前业务未明显使用 |
| `android.permission.MOUNT_UNMOUNT_FILESYSTEMS` | 挂载/卸载文件系统 | ❌ 极可能是 uni-app 模板默认带 |

## 已确认需要的权限

- `CHANGE_NETWORK_STATE` / `ACCESS_NETWORK_STATE` / `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE` — 网络状态
- `CAMERA` / `android.hardware.camera` / `autofocus` / `FLASHLIGHT` — 拍照与手电筒（巡检、故障拍照）
- `VIBRATE` — 消息震动
- `WAKE_LOCK` — 后台任务保活（GPS 打卡）

## 处理建议

1. 如果 READ_LOGS / WRITE_SETTINGS 没有具体业务场景，**直接删除**
2. 建议增加 `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`（GPS 打卡、巡检验证）— 当前未在 manifest 显式声明，靠 mp-weixin 的 `scope.userLocation` 是不够的
3. READ_PHONE_STATE / GET_ACCOUNTS 在 2026 年隐私合规收紧后，建议优先移除并迁移到 uni-app 提供的合规 API

## 处理方式

请 user 在审批阶段四（移动端清理）时一并答复上述权限去留。本阶段暂不动 manifest.json。
