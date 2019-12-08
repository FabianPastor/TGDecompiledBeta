package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EmuDetector {
    private static final String[] ANDY_FILES = new String[]{"fstab.andy", "ueventd.andy.rc"};
    private static final String[] DEVICE_IDS = new String[]{"NUM", "e21833235b6eevar_", "NUM"};
    private static final String[] GENY_FILES = new String[]{"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    private static final String[] IMSI_IDS = new String[]{"NUM"};
    private static final String IP = "10.0.2.15";
    private static final int MIN_PROPERTIES_THRESHOLD = 5;
    private static final String[] NOX_FILES = new String[]{"fstab.nox", "init.nox.rc", "ueventd.nox.rc"};
    private static final String[] PHONE_NUMBERS = new String[]{"NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM"};
    private static final String[] PIPES = new String[]{"/dev/socket/qemud", "/dev/qemu_pipe"};
    private static final Property[] PROPERTIES = new Property[]{new Property("init.svc.qemud", null), new Property("init.svc.qemu-props", null), new Property("qemu.hw.mainkeys", null), new Property("qemu.sf.fake_camera", null), new Property("qemu.sf.lcd_density", null), new Property("ro.bootloader", "unknown"), new Property("ro.bootmode", "unknown"), new Property("ro.hardware", "goldfish"), new Property("ro.kernel.android.qemud", null), new Property("ro.kernel.qemu.gles", null), new Property("ro.kernel.qemu", "1"), new Property("ro.product.device", "generic"), new Property("ro.product.model", "sdk"), new Property("ro.product.name", "sdk"), new Property("ro.serialno", null)};
    private static final String[] QEMU_DRIVERS = new String[]{"goldfish"};
    private static final String[] X86_FILES = new String[]{"ueventd.android_x86.rc", "x86.prop", "ueventd.ttVM_x86.rc", "init.ttVM_x86.rc", "fstab.ttVM_x86", "fstab.vbox86", "init.vbox86.rc", "ueventd.vbox86.rc"};
    @SuppressLint({"StaticFieldLeak"})
    private static EmuDetector mEmulatorDetector;
    private boolean detectResult;
    private boolean detected;
    private boolean isCheckPackage = true;
    private boolean isTelephony = false;
    private final Context mContext;
    private List<String> mListPackageName = new ArrayList();

    public interface OnEmulatorDetectorListener {
        void onResult(boolean z);
    }

    static class Property {
        public String name;
        public String seek_value;

        public Property(String str, String str2) {
            this.name = str;
            this.seek_value = str2;
        }
    }

    public static EmuDetector with(Context context) {
        if (context != null) {
            if (mEmulatorDetector == null) {
                mEmulatorDetector = new EmuDetector(context.getApplicationContext());
            }
            return mEmulatorDetector;
        }
        throw new IllegalArgumentException("Context must not be null.");
    }

    private EmuDetector(Context context) {
        this.mContext = context;
        this.mListPackageName.add("com.google.android.launcher.layouts.genymotion");
        this.mListPackageName.add("com.bluestacks");
        this.mListPackageName.add("com.bignox.app");
    }

    public boolean isCheckTelephony() {
        return this.isTelephony;
    }

    public boolean isCheckPackage() {
        return this.isCheckPackage;
    }

    public EmuDetector setCheckTelephony(boolean z) {
        this.isTelephony = z;
        return this;
    }

    public EmuDetector setCheckPackage(boolean z) {
        this.isCheckPackage = z;
        return this;
    }

    public EmuDetector addPackageName(String str) {
        this.mListPackageName.add(str);
        return this;
    }

    public EmuDetector addPackageName(List<String> list) {
        this.mListPackageName.addAll(list);
        return this;
    }

    public boolean detect() {
        if (this.detected) {
            return this.detectResult;
        }
        try {
            this.detected = true;
            if (!this.detectResult) {
                this.detectResult = checkBasic();
            }
            if (!this.detectResult) {
                this.detectResult = checkAdvanced();
            }
            if (!this.detectResult) {
                this.detectResult = checkPackageName();
            }
            return this.detectResult;
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c2 A:{RETURN} */
    private boolean checkBasic() {
        /*
        r6 = this;
        r0 = android.os.Build.FINGERPRINT;
        r1 = "generic";
        r0 = r0.startsWith(r1);
        r2 = 0;
        r3 = "google_sdk";
        r4 = 1;
        if (r0 != 0) goto L_0x00bf;
    L_0x000e:
        r0 = android.os.Build.MODEL;
        r0 = r0.contains(r3);
        if (r0 != 0) goto L_0x00bf;
    L_0x0016:
        r0 = android.os.Build.MODEL;
        r0 = r0.toLowerCase();
        r5 = "droid4x";
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0024:
        r0 = android.os.Build.MODEL;
        r5 = "Emulator";
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x002e:
        r0 = android.os.Build.MODEL;
        r5 = "Android SDK built for x86";
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0038:
        r0 = android.os.Build.MANUFACTURER;
        r5 = "Genymotion";
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0042:
        r0 = android.os.Build.HARDWARE;
        r5 = "goldfish";
        r0 = r0.equals(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x004c:
        r0 = android.os.Build.HARDWARE;
        r5 = "vbox86";
        r0 = r0.equals(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0057:
        r0 = android.os.Build.PRODUCT;
        r5 = "sdk";
        r0 = r0.equals(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0061:
        r0 = android.os.Build.PRODUCT;
        r0 = r0.equals(r3);
        if (r0 != 0) goto L_0x00bf;
    L_0x0069:
        r0 = android.os.Build.PRODUCT;
        r5 = "sdk_x86";
        r0 = r0.equals(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0073:
        r0 = android.os.Build.PRODUCT;
        r5 = "vbox86p";
        r0 = r0.equals(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x007e:
        r0 = android.os.Build.BOARD;
        r0 = r0.toLowerCase();
        r5 = "nox";
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x008c:
        r0 = android.os.Build.BOOTLOADER;
        r0 = r0.toLowerCase();
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x0098:
        r0 = android.os.Build.HARDWARE;
        r0 = r0.toLowerCase();
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x00a4:
        r0 = android.os.Build.PRODUCT;
        r0 = r0.toLowerCase();
        r0 = r0.contains(r5);
        if (r0 != 0) goto L_0x00bf;
    L_0x00b0:
        r0 = android.os.Build.SERIAL;
        r0 = r0.toLowerCase();
        r0 = r0.contains(r5);
        if (r0 == 0) goto L_0x00bd;
    L_0x00bc:
        goto L_0x00bf;
    L_0x00bd:
        r0 = 0;
        goto L_0x00c0;
    L_0x00bf:
        r0 = 1;
    L_0x00c0:
        if (r0 == 0) goto L_0x00c3;
    L_0x00c2:
        return r4;
    L_0x00c3:
        r5 = android.os.Build.BRAND;
        r5 = r5.startsWith(r1);
        if (r5 == 0) goto L_0x00d4;
    L_0x00cb:
        r5 = android.os.Build.DEVICE;
        r1 = r5.startsWith(r1);
        if (r1 == 0) goto L_0x00d4;
    L_0x00d3:
        r2 = 1;
    L_0x00d4:
        r0 = r0 | r2;
        if (r0 == 0) goto L_0x00d8;
    L_0x00d7:
        return r4;
    L_0x00d8:
        r1 = android.os.Build.PRODUCT;
        r1 = r3.equals(r1);
        r0 = r0 | r1;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.EmuDetector.checkBasic():boolean");
    }

    private boolean checkAdvanced() {
        return checkTelephony() || checkFiles(GENY_FILES, "Geny") || checkFiles(ANDY_FILES, "Andy") || checkFiles(NOX_FILES, "Nox") || checkQEmuDrivers() || checkFiles(PIPES, "Pipes") || checkIp() || (checkQEmuProps() && checkFiles(X86_FILES, "X86"));
    }

    private boolean checkPackageName() {
        if (this.isCheckPackage && !this.mListPackageName.isEmpty()) {
            PackageManager packageManager = this.mContext.getPackageManager();
            for (String launchIntentForPackage : this.mListPackageName) {
                Intent launchIntentForPackage2 = packageManager.getLaunchIntentForPackage(launchIntentForPackage);
                if (launchIntentForPackage2 != null && !packageManager.queryIntentActivities(launchIntentForPackage2, 65536).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkTelephony() {
        return ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_PHONE_STATE") == 0 && this.isTelephony && isSupportTelePhony() && (checkPhoneNumber() || checkDeviceId() || checkImsi() || checkOperatorNameAndroid());
    }

    private boolean checkPhoneNumber() {
        String line1Number = ((TelephonyManager) this.mContext.getSystemService("phone")).getLine1Number();
        for (String equalsIgnoreCase : PHONE_NUMBERS) {
            if (equalsIgnoreCase.equalsIgnoreCase(line1Number)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDeviceId() {
        String deviceId = ((TelephonyManager) this.mContext.getSystemService("phone")).getDeviceId();
        for (String equalsIgnoreCase : DEVICE_IDS) {
            if (equalsIgnoreCase.equalsIgnoreCase(deviceId)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkImsi() {
        String subscriberId = ((TelephonyManager) this.mContext.getSystemService("phone")).getSubscriberId();
        for (String equalsIgnoreCase : IMSI_IDS) {
            if (equalsIgnoreCase.equalsIgnoreCase(subscriberId)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOperatorNameAndroid() {
        return ((TelephonyManager) this.mContext.getSystemService("phone")).getNetworkOperatorName().equalsIgnoreCase("android");
    }

    private boolean checkQEmuDrivers() {
        for (File file : new File[]{new File("/proc/tty/drivers"), new File("/proc/cpuinfo")}) {
            if (file.exists() && file.canRead()) {
                byte[] bArr = new byte[1024];
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    fileInputStream.read(bArr);
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String str = new String(bArr);
                for (CharSequence contains : QEMU_DRIVERS) {
                    if (str.contains(contains)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private boolean checkFiles(String[] strArr, String str) {
        for (String file : strArr) {
            if (new File(file).exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkQEmuProps() {
        int i = 0;
        for (Property property : PROPERTIES) {
            String prop = getProp(this.mContext, property.name);
            if (property.seek_value == null && prop != null) {
                i++;
            }
            String str = property.seek_value;
            if (str != null && prop.contains(str)) {
                i++;
            }
        }
        if (i >= 5) {
            return true;
        }
        return false;
    }

    private boolean checkIp() {
        if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.INTERNET") != 0) {
            return false;
        }
        String[] strArr = new String[]{"/system/bin/netcfg"};
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(strArr);
            processBuilder.directory(new File("/system/bin/"));
            processBuilder.redirectErrorStream(true);
            InputStream inputStream = processBuilder.start().getInputStream();
            byte[] bArr = new byte[1024];
            while (inputStream.read(bArr) != -1) {
                stringBuilder.append(new String(bArr));
            }
            inputStream.close();
        } catch (Exception unused) {
        }
        String stringBuilder2 = stringBuilder.toString();
        if (TextUtils.isEmpty(stringBuilder2)) {
            return false;
        }
        for (String str : stringBuilder2.split("\n")) {
            if ((str.contains("wlan0") || str.contains("tunl0") || str.contains("eth0")) && str.contains("10.0.2.15")) {
                return true;
            }
        }
        return false;
    }

    private String getProp(Context context, String str) {
        try {
            Class loadClass = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (String) loadClass.getMethod("get", new Class[]{String.class}).invoke(loadClass, new Object[]{str});
        } catch (Exception unused) {
            return null;
        }
    }

    private boolean isSupportTelePhony() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }
}
