package org.telegram.messenger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.File;
import java.io.RandomAccessFile;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;
    private static Drawable cachedWallpaper;
    private static boolean isCustomTheme;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    private static int selectedColor;
    private static int serviceMessageColor;
    private static int serviceSelectedMessageColor;
    private static final Object sync = new Object();

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static int getSelectedColor() {
        return selectedColor;
    }

    public static void reloadWallpaper() {
        cachedWallpaper = null;
        serviceMessageColor = 0;
        applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("serviceMessageColor").commit();
        loadWallpaper();
    }

    private static void calcBackgroundColor() {
        int[] result = AndroidUtilities.calcDrawableColor(cachedWallpaper);
        serviceMessageColor = result[0];
        serviceSelectedMessageColor = result[1];
        applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("serviceMessageColor", serviceMessageColor).putInt("serviceSelectedMessageColor", serviceSelectedMessageColor).commit();
    }

    public static int getServiceMessageColor() {
        return serviceMessageColor;
    }

    public static int getServiceSelectedMessageColor() {
        return serviceSelectedMessageColor;
    }

    public static void loadWallpaper() {
        if (cachedWallpaper == null) {
            Utilities.searchQueue.postRunnable(new Runnable() {
                public void run() {
                    synchronized (ApplicationLoader.sync) {
                        int selectedColor = 0;
                        try {
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                            int selectedBackground = preferences.getInt("selectedBackground", 1000001);
                            selectedColor = preferences.getInt("selectedColor", 0);
                            ApplicationLoader.serviceMessageColor = preferences.getInt("serviceMessageColor", 0);
                            ApplicationLoader.serviceSelectedMessageColor = preferences.getInt("serviceSelectedMessageColor", 0);
                            if (selectedColor == 0) {
                                if (selectedBackground == 1000001) {
                                    ApplicationLoader.cachedWallpaper = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.background_hd);
                                    ApplicationLoader.isCustomTheme = false;
                                } else {
                                    File toFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                                    if (toFile.exists()) {
                                        ApplicationLoader.cachedWallpaper = Drawable.createFromPath(toFile.getAbsolutePath());
                                        ApplicationLoader.isCustomTheme = true;
                                    } else {
                                        ApplicationLoader.cachedWallpaper = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.background_hd);
                                        ApplicationLoader.isCustomTheme = false;
                                    }
                                }
                            }
                        } catch (Throwable th) {
                        }
                        if (ApplicationLoader.cachedWallpaper == null) {
                            if (selectedColor == 0) {
                                selectedColor = -2693905;
                            }
                            ApplicationLoader.cachedWallpaper = new ColorDrawable(selectedColor);
                        }
                        if (ApplicationLoader.serviceMessageColor == 0) {
                            ApplicationLoader.calcBackgroundColor();
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
                            }
                        });
                    }
                }
            });
        }
    }

    public static Drawable getCachedWallpaper() {
        Drawable drawable;
        synchronized (sync) {
            drawable = cachedWallpaper;
        }
        return drawable;
    }

    private static void convertConfig() {
        SharedPreferences preferences = applicationContext.getSharedPreferences("dataconfig", 0);
        if (preferences.contains("currentDatacenterId")) {
            boolean z;
            SerializedData buffer = new SerializedData(32768);
            buffer.writeInt32(2);
            if (preferences.getInt("datacenterSetId", 0) != 0) {
                z = true;
            } else {
                z = false;
            }
            buffer.writeBool(z);
            buffer.writeBool(true);
            buffer.writeInt32(preferences.getInt("currentDatacenterId", 0));
            buffer.writeInt32(preferences.getInt("timeDifference", 0));
            buffer.writeInt32(preferences.getInt("lastDcUpdateTime", 0));
            buffer.writeInt64(preferences.getLong("pushSessionId", 0));
            buffer.writeBool(false);
            buffer.writeInt32(0);
            try {
                String datacentersString = preferences.getString("datacenters", null);
                if (datacentersString != null) {
                    byte[] datacentersBytes = Base64.decode(datacentersString, 0);
                    if (datacentersBytes != null) {
                        SerializedData data = new SerializedData(datacentersBytes);
                        buffer.writeInt32(data.readInt32(false));
                        buffer.writeBytes(datacentersBytes, 4, datacentersBytes.length - 4);
                        data.cleanup();
                    }
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            try {
                RandomAccessFile fileOutputStream = new RandomAccessFile(new File(getFilesDirFixed(), "tgnet.dat"), "rws");
                byte[] bytes = buffer.toByteArray();
                fileOutputStream.writeInt(Integer.reverseBytes(bytes.length));
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (Throwable e2) {
                FileLog.e("tmessages", e2);
            }
            buffer.cleanup();
            preferences.edit().clear().commit();
        }
    }

    public static File getFilesDirFixed() {
        for (int a = 0; a < 10; a++) {
            File path = applicationContext.getFilesDir();
            if (path != null) {
                return path;
            }
        }
        try {
            path = new File(applicationContext.getApplicationInfo().dataDir, "files");
            path.mkdirs();
            return path;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return new File("/data/data/org.telegram.messenger/files");
        }
    }

    public static void postInitApplication() {
        if (!applicationInited) {
            String langCode;
            String deviceModel;
            String appVersion;
            String systemVersion;
            applicationInited = true;
            convertConfig();
            try {
                LocaleController.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), filter);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                isScreenOn = ((PowerManager) applicationContext.getSystemService("power")).isScreenOn();
                FileLog.e("tmessages", "screen state = " + isScreenOn);
            } catch (Throwable e3) {
                FileLog.e("tmessages", e3);
            }
            UserConfig.loadConfig();
            String configPath = getFilesDirFixed().toString();
            try {
                langCode = LocaleController.getLocaleStringIso639();
                deviceModel = Build.MANUFACTURER + Build.MODEL;
                PackageInfo pInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
                appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
                systemVersion = "SDK " + VERSION.SDK_INT;
            } catch (Exception e4) {
                langCode = "en";
                deviceModel = "Android unknown";
                appVersion = "App version unknown";
                systemVersion = "SDK " + VERSION.SDK_INT;
            }
            if (langCode.trim().length() == 0) {
                langCode = "en";
            }
            if (deviceModel.trim().length() == 0) {
                deviceModel = "Android unknown";
            }
            if (appVersion.trim().length() == 0) {
                appVersion = "App version unknown";
            }
            if (systemVersion.trim().length() == 0) {
                systemVersion = "SDK Unknown";
            }
            boolean enablePushConnection = applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushConnection", true);
            MessagesController.getInstance();
            ConnectionsManager.getInstance().init(BuildVars.BUILD_VERSION, 57, BuildVars.APP_ID, deviceModel, systemVersion, appVersion, langCode, configPath, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), enablePushConnection);
            if (UserConfig.getCurrentUser() != null) {
                MessagesController.getInstance().putUser(UserConfig.getCurrentUser(), true);
                ConnectionsManager.getInstance().applyCountryPortNumber(UserConfig.getCurrentUser().phone);
                MessagesController.getInstance().getBlockedUsers(true);
                SendMessagesHelper.getInstance().checkUnsentMessages();
            }
            ((ApplicationLoader) applicationContext).initPlayServices();
            FileLog.e("tmessages", "app initied");
            ContactsController.getInstance().checkAppAccount();
            MediaController.getInstance();
        }
    }

    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        NativeLoader.initNativeLibs(applicationContext);
        boolean z = VERSION.SDK_INT == 14 || VERSION.SDK_INT == 15;
        ConnectionsManager.native_setJava(z);
        ForegroundDetector foregroundDetector = new ForegroundDetector(this);
        applicationHandler = new Handler(applicationContext.getMainLooper());
        startPushService();
    }

    public static void startPushService() {
        if (applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true)) {
            applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
        } else {
            stopPushService();
        }
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
        ((AlarmManager) applicationContext.getSystemService("alarm")).cancel(PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (ApplicationLoader.this.checkPlayServices()) {
                    if (UserConfig.pushString == null || UserConfig.pushString.length() == 0) {
                        FileLog.d("tmessages", "GCM Registration not found.");
                    } else {
                        FileLog.d("tmessages", "GCM regId = " + UserConfig.pushString);
                    }
                    ApplicationLoader.this.startService(new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class));
                    return;
                }
                FileLog.d("tmessages", "No valid Google Play Services APK found.");
            }
        }, 1000);
    }

    private boolean checkPlayServices() {
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return true;
        }
    }
}
