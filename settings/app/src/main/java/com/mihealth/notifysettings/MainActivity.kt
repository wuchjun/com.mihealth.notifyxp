package com.mihealth.notifysettings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mihealth.notifysettings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AppAdapter
    private val appList = mutableListOf<AppInfo>()
    private var selectedPackages = mutableSetOf<String>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateStatus()
        loadApps()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSaveButton()
        updateStatus()
        loadApps()
    }

    private fun setupRecyclerView() {
        adapter = AppAdapter { app, checked ->
            if (checked) {
                selectedPackages.add(app.packageName)
            } else {
                selectedPackages.remove(app.packageName)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            saveAndRestart()
        }
    }

    private fun updateStatus() {
        val hasPermission = PreferencesHelper.hasPermission(this)
        if (hasPermission) {
            binding.statusText.text = "已获取权限，可以修改通知设置"
        } else {
            binding.statusText.text = getString(R.string.no_permission)
        }
    }

    private fun loadApps() {
        if (!PreferencesHelper.hasPermission(this)) {
            requestPermission()
            return
        }

        val pm = packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != packageName }
            .mapNotNull { appInfo ->
                try {
                    AppInfo(
                        packageName = appInfo.packageName,
                        appName = pm.getApplicationLabel(appInfo).toString(),
                        icon = pm.getApplicationIcon(appInfo.packageName)
                    )
                } catch (e: Exception) {
                    null
                }
            }
            .sortedBy { it.appName }

        val whiteList = PreferencesHelper.getNotificationWhiteList(this)
        selectedPackages.clear()
        selectedPackages.addAll(whiteList)

        appList.clear()
        appList.addAll(installedApps.map {
            it.copy(selected = selectedPackages.contains(it.packageName))
        })

        adapter.submitList(appList.toList())
    }

    private fun requestPermission() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${PreferencesHelper.TARGET_PACKAGE}")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "请手动在设置中授权: ${PreferencesHelper.TARGET_PACKAGE}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveAndRestart() {
        val success = PreferencesHelper.setNotificationWhiteList(this, selectedPackages)
        if (success) {
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
        }
    }
}
