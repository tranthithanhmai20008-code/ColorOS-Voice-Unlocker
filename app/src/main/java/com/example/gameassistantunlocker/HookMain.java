package com.example.gameassistantunlocker;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMain implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.oplus.games")) {
            return;
        }

        XposedBridge.log("ColorOS Voice Unlocker: Hooking com.oplus.games");

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
                        if ("com.vng.pubgmobile".equals(pkgName)) {
                            param.args[0] = "com.tencent.tmgp.pubgmhd";
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("ColorOS Voice Unlocker Error: " + t.getMessage());
        }
    }
}
