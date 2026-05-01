package com.mihealth.notifysettings

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferencesHelper {
    const val TARGET_PACKAGE = "com.mi.health"
    private const val NOTIFICATION_WHITE_LIST = "NOTIFICATION_APP_IN_WHITE_LIST"
    private const val NOTIFICATION_SPOTLIGHT_WHITE_LIST = "NOTIFICATION_SPOTLIGHT_APP_IN_WHITE_LIST"

    private val gson = Gson()

    /**
     * 获取小米运动健康的 SharedPreferences
     */
    fun getMiHealthPrefs(context: Context): SharedPreferences? {
        return try {
            val pkgContext = context.createPackageContext(TARGET_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
            pkgContext.getSharedPreferences("cloud_pref", Context.MODE_PRIVATE)
        } catch (e: PackageManager.NameNotFoundException) {
            android.util.Log.e("PrefsHelper", "Package not found: $TARGET_PACKAGE")
            null
        } catch (e: Exception) {
            android.util.Log.e("PrefsHelper", "Error: ${e.message}")
            null
        }
    }

    /**
     * 读取通知白名单
     */
    fun getNotificationWhiteList(context: Context): Set<String> {
        val prefs = getMiHealthPrefs(context) ?: return emptySet()
        val json = prefs.getString(NOTIFICATION_WHITE_LIST, null) ?: return emptySet()
        return try {
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson(json, type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    /**
     * 写入通知白名单
     */
    fun setNotificationWhiteList(context: Context, packages: Set<String>): Boolean {
        val prefs = getMiHealthPrefs(context) ?: return false
        return try {
            val json = gson.toJson(packages)
            prefs.edit().putString(NOTIFICATION_WHITE_LIST, json).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 读取聚焦通知白名单
     */
    fun getSpotlightWhiteList(context: Context): Set<String> {
        val prefs = getMiHealthPrefs(context) ?: return emptySet()
        val json = prefs.getString(NOTIFICATION_SPOTLIGHT_WHITE_LIST, null) ?: return emptySet()
        return try {
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson(json, type) ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    /**
     * 写入聚焦通知白名单
     */
    fun setSpotlightWhiteList(context: Context, packages: Set<String>): Boolean {
        val prefs = getMiHealthPrefs(context) ?: return false
        return try {
            val json = gson.toJson(packages)
            prefs.edit().putString(NOTIFICATION_SPOTLIGHT_WHITE_LIST, json).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检查是否有权限访问
     */
    fun hasPermission(context: Context): Boolean {
        return getMiHealthPrefs(context) != null
    }
}
