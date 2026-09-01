package com.example.gameassistantunlocker;

import java.lang.reflect.Method;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
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

        XposedBridge.log("ColorOS Voice Unlocker: Hooking Magical Voice Module");

        // 1. Hook String.equals de bypass cac chuoi so sanh package name
        try {
            XposedHelpers.findAndHookMethod(
                String.class, "equals", Object.class,
                new de.robv.android.xposed.XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object other = param.args[0];
                        if (SPOOF_PKG.equals(param.thisObject) && TARGET_PKG.equals(other)) {
                            param.args[0] = SPOOF_PKG;
                        } else if (TARGET_PKG.equals(param.thisObject) && SPOOF_PKG.equals(other)) {
                            param.result = true;
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("String hook error: " + t.getMessage());
        }

        // 2. Scan va hook truc tiep va o cac Class co chua tu khoa "magicalvoice" hoac "voice"
        hookByReflection(lpparam.classLoader);
    }

    private void hookByReflection(ClassLoader classLoader) {
        // Thu hook cac Class pho bien lien quan den Voice Feature trong Oplus Framework
        String[] possibleClasses = new String[] {
            "business.module.magicalvoice.voice.VoiceFragment",
            "business.module.magicalvoice.a",
            "business.module.magicalvoice.b",
            "business.module.magicalvoice.c",
            "com.oplus.games.feature.voicesnippets.VoiceSnippetsLogin"
        };

        for (String className : possibleClasses) {
            try {
                Class<?> clazz = classLoader.loadClass(className);
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getReturnType() == boolean.class) {
                        XposedBridge.log("Forcing true for method: " + className + "." + method.getName());
                        XposedHelpers.findAndHookMethod(clazz, method.getName(), method.getParameterTypes(), XC_MethodReplacement.returnConstant(true));
                    }
                }
            } catch (Throwable ignored) {
                // Ignore class not found
            }
        }
    }
}
