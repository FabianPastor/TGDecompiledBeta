package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.PowerManager;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.File;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader extends Application {
    @SuppressLint({"StaticFieldLeak"})
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static volatile long mainInterfacePausedStageQueueTime;

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
            FileLog.e(e);
            return new File("/data/data/org.telegram.messenger/files");
        }
    }

    public static void postInitApplication() {
        if (!applicationInited) {
            int a;
            applicationInited = true;
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
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("screen state = " + isScreenOn);
                }
            } catch (Throwable e3) {
                FileLog.e(e3);
            }
            SharedConfig.loadConfig();
            for (a = 0; a < 3; a++) {
                UserConfig.getInstance(a).loadConfig();
                MessagesController.getInstance(a);
                ConnectionsManager.getInstance(a);
                User user = UserConfig.getInstance(a).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(a).putUser(user, true);
                    MessagesController.getInstance(a).getBlockedUsers(true);
                    SendMessagesHelper.getInstance(a).checkUnsentMessages();
                }
            }
            ((ApplicationLoader) applicationContext).initPlayServices();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app initied");
            }
            MediaController.getInstance();
            for (a = 0; a < 3; a++) {
                ContactsController.getInstance(a).checkAppAccount();
                DownloadController.getInstance(a);
            }
            WearDataLayerListenerService.updateWatchConnectionState();
        }
    }

    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        NativeLoader.initNativeLibs(applicationContext);
        ConnectionsManager.native_setJava(false);
        ForegroundDetector foregroundDetector = new ForegroundDetector(this);
        applicationHandler = new Handler(applicationContext.getMainLooper());
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ApplicationLoader.startPushService();
            }
        });
    }

    public static void startPushService() {
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
                return;
            } catch (Throwable e) {
                FileLog.e(e);
                return;
            }
        }
        stopPushService();
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
                    if (SharedConfig.pushString == null || SharedConfig.pushString.length() == 0) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("GCM Registration not found.");
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("GCM regId = " + SharedConfig.pushString);
                    }
                    try {
                        ApplicationLoader.this.startService(new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class));
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("No valid Google Play Services APK found.");
                }
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
            FileLog.e(e);
            return true;
        }
    }
}
