package com.example.gameassistantunlocker;

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

        XposedBridge.log("ColorOS Voice Unlocker: Hooking Magical Voice Module");

        // 1. Hook bypass các hàm kiểm tra Support/Enable trong package MagicalVoice
        hookMagicalVoice(lpparam.classLoader);
    }

    private void hookMagicalVoice(ClassLoader classLoader) {
        // Danh sách các Class nghi vấn quản lý kiểm tra tính năng Voice Changer
        String[] targetClasses = new String[]{
            "business.module.magicalvoice.voice.VoiceFragment",
            "business.module.magicalvoice.MagicalVoiceManager",
            "business.module.magicalvoice.utils.VoiceUtils",
            "business.module.magicalvoice.helper.VoiceHelper"
        };

        for (String className : targetClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(className, classLoader);
                if (clazz != null) {
                    // Tim tat ca cac method trong class, neu method tra ve boolean va chua tu khoa check -> Ep tra ve true
                    for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                        if (method.getReturnType() == boolean.class) {
                            String mName = method.getName().toLowerCase();
                            if (mName.contains("support") || mName.contains("enable") || mName.contains("show") || mName.contains("check")) {
                                XposedBridge.log("Hooking method: " + className + "." + method.getName());
                                XposedHelpers.findAndHookMethod(clazz, method.getName(), method.getParameterTypes(), XC_MethodReplacement.returnConstant(true));
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                XposedBridge.log("Failed to hook " + className + ": " + t.getMessage());
            }
        }
    }
}
