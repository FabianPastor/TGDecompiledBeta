package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0559C;

public class EmuDetector {
    private static final String[] ANDY_FILES = new String[]{"fstab.andy", "ueventd.andy.rc"};
    private static final String[] DEVICE_IDS = new String[]{"000000000000000", "e21833235b6eef10", "012345678912345"};
    private static final String[] GENY_FILES = new String[]{"/dev/socket/genyd", "/dev/socket/baseband_genyd"};
    private static final String[] IMSI_IDS = new String[]{"310260000000000"};
    private static final String IP = "10.0.2.15";
    private static final int MIN_PROPERTIES_THRESHOLD = 5;
    private static final String[] NOX_FILES = new String[]{"fstab.nox", "init.nox.rc", "ueventd.nox.rc"};
    private static final String[] PHONE_NUMBERS = new String[]{"15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564", "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576", "15555215578", "15555215580", "15555215582", "15555215584"};
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

        public Property(String name, String seek_value) {
            this.name = name;
            this.seek_value = seek_value;
        }
    }

    public static EmuDetector with(Context pContext) {
        if (pContext == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        if (mEmulatorDetector == null) {
            mEmulatorDetector = new EmuDetector(pContext.getApplicationContext());
        }
        return mEmulatorDetector;
    }

    private EmuDetector(Context pContext) {
        this.mContext = pContext;
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

    public EmuDetector setCheckTelephony(boolean telephony) {
        this.isTelephony = telephony;
        return this;
    }

    public EmuDetector setCheckPackage(boolean chkPackage) {
        this.isCheckPackage = chkPackage;
        return this;
    }

    public EmuDetector addPackageName(String pPackageName) {
        this.mListPackageName.add(pPackageName);
        return this;
    }

    public EmuDetector addPackageName(List<String> pListPackageName) {
        this.mListPackageName.addAll(pListPackageName);
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
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkBasic() {
        boolean result;
        int i = 0;
        if (Build.FINGERPRINT.startsWith("generic") || Build.MODEL.contains("google_sdk") || Build.MODEL.toLowerCase().contains("droid4x") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || Build.HARDWARE.equals("goldfish") || Build.HARDWARE.equals("vbox86") || Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("google_sdk") || Build.PRODUCT.equals("sdk_x86") || Build.PRODUCT.equals("vbox86p") || Build.BOARD.toLowerCase().contains("nox") || Build.BOOTLOADER.toLowerCase().contains("nox") || Build.HARDWARE.toLowerCase().contains("nox") || Build.PRODUCT.toLowerCase().contains("nox") || Build.SERIAL.toLowerCase().contains("nox")) {
            result = true;
        } else {
            result = false;
        }
        if (result) {
            return true;
        }
        if (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) {
            i = 1;
        }
        result |= i;
        if (result) {
            boolean z = result;
            return true;
        }
        result |= "google_sdk".equals(Build.PRODUCT);
        z = result;
        return result;
    }

    private boolean checkAdvanced() {
        if (checkTelephony() || checkFiles(GENY_FILES, "Geny") || checkFiles(ANDY_FILES, "Andy") || checkFiles(NOX_FILES, "Nox") || checkQEmuDrivers() || checkFiles(PIPES, "Pipes") || checkIp() || (checkQEmuProps() && checkFiles(X86_FILES, "X86"))) {
            return true;
        }
        return false;
    }

    private boolean checkPackageName() {
        if (!this.isCheckPackage || this.mListPackageName.isEmpty()) {
            return false;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        for (String pkgName : this.mListPackageName) {
            Intent tryIntent = packageManager.getLaunchIntentForPackage(pkgName);
            if (tryIntent != null && !packageManager.queryIntentActivities(tryIntent, C0559C.DEFAULT_BUFFER_SEGMENT_SIZE).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTelephony() {
        return ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_PHONE_STATE") == 0 && this.isTelephony && isSupportTelePhony() && (checkPhoneNumber() || checkDeviceId() || checkImsi() || checkOperatorNameAndroid());
    }

    private boolean checkPhoneNumber() {
        String phoneNumber = ((TelephonyManager) this.mContext.getSystemService("phone")).getLine1Number();
        for (String number : PHONE_NUMBERS) {
            if (number.equalsIgnoreCase(phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDeviceId() {
        String deviceId = ((TelephonyManager) this.mContext.getSystemService("phone")).getDeviceId();
        for (String known_deviceId : DEVICE_IDS) {
            if (known_deviceId.equalsIgnoreCase(deviceId)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkImsi() {
        String imsi = ((TelephonyManager) this.mContext.getSystemService("phone")).getSubscriberId();
        for (String known_imsi : IMSI_IDS) {
            if (known_imsi.equalsIgnoreCase(imsi)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOperatorNameAndroid() {
        return ((TelephonyManager) this.mContext.getSystemService("phone")).getNetworkOperatorName().equalsIgnoreCase("android");
    }

    private boolean checkQEmuDrivers() {
        for (File drivers_file : new File[]{new File("/proc/tty/drivers"), new File("/proc/cpuinfo")}) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                byte[] data = new byte[1024];
                try {
                    InputStream is = new FileInputStream(drivers_file);
                    is.read(data);
                    is.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                String driver_data = new String(data);
                for (String known_qemu_driver : QEMU_DRIVERS) {
                    if (driver_data.contains(known_qemu_driver)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private boolean checkFiles(String[] targets, String type) {
        for (String pipe : targets) {
            if (new File(pipe).exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkQEmuProps() {
        int found_props = 0;
        for (Property property : PROPERTIES) {
            String property_value = getProp(this.mContext, property.name);
            if (property.seek_value == null && property_value != null) {
                found_props++;
            }
            if (property.seek_value != null && property_value.contains(property.seek_value)) {
                found_props++;
            }
        }
        if (found_props >= 5) {
            return true;
        }
        return false;
    }

    private boolean checkIp() {
        int i = 0;
        if (ContextCompat.checkSelfPermission(this.mContext, "android.permission.INTERNET") != 0) {
            return false;
        }
        String[] args = new String[]{"/system/bin/netcfg"};
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder(args);
            builder.directory(new File("/system/bin/"));
            builder.redirectErrorStream(true);
            InputStream in = builder.start().getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                stringBuilder.append(new String(re));
            }
            in.close();
        } catch (Exception e) {
        }
        String netData = stringBuilder.toString();
        if (TextUtils.isEmpty(netData)) {
            return false;
        }
        String[] array = netData.split("\n");
        int length = array.length;
        while (i < length) {
            String lan = array[i];
            if ((lan.contains("wlan0") || lan.contains("tunl0") || lan.contains("eth0")) && lan.contains(IP)) {
                return true;
            }
            i++;
        }
        return false;
    }

    private String getProp(Context context, String property) {
        try {
            Class<?> systemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (String) systemProperties.getMethod("get", new Class[]{String.class}).invoke(systemProperties, new Object[]{property});
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isSupportTelePhony() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }
}
