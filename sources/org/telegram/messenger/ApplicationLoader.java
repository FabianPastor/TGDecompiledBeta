package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.multidex.MultiDex;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.File;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.Components.ForegroundDetector;

public class ApplicationLoader extends Application {
    @SuppressLint({"StaticFieldLeak"})
    public static volatile Context applicationContext = null;
    public static volatile Handler applicationHandler = null;
    private static volatile boolean applicationInited = false;
    public static boolean canDrawOverlays = false;
    /* access modifiers changed from: private */
    public static ConnectivityManager connectivityManager = null;
    public static volatile NetworkInfo currentNetworkInfo = null;
    public static volatile boolean externalInterfacePaused = true;
    public static boolean hasPlayServices = false;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static volatile long mainInterfacePausedStageQueueTime = 0;
    public static volatile boolean mainInterfaceStopped = true;
    public static long startTime;

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static File getFilesDirFixed() {
        for (int i = 0; i < 10; i++) {
            File filesDir = applicationContext.getFilesDir();
            if (filesDir != null) {
                return filesDir;
            }
        }
        try {
            File file = new File(applicationContext.getApplicationInfo().dataDir, "files");
            file.mkdirs();
            return file;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
                connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
                applicationContext.registerReceiver(new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        try {
                            ApplicationLoader.currentNetworkInfo = ApplicationLoader.connectivityManager.getActiveNetworkInfo();
                        } catch (Throwable unused) {
                        }
                        boolean isConnectionSlow = ApplicationLoader.isConnectionSlow();
                        for (int i = 0; i < 3; i++) {
                            ConnectionsManager.getInstance(i).checkConnection();
                            FileLoader.getInstance(i).onNetworkChanged(isConnectionSlow);
                        }
                    }
                }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), intentFilter);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            try {
                isScreenOn = ((PowerManager) applicationContext.getSystemService("power")).isScreenOn();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("screen state = " + isScreenOn);
                }
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
            SharedConfig.loadConfig();
            for (int i = 0; i < 3; i++) {
                UserConfig.getInstance(i).loadConfig();
                MessagesController.getInstance(i);
                if (i == 0) {
                    SharedConfig.pushStringStatus = "__FIREBASE_GENERATING_SINCE_" + ConnectionsManager.getInstance(i).getCurrentTime() + "__";
                } else {
                    ConnectionsManager.getInstance(i);
                }
                TLRPC$User currentUser = UserConfig.getInstance(i).getCurrentUser();
                if (currentUser != null) {
                    MessagesController.getInstance(i).putUser(currentUser, true);
                    SendMessagesHelper.getInstance(i).checkUnsentMessages();
                }
            }
            ((ApplicationLoader) applicationContext).initPlayServices();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app initied");
            }
            MediaController.getInstance();
            for (int i2 = 0; i2 < 3; i2++) {
                ContactsController.getInstance(i2).checkAppAccount();
                DownloadController.getInstance(i2);
            }
            WearDataLayerListenerService.updateWatchConnectionState();
        }
    }

    public void onCreate() {
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable unused) {
        }
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("app start time = ");
            long elapsedRealtime = SystemClock.elapsedRealtime();
            startTime = elapsedRealtime;
            sb.append(elapsedRealtime);
            FileLog.d(sb.toString());
        }
        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }
        NativeLoader.initNativeLibs(applicationContext);
        ConnectionsManager.native_setJava(false);
        new ForegroundDetector(this) {
            public void onActivityStarted(Activity activity) {
                boolean isBackground = isBackground();
                super.onActivityStarted(activity);
                if (isBackground) {
                    ApplicationLoader.ensureCurrentNetworkGet(true);
                }
            }
        };
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load libs time = " + (SystemClock.elapsedRealtime() - startTime));
        }
        applicationHandler = new Handler(applicationContext.getMainLooper());
        AndroidUtilities.runOnUIThread($$Lambda$6var_mOFSl8nO3HAcLKG9VPMoeQg.INSTANCE);
    }

    public static void startPushService() {
        boolean z;
        SharedPreferences globalNotificationsSettings = MessagesController.getGlobalNotificationsSettings();
        if (globalNotificationsSettings.contains("pushService")) {
            z = globalNotificationsSettings.getBoolean("pushService", true);
        } else {
            z = MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("keepAliveService", false);
        }
        if (z) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
            } catch (Throwable unused) {
            }
        } else {
            applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
            ((AlarmManager) applicationContext.getSystemService("alarm")).cancel(PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0));
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(configuration);
            AndroidUtilities.checkDisplaySize(applicationContext, configuration);
            VideoCapturerDevice.checkScreenCapturerSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                ApplicationLoader.this.lambda$initPlayServices$2$ApplicationLoader();
            }
        }, 1000);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initPlayServices$2 */
    public /* synthetic */ void lambda$initPlayServices$2$ApplicationLoader() {
        boolean checkPlayServices = checkPlayServices();
        hasPlayServices = checkPlayServices;
        if (checkPlayServices) {
            String str = SharedConfig.pushString;
            if (!TextUtils.isEmpty(str)) {
                if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                    FileLog.d("GCM regId = " + str);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("GCM Registration not found.");
            }
            Utilities.globalQueue.postRunnable($$Lambda$ApplicationLoader$dbTUy49tKRSqRXfy82kI6aAf9FY.INSTANCE);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("No valid Google Play Services APK found.");
        }
        SharedConfig.pushStringStatus = "__NO_GOOGLE_PLAY_SERVICES__";
        GcmPushListenerService.sendRegistrationToServer((String) null);
    }

    static /* synthetic */ void lambda$null$1() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener($$Lambda$ApplicationLoader$v99OGWADdqAMce5sv4qrr65oZVo.INSTANCE);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    static /* synthetic */ void lambda$null$0(Task task) {
        if (!task.isSuccessful()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Failed to get regid");
            }
            SharedConfig.pushStringStatus = "__FIREBASE_FAILED__";
            GcmPushListenerService.sendRegistrationToServer((String) null);
            return;
        }
        String str = (String) task.getResult();
        if (!TextUtils.isEmpty(str)) {
            GcmPushListenerService.sendRegistrationToServer(str);
        }
    }

    private boolean checkPlayServices() {
        try {
            return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public static void ensureCurrentNetworkGet(boolean z) {
        if (z || currentNetworkInfo == null) {
            try {
                if (connectivityManager == null) {
                    connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
                }
                currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
            } catch (Throwable unused) {
            }
        }
    }

    public static boolean isRoaming() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null || !currentNetworkInfo.isRoaming()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        NetworkInfo.State state;
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null || ((currentNetworkInfo.getType() != 1 && currentNetworkInfo.getType() != 9) || ((state = currentNetworkInfo.getState()) != NetworkInfo.State.CONNECTED && state != NetworkInfo.State.CONNECTING && state != NetworkInfo.State.SUSPENDED))) {
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static boolean isConnectedToWiFi() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null || ((currentNetworkInfo.getType() != 1 && currentNetworkInfo.getType() != 9) || currentNetworkInfo.getState() != NetworkInfo.State.CONNECTED)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static boolean isConnectionSlow() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && currentNetworkInfo.getType() == 0) {
                int subtype = currentNetworkInfo.getSubtype();
                if (subtype == 1 || subtype == 2 || subtype == 4 || subtype == 7 || subtype == 11) {
                    return true;
                }
            }
        } catch (Throwable unused) {
        }
        return false;
    }

    public static int getAutodownloadNetworkType() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null) {
                return 0;
            }
            if (currentNetworkInfo.getType() != 1) {
                if (currentNetworkInfo.getType() != 9) {
                    if (currentNetworkInfo.isRoaming()) {
                        return 2;
                    }
                    return 0;
                }
            }
            if (connectivityManager.isActiveNetworkMetered()) {
                return 0;
            }
            return 1;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return 1;
        }
        return isRoaming() ? 2 : 0;
    }

    public static boolean isNetworkOnlineFast() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && !currentNetworkInfo.isConnectedOrConnecting()) {
                if (!currentNetworkInfo.isAvailable()) {
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
                    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                        return true;
                    }
                    NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(1);
                    return networkInfo2 != null && networkInfo2.isConnectedOrConnecting();
                }
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return true;
        }
    }

    public static boolean isNetworkOnlineRealtime() {
        try {
            ConnectivityManager connectivityManager2 = (ConnectivityManager) applicationContext.getSystemService("connectivity");
            NetworkInfo activeNetworkInfo = connectivityManager2.getActiveNetworkInfo();
            if (activeNetworkInfo != null && (activeNetworkInfo.isConnectedOrConnecting() || activeNetworkInfo.isAvailable())) {
                return true;
            }
            NetworkInfo networkInfo = connectivityManager2.getNetworkInfo(0);
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
            NetworkInfo networkInfo2 = connectivityManager2.getNetworkInfo(1);
            if (networkInfo2 == null || !networkInfo2.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return true;
        }
    }

    public static boolean isNetworkOnline() {
        boolean isNetworkOnlineRealtime = isNetworkOnlineRealtime();
        if (BuildVars.DEBUG_PRIVATE_VERSION && isNetworkOnlineRealtime != isNetworkOnlineFast()) {
            FileLog.d("network online mismatch");
        }
        return isNetworkOnlineRealtime;
    }
}
