package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.io.File;
import org.telegram.p005ui.Components.ForegroundDetector;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;

public class ApplicationLoader extends Application {
    @SuppressLint({"StaticFieldLeak"})
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;
    private static ConnectivityManager connectivityManager;
    public static volatile NetworkInfo currentNetworkInfo;
    public static volatile boolean externalInterfacePaused = true;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static volatile long mainInterfacePausedStageQueueTime;

    /* renamed from: org.telegram.messenger.ApplicationLoader$1 */
    static class CLASSNAME extends BroadcastReceiver {
        CLASSNAME() {
        }

        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.currentNetworkInfo = ApplicationLoader.connectivityManager.getActiveNetworkInfo();
            for (int a = 0; a < 3; a++) {
                ConnectionsManager.getInstance(a).checkConnection();
                FileLoader.getInstance(a).onNetworkChanged(ApplicationLoader.isConnectionSlow());
            }
        }
    }

    public static File getFilesDirFixed() {
        File path;
        for (int a = 0; a < 10; a++) {
            path = applicationContext.getFilesDir();
            if (path != null) {
                return path;
            }
        }
        try {
            path = new File(applicationContext.getApplicationInfo().dataDir, "files");
            path.mkdirs();
            return path;
        } catch (Throwable e) {
            FileLog.m13e(e);
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
                ThrowableExtension.printStackTrace(e);
            }
            try {
                connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
                applicationContext.registerReceiver(new CLASSNAME(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                Utilities.globalQueue.postRunnable(ApplicationLoader$$Lambda$0.$instance);
            } catch (Exception e2) {
                ThrowableExtension.printStackTrace(e2);
            }
            try {
                IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), filter);
            } catch (Exception e22) {
                ThrowableExtension.printStackTrace(e22);
            }
            try {
                isScreenOn = ((PowerManager) applicationContext.getSystemService("power")).isScreenOn();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("screen state = " + isScreenOn);
                }
            } catch (Throwable e3) {
                FileLog.m13e(e3);
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
                FileLog.m10d("app initied");
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
        AndroidUtilities.runOnUIThread(ApplicationLoader$$Lambda$1.$instance);
    }

    public static void startPushService() {
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
                return;
            } catch (Throwable th) {
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
            ThrowableExtension.printStackTrace(e);
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new ApplicationLoader$$Lambda$2(this), 1000);
    }

    final /* synthetic */ void lambda$initPlayServices$2$ApplicationLoader() {
        if (checkPlayServices()) {
            String currentPushString = SharedConfig.pushString;
            if (TextUtils.isEmpty(currentPushString)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("GCM Registration not found.");
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d("GCM regId = " + currentPushString);
            }
            Utilities.globalQueue.postRunnable(ApplicationLoader$$Lambda$3.$instance);
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.m10d("No valid Google Play Services APK found.");
        }
    }

    static final /* synthetic */ void lambda$null$1$ApplicationLoader() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(ApplicationLoader$$Lambda$4.$instance);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    static final /* synthetic */ void lambda$null$0$ApplicationLoader(InstanceIdResult instanceIdResult) {
        String token = instanceIdResult.getToken();
        if (!TextUtils.isEmpty(token)) {
            GcmPushListenerService.sendRegistrationToServer(token);
        }
    }

    private boolean checkPlayServices() {
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            FileLog.m13e(e);
            return true;
        }
    }

    private static void ensureCurrentNetworkGet() {
        if (currentNetworkInfo == null) {
            currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
    }

    public static boolean isRoaming() {
        try {
            ensureCurrentNetworkGet();
            if (currentNetworkInfo == null || !currentNetworkInfo.isRoaming()) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m13e(e);
            return false;
        }
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            ensureCurrentNetworkGet();
            if (currentNetworkInfo != null) {
                State state = currentNetworkInfo.getState();
                if (state == State.CONNECTED || state == State.CONNECTING || state == State.SUSPENDED) {
                    return true;
                }
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return false;
    }

    public static boolean isConnectedToWiFi() {
        try {
            ensureCurrentNetworkGet();
            if (currentNetworkInfo != null && currentNetworkInfo.getState() == State.CONNECTED) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        return false;
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return 1;
        }
        if (isRoaming()) {
            return 2;
        }
        return 0;
    }

    public static boolean isConnectionSlow() {
        try {
            ensureCurrentNetworkGet();
            if (currentNetworkInfo != null && currentNetworkInfo.getType() == 0) {
                switch (currentNetworkInfo.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return true;
                }
            }
        } catch (Throwable th) {
        }
        return false;
    }

    public static boolean isNetworkOnline() {
        try {
            ensureCurrentNetworkGet();
            if (currentNetworkInfo == null) {
                return false;
            }
            if (currentNetworkInfo.isConnectedOrConnecting() || currentNetworkInfo.isAvailable()) {
                return true;
            }
            NetworkInfo netInfo = connectivityManager.getNetworkInfo(0);
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            netInfo = connectivityManager.getNetworkInfo(1);
            if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m13e(e);
            return true;
        }
    }
}
