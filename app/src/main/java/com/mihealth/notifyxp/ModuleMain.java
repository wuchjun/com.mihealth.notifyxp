package com.mihealth.notifyxp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;
import io.github.libxposed.api.XposedInterface.Invoker;

public class ModuleMain extends XposedModule {
    private static final String TAG = "MiHealthNotifyXP";
    private static final String TARGET_PACKAGE = "com.mi.health";
    private static volatile boolean sHooked = false;

    @Override
    public void onPackageLoaded(XposedModuleInterface.PackageLoadedParam param) {
        if (!param.getPackageName().equals(TARGET_PACKAGE) || sHooked) {
            return;
        }
        sHooked = true;
        hookAppPackageManager(param.getDefaultClassLoader());
    }

    private void hookAppPackageManager(ClassLoader classLoader) {
        try {
            Class<?> apmClass = classLoader.loadClass("android.app.ApplicationPackageManager");

            Context systemContext = getSystemContext(classLoader);

            for (Method m : apmClass.getDeclaredMethods()) {
                if (m.getName().equals("queryIntentActivities")) {
                    final Method targetMethod = m;
                    final Context ctx = systemContext;

                    hook(m).intercept(chain -> {
                        Intent intent = (Intent) chain.getArgs().get(0);
                        if (!isLauncherQuery(intent)) {
                            return chain.proceed();
                        }

                        // 获取LAUNCHER应用
                        Intent launcherIntent = new Intent(Intent.ACTION_MAIN)
                            .addCategory(Intent.CATEGORY_LAUNCHER);
                        Object secondArg = chain.getArgs().size() > 1 ? chain.getArgs().get(1) : 0;

                        Object launcherResult = getInvoker(targetMethod)
                            .setType(Invoker.Type.ORIGIN)
                            .invoke(chain.getThisObject(), launcherIntent, secondArg);

                        Map<String, ResolveInfo> uniqueApps = new HashMap<>();

                        if (launcherResult instanceof List) {
                            for (Object item : (List<?>) launcherResult) {
                                if (item instanceof ResolveInfo) {
                                    ResolveInfo info = (ResolveInfo) item;
                                    if (info.activityInfo != null) {
                                        uniqueApps.put(info.activityInfo.packageName, info);
                                    }
                                }
                            }
                        }

                        // 补充所有已安装包
                        if (ctx != null) {
                            PackageManager pm = ctx.getPackageManager();
                            List<PackageInfo> allPackages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);

                            for (PackageInfo pkgInfo : allPackages) {
                                String pkgName = pkgInfo.packageName;
                                if (!uniqueApps.containsKey(pkgName)
                                    && pkgInfo.activities != null
                                    && pkgInfo.activities.length > 0) {

                                    ResolveInfo ri = new ResolveInfo();
                                    ri.activityInfo = pkgInfo.activities[0];
                                    ri.activityInfo.applicationInfo = pkgInfo.applicationInfo;
                                    uniqueApps.put(pkgName, ri);
                                }
                            }
                        }

                        return new ArrayList<>(uniqueApps.values());
                    });
                    break;
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "Hook failed: " + e);
        }
    }

    private Context getSystemContext(ClassLoader cl) {
        try {
            Class<?> atClass = cl.loadClass("android.app.ActivityThread");
            Object at = atClass.getMethod("currentActivityThread").invoke(null);
            return (Context) atClass.getMethod("getSystemContext").invoke(at);
        } catch (Throwable e) {
            return null;
        }
    }

    private boolean isLauncherQuery(Intent intent) {
        return intent != null
            && Intent.ACTION_MAIN.equals(intent.getAction())
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER);
    }
}
