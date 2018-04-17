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
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static volatile long mainInterfacePausedStageQueueTime;

    /* renamed from: org.telegram.messenger.ApplicationLoader$1 */
    class C00641 implements Runnable {
        C00641() {
        }

        public void run() {
            ApplicationLoader.startPushService();
        }
    }

    /* renamed from: org.telegram.messenger.ApplicationLoader$2 */
    class C00652 implements Runnable {

        /* renamed from: org.telegram.messenger.ApplicationLoader$2$1 */
        class C23681 implements Runnable {
            C23681() {
            }

            public void run() {
                try {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    if (!TextUtils.isEmpty(token)) {
                        GcmInstanceIDListenerService.sendRegistrationToServer(token);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C00652() {
        }

        public void run() {
            if (ApplicationLoader.this.checkPlayServices()) {
                String currentPushString = SharedConfig.pushString;
                if (TextUtils.isEmpty(currentPushString)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("GCM Registration not found.");
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("GCM regId = ");
                    stringBuilder.append(currentPushString);
                    FileLog.m0d(stringBuilder.toString());
                }
                Utilities.globalQueue.postRunnable(new C23681());
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("No valid Google Play Services APK found.");
            }
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
                IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), filter);
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
            int a = 0;
            for (int a2 = 0; a2 < 3; a2++) {
                UserConfig.getInstance(a2).loadConfig();
                MessagesController.getInstance(a2);
                ConnectionsManager.getInstance(a2);
                User user = UserConfig.getInstance(a2).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(a2).putUser(user, true);
                    MessagesController.getInstance(a2).getBlockedUsers(true);
                    SendMessagesHelper.getInstance(a2).checkUnsentMessages();
                }
            }
            applicationContext.initPlayServices();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("app initied");
            }
            MediaController.getInstance();
            while (a < 3) {
                ContactsController.getInstance(a).checkAppAccount();
                DownloadController.getInstance(a);
                a++;
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
        AndroidUtilities.runOnUIThread(new C00641());
    }

    public static void startPushService() {
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            return;
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
        AndroidUtilities.runOnUIThread(new C00652(), 1000);
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
