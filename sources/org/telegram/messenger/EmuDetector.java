package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EmuDetector {
    private static final String[] ANDY_FILES = {"fstab.andy", "ueventd.andy.rc"};
    private static final String[] BLUE_FILES = {"/Android/data/com.bluestacks.home", "/Android/data/com.bluestacks.settings"};
    private static final String[] DEVICE_IDS = {"NUM", "e21833235b6eevar_", "NUM"};
    private static final String[] GENY_FILES = {"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    private static final String[] IMSI_IDS = {"NUM"};
    private static final String IP = "10.0.2.15";
    private static final int MIN_PROPERTIES_THRESHOLD = 5;
    private static final String[] NOX_FILES = {"fstab.nox", "init.nox.rc", "ueventd.nox.rc", "/BigNoxGameHD", "/YSLauncher"};
    private static final String[] PHONE_NUMBERS = {"NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM", "NUM"};
    private static final String[] PIPES = {"/dev/socket/qemud", "/dev/qemu_pipe"};
    private static final Property[] PROPERTIES = {new Property("init.svc.qemud", (String) null), new Property("init.svc.qemu-props", (String) null), new Property("qemu.hw.mainkeys", (String) null), new Property("qemu.sf.fake_camera", (String) null), new Property("qemu.sf.lcd_density", (String) null), new Property("ro.bootloader", "unknown"), new Property("ro.bootmode", "unknown"), new Property("ro.hardware", "goldfish"), new Property("ro.kernel.android.qemud", (String) null), new Property("ro.kernel.qemu.gles", (String) null), new Property("ro.kernel.qemu", "1"), new Property("ro.product.device", "generic"), new Property("ro.product.model", "sdk"), new Property("ro.product.name", "sdk"), new Property("ro.serialno", (String) null)};
    private static final String[] QEMU_DRIVERS = {"goldfish"};
    private static final String[] X86_FILES = {"ueventd.android_x86.rc", "x86.prop", "ueventd.ttVM_x86.rc", "init.ttVM_x86.rc", "fstab.ttVM_x86", "fstab.vbox86", "init.vbox86.rc", "ueventd.vbox86.rc"};
    @SuppressLint({"StaticFieldLeak"})
    private static EmuDetector mEmulatorDetector;
    private boolean detectResult;
    private boolean detected;
    private boolean isCheckPackage = true;
    private boolean isTelephony = false;
    private final Context mContext;
    private List<String> mListPackageName;

    private enum EmulatorTypes {
        GENY,
        ANDY,
        NOX,
        BLUE,
        PIPES,
        X86
    }

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
        ArrayList arrayList = new ArrayList();
        this.mListPackageName = arrayList;
        this.mContext = context;
        arrayList.add("com.google.android.launcher.layouts.genymotion");
        this.mListPackageName.add("com.bluestacks");
        this.mListPackageName.add("com.bignox.app");
        this.mListPackageName.add("com.vphone.launcher");
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
            if (!this.detectResult) {
                this.detectResult = EmuInputDevicesDetector.detect();
            }
            return this.detectResult;
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00ce A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00cf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkBasic() {
        /*
            r8 = this;
            java.lang.String r0 = android.os.Build.BOARD
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r1 = "nox"
            boolean r0 = r0.contains(r1)
            r2 = 0
            java.lang.String r3 = "google_sdk"
            java.lang.String r4 = "generic"
            r5 = 1
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.BOOTLOADER
            java.lang.String r0 = r0.toLowerCase()
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.FINGERPRINT
            boolean r0 = r0.startsWith(r4)
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.MODEL
            java.lang.String r6 = r0.toLowerCase()
            boolean r6 = r6.contains(r3)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = r0.toLowerCase()
            java.lang.String r7 = "droid4x"
            boolean r6 = r6.contains(r7)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = r0.toLowerCase()
            java.lang.String r7 = "emulator"
            boolean r6 = r6.contains(r7)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = "Android SDK built for x86"
            boolean r0 = r0.contains(r6)
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.MANUFACTURER
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r6 = "genymotion"
            boolean r0 = r0.contains(r6)
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.HARDWARE
            java.lang.String r6 = r0.toLowerCase()
            java.lang.String r7 = "goldfish"
            boolean r6 = r6.contains(r7)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = r0.toLowerCase()
            java.lang.String r7 = "vbox86"
            boolean r6 = r6.contains(r7)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = r0.toLowerCase()
            java.lang.String r7 = "android_x86"
            boolean r6 = r6.contains(r7)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r0 = r0.toLowerCase()
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.PRODUCT
            java.lang.String r6 = "sdk"
            boolean r6 = r0.equals(r6)
            if (r6 != 0) goto L_0x00cb
            boolean r6 = r0.equals(r3)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = "sdk_x86"
            boolean r6 = r0.equals(r6)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r6 = "vbox86p"
            boolean r6 = r0.equals(r6)
            if (r6 != 0) goto L_0x00cb
            java.lang.String r0 = r0.toLowerCase()
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x00cb
            java.lang.String r0 = android.os.Build.SERIAL
            java.lang.String r0 = r0.toLowerCase()
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x00c9
            goto L_0x00cb
        L_0x00c9:
            r0 = 0
            goto L_0x00cc
        L_0x00cb:
            r0 = 1
        L_0x00cc:
            if (r0 == 0) goto L_0x00cf
            return r5
        L_0x00cf:
            java.lang.String r1 = android.os.Build.BRAND
            boolean r1 = r1.startsWith(r4)
            if (r1 == 0) goto L_0x00e0
            java.lang.String r1 = android.os.Build.DEVICE
            boolean r1 = r1.startsWith(r4)
            if (r1 == 0) goto L_0x00e0
            r2 = 1
        L_0x00e0:
            r0 = r0 | r2
            if (r0 == 0) goto L_0x00e4
            return r5
        L_0x00e4:
            java.lang.String r1 = android.os.Build.PRODUCT
            boolean r1 = r3.equals(r1)
            r0 = r0 | r1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.EmuDetector.checkBasic():boolean");
    }

    private boolean checkAdvanced() {
        return checkTelephony() || checkFiles(GENY_FILES, EmulatorTypes.GENY) || checkFiles(ANDY_FILES, EmulatorTypes.ANDY) || checkFiles(NOX_FILES, EmulatorTypes.NOX) || checkFiles(BLUE_FILES, EmulatorTypes.BLUE) || checkQEmuDrivers() || checkFiles(PIPES, EmulatorTypes.PIPES) || checkIp() || (checkQEmuProps() && checkFiles(X86_FILES, EmulatorTypes.X86));
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
        File[] fileArr = {new File("/proc/tty/drivers"), new File("/proc/cpuinfo")};
        for (int i = 0; i < 2; i++) {
            File file = fileArr[i];
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
                for (String contains : QEMU_DRIVERS) {
                    if (str.contains(contains)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private boolean checkFiles(String[] strArr, EmulatorTypes emulatorTypes) {
        File file;
        for (String str : strArr) {
            if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                file = new File(str);
            } else if ((!str.contains("/") || emulatorTypes != EmulatorTypes.NOX) && emulatorTypes != EmulatorTypes.BLUE) {
                file = new File(str);
            } else {
                file = new File(Environment.getExternalStorageDirectory() + str);
            }
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkQEmuProps() {
        int i = 0;
        for (Property property : PROPERTIES) {
            String prop = getProp(this.mContext, property.name);
            String str = property.seek_value;
            if (str == null && prop != null) {
                i++;
            }
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
        String[] strArr = {"/system/bin/netcfg"};
        StringBuilder sb = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(strArr);
            processBuilder.directory(new File("/system/bin/"));
            processBuilder.redirectErrorStream(true);
            InputStream inputStream = processBuilder.start().getInputStream();
            byte[] bArr = new byte[1024];
            while (inputStream.read(bArr) != -1) {
                sb.append(new String(bArr));
            }
            inputStream.close();
        } catch (Exception unused) {
        }
        String sb2 = sb.toString();
        if (TextUtils.isEmpty(sb2)) {
            return false;
        }
        for (String str : sb2.split("\n")) {
            if ((str.contains("wlan0") || str.contains("tunl0") || str.contains("eth0")) && str.contains("10.0.2.15")) {
                return true;
            }
        }
        return false;
    }

    private String getProp(Context context, String str) {
        try {
            Class<?> loadClass = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (String) loadClass.getMethod("get", new Class[]{String.class}).invoke(loadClass, new Object[]{str});
        } catch (Exception unused) {
            return null;
        }
    }

    private boolean isSupportTelePhony() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }
}
