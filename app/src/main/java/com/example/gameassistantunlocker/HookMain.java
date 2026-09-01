package com.example.gameassistantunlocker;

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

        XposedBridge.log("ColorOS Voice Unlocker: Hooking com.oplus.games");

        // 1. Hook getPackageInfo (Chuỗi tham số Package Name)
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager",
                lpparam.classLoader,
                "getPackageInfo",
                String.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String pkgName = (String) param.args[0];
                        if (TARGET_PKG.equals(pkgName)) {
                            param.args[0] = SPOOF_PKG;
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("Error hooking getPackageInfo: " + t.getMessage());
        }

        // 2. Hook getApplicationInfo
        try {
            XposedHelpers.findAndHookMethod(
                "android.app.ApplicationPackageManager",
                lpparam.classLoader,
                "getApplicationInfo",
                String.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String pkgName = (String) param.args[0];
                        if (TARGET_PKG.equals(pkgName)) {
                            param.args[0] = SPOOF_PKG;
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("Error hooking getApplicationInfo: " + t.getMessage());
        }
    }
}
