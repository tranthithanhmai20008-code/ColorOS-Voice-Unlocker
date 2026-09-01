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

        XposedBridge.log("ColorOS Voice Unlocker: Hooking VoiceContentDataHelper directly");

        try {
            Class<?> helperClass = XposedHelpers.findClass(
                "business.module.magicalvoice.voice.VoiceContentDataHelper", 
                lpparam.classLoader
            );

            // Hook tat ca cac method trong VoiceContentDataHelper tra ve boolean -> Ep tra ve true
            for (Method method : helperClass.getDeclaredMethods()) {
                if (method.getReturnType() == boolean.class) {
                    XposedBridge.log("Hooking boolean method: " + method.getName());
                    XposedHelpers.findAndHookMethod(
                        helperClass, 
                        method.getName(), 
                        method.getParameterTypes(), 
                        XC_MethodReplacement.returnConstant(true)
                    );
                }
            }

            // Forced trigger cho ham r(Z)V hoac o()V neu can
            XposedHelpers.findAndHookMethod(
                helperClass, 
                "o", 
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("VoiceContentDataHelper.o() called!");
                    }
                }
            );

        } catch (Throwable t) {
            XposedBridge.log("Failed to hook VoiceContentDataHelper: " + t.getMessage());
        }
    }
}
