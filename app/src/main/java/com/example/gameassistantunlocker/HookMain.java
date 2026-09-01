package com.example.gameassistantunlocker;

import java.util.List;
import android.app.ActivityManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMain implements IXposedHookLoadPackage {

    private static final String TARGET_PKG = "com.vng.pubgmobile";
    private static final String SPOOF_PKG = "com.tencent.tmgp.pubgmhd";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.oplus.games")) {
            return;
        }

        XposedBridge.log("ColorOS Voice Unlocker: Hooking com.oplus.games deep APIs");

        // 1. Hook PackageManager getPackageInfo & getApplicationInfo
        hookPackageManager(lpparam.classLoader);

        // 2. Hook ActivityManager getRunningAppProcesses (Đánh tráo tên tiến trình đang chạy)
        hookActivityManager(lpparam.classLoader);
    }

    private void hookPackageManager(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager", classLoader,
                "getPackageInfo", String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (TARGET_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOF_PKG;
                        }
                    }
                }
            );

            XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager", classLoader,
                "getApplicationInfo", String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (TARGET_PKG.equals(param.args[0])) {
                            param.args[0] = SPOOF_PKG;
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("PM Hook Error: " + t.getMessage());
        }
    }

    private void hookActivityManager(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.ActivityManager", classLoader,
                "getRunningAppProcesses",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        List<ActivityManager.RunningAppProcessInfo> processes = 
                            (List<ActivityManager.RunningAppProcessInfo>) param.getResult();
                        if (processes != null) {
                            for (ActivityManager.RunningAppProcessInfo info : processes) {
                                if (TARGET_PKG.equals(info.processName)) {
                                    info.processName = SPOOF_PKG;
                                }
                                if (info.pkgList != null) {
                                    for (int i = 0; i < info.pkgList.length; i++) {
                                        if (TARGET_PKG.equals(info.pkgList[i])) {
                                            info.pkgList[i] = SPOOF_PKG;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("AM Hook Error: " + t.getMessage());
        }
    }
}
