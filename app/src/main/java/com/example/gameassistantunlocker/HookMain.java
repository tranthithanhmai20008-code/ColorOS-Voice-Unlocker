package com.example.gameassistantunlocker;

import java.lang.reflect.Method;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMain implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.oplus.games")) {
            return;
        }

        XposedBridge.log("ColorOS Voice Unlocker: Hooking VIP Checkers & NetPanel");

        // Hook truc tiep lop VipOfflineModel va VoiceContentDataHelper
        try {
            Class<?> vipModelClass = XposedHelpers.findClassIfExists(
                "business.module.netpanel.ui.vm.VipOfflineModel", 
                lpparam.classLoader
            );

            if (vipModelClass != null) {
                for (Method method : vipModelClass.getDeclaredMethods()) {
                    if (method.getReturnType() == boolean.class) {
                        XposedBridge.log("Hooking VipOfflineModel method: " + method.getName());
                        XposedHelpers.findAndHookMethod(
                            vipModelClass, 
                            method.getName(), 
                            method.getParameterTypes(), 
                            XC_MethodReplacement.returnConstant(true)
                        );
                    }
                }
            }
        } catch (Throwable t) {
            XposedBridge.log("Error hooking VipOfflineModel: " + t.getMessage());
        }
    }
}
