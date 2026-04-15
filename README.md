# MiHealthNotifyXP

让小米运动健康的通知设置页面显示所有已安装应用，包括无桌面入口的系统应用（如"设备互联"）。

## 功能

- Hook `ApplicationPackageManager.queryIntentActivities()`
- 补充显示所有已安装包
- 支持显示"设备互联"等无 LAUNCHER 入口的系统应用

## 作用域

仅 `com.mi.health`（小米运动健康）

## 安装

1. 下载 APK（见 [Releases](https://github.com/wuchjun/com.mihealth.notifyxp/releases)）
2. 安装并启用模块
3. 设置作用域为 `com.mi.health`
4. 重启目标 App

## 构建

```bash
./gradlew assembleRelease
```

## 技术

- libxposed API 101

## 许可证

[MIT License](LICENSE)
