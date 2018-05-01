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
import android.text.TextUtils;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.File;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader extends Application {
    @SuppressLint({"StaticFieldLeak"})
    public static volatile Context applicationContext = null;
    public static volatile Handler applicationHandler = null;
    private static volatile boolean applicationInited = false;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static volatile long mainInterfacePausedStageQueueTime;

    /* renamed from: org.telegram.messenger.ApplicationLoader$1 */
    class C00671 implements Runnable {
        C00671() {
        }

        public void run() {
            ApplicationLoader.startPushService();
        }
    }

    /* renamed from: org.telegram.messenger.ApplicationLoader$2 */
    class C00692 implements Runnable {

        /* renamed from: org.telegram.messenger.ApplicationLoader$2$1 */
        class C00681 implements Runnable {
            C00681() {
            }

            public void run() {
                try {
                    Object token = FirebaseInstanceId.getInstance().getToken();
                    if (!TextUtils.isEmpty(token)) {
                        GcmInstanceIDListenerService.sendRegistrationToServer(token);
                    }
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
        }

        C00692() {
        }

        public void run() {
            if (ApplicationLoader.this.checkPlayServices()) {
                Object obj = SharedConfig.pushString;
                if (TextUtils.isEmpty(obj)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("GCM Registration not found.");
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("GCM regId = ");
                    stringBuilder.append(obj);
                    FileLog.m0d(stringBuilder.toString());
                }
                Utilities.globalQueue.postRunnable(new C00681());
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("No valid Google Play Services APK found.");
            }
        }
    }

    public static File getFilesDirFixed() {
        for (int i = 0; i < 10; i++) {
            File filesDir = applicationContext.getFilesDir();
            if (filesDir != null) {
                return filesDir;
            }
        }
        try {
            filesDir = new File(applicationContext.getApplicationInfo().dataDir, "files");
            filesDir.mkdirs();
            return filesDir;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return new File("/data/data/org.telegram.messenger/files");
        }
    }

    public static void postInitApplication() {
        if (!applicationInited) {
            applicationInited = true;
            try {
                LocaleController.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), intentFilter);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                isScreenOn = ((PowerManager) applicationContext.getSystemService("power")).isScreenOn();
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("screen state = ");
                    stringBuilder.append(isScreenOn);
                    FileLog.m0d(stringBuilder.toString());
                }
            } catch (Throwable e3) {
                FileLog.m3e(e3);
            }
            SharedConfig.loadConfig();
            int i = 0;
            for (int i2 = 0; i2 < 3; i2++) {
                UserConfig.getInstance(i2).loadConfig();
                MessagesController.getInstance(i2);
                ConnectionsManager.getInstance(i2);
                User currentUser = UserConfig.getInstance(i2).getCurrentUser();
                if (currentUser != null) {
                    MessagesController.getInstance(i2).putUser(currentUser, true);
                    MessagesController.getInstance(i2).getBlockedUsers(true);
                    SendMessagesHelper.getInstance(i2).checkUnsentMessages();
                }
            }
            ((ApplicationLoader) applicationContext).initPlayServices();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("app initied");
            }
            MediaController.getInstance();
            while (i < 3) {
                ContactsController.getInstance(i).checkAppAccount();
                DownloadController.getInstance(i);
                i++;
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
        AndroidUtilities.runOnUIThread(new C00671());
    }

    public static void startPushService() {
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
                return;
            } catch (Throwable th) {
                FileLog.m3e(th);
                return;
            }
        }
        stopPushService();
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
        ((AlarmManager) applicationContext.getSystemService("alarm")).cancel(PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0));
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(configuration);
            AndroidUtilities.checkDisplaySize(applicationContext, configuration);
        } catch (Configuration configuration2) {
            configuration2.printStackTrace();
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new C00692(), 1000);
    }

    private boolean checkPlayServices() {
        boolean z = true;
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != 0) {
                z = false;
            }
            return z;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return true;
        }
    }
}
