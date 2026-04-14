# MiHealthNotifyXP

Xposed 模块，让小米运动健康的通知设置页面显示所有已安装应用（包括系统应用）。

## 功能

- Hook `ApplicationPackageManager.queryIntentActivities()`
- 拦截 LAUNCHER 查询时，补充显示所有已安装包
- 支持显示无桌面入口的系统应用（如"设备互联"）

## 作用域

仅 `com.mi.health`（小米运动健康）

## 构建

```bash
./gradlew assembleDebug
```

## 安装

1. 安装 APK
2. LSPosed 中启用模块
3. 设置作用域为 `com.mi.health`
4. 重启目标 App

## 技术

- libxposed API 101
- 目标：显示完整的应用列表用于通知白名单配置
