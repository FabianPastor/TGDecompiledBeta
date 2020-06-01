package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;

public class ConnectionsManager extends BaseController {
    private static final int CORE_POOL_SIZE;
    public static final int CPU_COUNT;
    public static final int ConnectionStateConnected = 3;
    public static final int ConnectionStateConnecting = 1;
    public static final int ConnectionStateConnectingToProxy = 4;
    public static final int ConnectionStateUpdating = 5;
    public static final int ConnectionStateWaitingForNetwork = 2;
    public static final int ConnectionTypeDownload = 2;
    public static final int ConnectionTypeDownload2 = 65538;
    public static final int ConnectionTypeGeneric = 1;
    public static final int ConnectionTypePush = 8;
    public static final int ConnectionTypeUpload = 4;
    public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
    public static final Executor DNS_THREAD_POOL_EXECUTOR;
    public static final int FileTypeAudio = 50331648;
    public static final int FileTypeFile = 67108864;
    public static final int FileTypePhoto = 16777216;
    public static final int FileTypeVideo = 33554432;
    private static volatile ConnectionsManager[] Instance = new ConnectionsManager[3];
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    /* access modifiers changed from: private */
    public static AsyncTask currentTask;
    /* access modifiers changed from: private */
    public static HashMap<String, ResolvedDomain> dnsCache = new HashMap<>();
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    /* access modifiers changed from: private */
    public static HashMap<String, ResolveHostByNameTask> resolvingHostnameTasks = new HashMap<>();
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue(128);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "DnsAsyncTask #" + this.mCount.getAndIncrement());
        }
    };
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState = native_getConnectionState(this.currentAccount);
    private boolean isUpdating;
    private long lastPauseTime = System.currentTimeMillis();
    private AtomicInteger lastRequestToken = new AtomicInteger(1);

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j, String str, int i2);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native long native_checkProxy(int i, String str, int i2, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate);

    public static native void native_cleanUp(int i, boolean z);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i5, int i6, boolean z, boolean z2, int i7);

    public static native int native_isTestBackend(int i);

    public static native void native_onHostNameResolved(String str, long j, String str2);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_seSystemLangCode(int i, String str);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2, boolean z2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3, String str4);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setRegId(int i, String str);

    public static native void native_setSystemLangCode(int i, String str);

    public static native void native_setUseIpv6(int i, boolean z);

    public static native void native_setUserId(int i, int i2);

    public static native void native_switchBackend(int i);

    public static native void native_updateDcSettings(int i);

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        CORE_POOL_SIZE = Math.max(2, Math.min(availableProcessors - 1, 4));
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        DNS_THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private static class ResolvedDomain {
        public ArrayList<String> addresses;
        long ttl;

        public ResolvedDomain(ArrayList<String> arrayList, long j) {
            this.addresses = arrayList;
            this.ttl = j;
        }

        public String getAddress() {
            ArrayList<String> arrayList = this.addresses;
            return arrayList.get(Utilities.random.nextInt(arrayList.size()));
        }
    }

    public static ConnectionsManager getInstance(int i) {
        ConnectionsManager connectionsManager = Instance[i];
        if (connectionsManager == null) {
            synchronized (ConnectionsManager.class) {
                connectionsManager = Instance[i];
                if (connectionsManager == null) {
                    ConnectionsManager[] connectionsManagerArr = Instance;
                    ConnectionsManager connectionsManager2 = new ConnectionsManager(i);
                    connectionsManagerArr[i] = connectionsManager2;
                    connectionsManager = connectionsManager2;
                }
            }
        }
        return connectionsManager;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ConnectionsManager(int i) {
        super(i);
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        int i2 = i;
        File filesDirFixed = ApplicationLoader.getFilesDirFixed();
        if (i2 != 0) {
            File file = new File(filesDirFixed, "account" + i2);
            file.mkdirs();
            filesDirFixed = file;
        }
        String file2 = filesDirFixed.toString();
        boolean isPushConnectionEnabled = isPushConnectionEnabled();
        try {
            str5 = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            String lowerCase = LocaleController.getLocaleStringIso639().toLowerCase();
            str3 = Build.MANUFACTURER + Build.MODEL;
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            str2 = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                str2 = str2 + " pbeta";
            } else if (BuildVars.DEBUG_VERSION) {
                str2 = str2 + " beta";
            }
            str4 = "SDK " + Build.VERSION.SDK_INT;
            str = lowerCase;
        } catch (Exception unused) {
            str4 = "SDK " + Build.VERSION.SDK_INT;
            str = "";
            str2 = "App version unknown";
            str3 = "Android unknown";
            str5 = "en";
        }
        if (str5.trim().length() == 0) {
            str6 = "en";
        } else {
            str6 = str5;
        }
        if (str3.trim().length() == 0) {
            str7 = "Android unknown";
        } else {
            str7 = str3;
        }
        if (str2.trim().length() == 0) {
            str8 = "App version unknown";
        } else {
            str8 = str2;
        }
        String str9 = str4.trim().length() == 0 ? "SDK Unknown" : str4;
        getUserConfig().loadConfig();
        String str10 = SharedConfig.pushString;
        if (TextUtils.isEmpty(str10) && !TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            str10 = SharedConfig.pushStringStatus;
        }
        init(BuildVars.BUILD_VERSION, 114, BuildVars.APP_ID, str7, str9, str8, str, str6, file2, FileLog.getNetworkLogPath(), str10, AndroidUtilities.getCertificateSHA256Fingerprint(), (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000, getUserConfig().getClientUserId(), isPushConnectionEnabled);
    }

    public boolean isPushConnectionEnabled() {
        SharedPreferences globalNotificationsSettings = MessagesController.getGlobalNotificationsSettings();
        if (globalNotificationsSettings.contains("pushConnection")) {
            return globalNotificationsSettings.getBoolean("pushConnection", true);
        }
        return MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("backgroundConnection", false);
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis(this.currentAccount);
    }

    public int getCurrentTime() {
        return native_getCurrentTime(this.currentAccount);
    }

    public int getTimeDifference() {
        return native_getTimeDifference(this.currentAccount);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate) {
        return sendRequest(tLObject, requestDelegate, (QuickAckDelegate) null, 0);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, (QuickAckDelegate) null, (WriteToSocketDelegate) null, i, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i, int i2) {
        return sendRequest(tLObject, requestDelegate, (QuickAckDelegate) null, (WriteToSocketDelegate) null, i, Integer.MAX_VALUE, i2, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, quickAckDelegate, (WriteToSocketDelegate) null, i, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i, int i2, int i3, boolean z) {
        int andIncrement = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new Runnable(tLObject, andIncrement, requestDelegate, quickAckDelegate, writeToSocketDelegate, i, i2, i3, z) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ RequestDelegate f$3;
            private final /* synthetic */ QuickAckDelegate f$4;
            private final /* synthetic */ WriteToSocketDelegate f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ int f$8;
            private final /* synthetic */ boolean f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
            }

            public final void run() {
                ConnectionsManager.this.lambda$sendRequest$2$ConnectionsManager(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
            }
        });
        return andIncrement;
    }

    public /* synthetic */ void lambda$sendRequest$2$ConnectionsManager(TLObject tLObject, int i, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z) {
        TLObject tLObject2 = tLObject;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("send request " + tLObject2 + " with token = " + i);
        } else {
            int i5 = i;
        }
        try {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
            tLObject2.serializeToStream(nativeByteBuffer);
            tLObject.freeResources();
            try {
                native_sendRequest(this.currentAccount, nativeByteBuffer.address, new RequestDelegateInternal(requestDelegate) {
                    private final /* synthetic */ RequestDelegate f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(long j, int i, String str, int i2) {
                        ConnectionsManager.lambda$null$1(TLObject.this, this.f$1, j, i, str, i2);
                    }
                }, quickAckDelegate, writeToSocketDelegate, i2, i3, i4, z, i);
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$null$1(TLObject tLObject, RequestDelegate requestDelegate, long j, int i, String str, int i2) {
        TLRPC$TL_error tLRPC$TL_error;
        TLObject tLObject2 = null;
        if (j != 0) {
            try {
                NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
                wrap.reused = true;
                TLObject deserializeResponse = tLObject.deserializeResponse(wrap, wrap.readInt32(true), true);
                tLRPC$TL_error = null;
                tLObject2 = deserializeResponse;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return;
            }
        } else if (str != null) {
            tLRPC$TL_error = new TLRPC$TL_error();
            tLRPC$TL_error.code = i;
            tLRPC$TL_error.text = str;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(tLObject + " got error " + tLRPC$TL_error.code + " " + tLRPC$TL_error.text);
            }
        } else {
            tLRPC$TL_error = null;
        }
        if (tLObject2 != null) {
            tLObject2.networkType = i2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("java received " + tLObject2 + " error = " + tLRPC$TL_error);
        }
        Utilities.stageQueue.postRunnable(new Runnable(tLObject2, tLRPC$TL_error) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC$TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ConnectionsManager.lambda$null$0(RequestDelegate.this, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$null$0(RequestDelegate requestDelegate, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        requestDelegate.run(tLObject, tLRPC$TL_error);
        if (tLObject != null) {
            tLObject.freeResources();
        }
    }

    public void cancelRequest(int i, boolean z) {
        native_cancelRequest(this.currentAccount, i, z);
    }

    public void cleanup(boolean z) {
        native_cleanUp(this.currentAccount, z);
    }

    public void cancelRequestsForGuid(int i) {
        native_cancelRequestsForGuid(this.currentAccount, i);
    }

    public void bindRequestToGuid(int i, int i2) {
        native_bindRequestToGuid(this.currentAccount, i, i2);
    }

    public void applyDatacenterAddress(int i, String str, int i2) {
        native_applyDatacenterAddress(this.currentAccount, i, str, i2);
    }

    public int getConnectionState() {
        if (this.connectionState != 3 || !this.isUpdating) {
            return this.connectionState;
        }
        return 5;
    }

    public void setUserId(int i) {
        native_setUserId(this.currentAccount, i);
    }

    public void checkConnection() {
        native_setUseIpv6(this.currentAccount, useIpv6Address());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean z) {
        native_setPushConnectionEnabled(this.currentAccount, z);
    }

    public void init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i4, int i5, boolean z) {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String string = sharedPreferences.getString("proxy_ip", "");
        String string2 = sharedPreferences.getString("proxy_user", "");
        String string3 = sharedPreferences.getString("proxy_pass", "");
        String string4 = sharedPreferences.getString("proxy_secret", "");
        int i6 = sharedPreferences.getInt("proxy_port", 1080);
        if (sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(string)) {
            native_setProxySettings(this.currentAccount, string, i6, string2, string3, string4);
        }
        native_init(this.currentAccount, i, i2, i3, str, str2, str3, str4, str5, str6, str7, str8, str9, i4, i5, z, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
        checkConnection();
    }

    public static void setLangCode(String str) {
        String lowerCase = str.replace('_', '-').toLowerCase();
        for (int i = 0; i < 3; i++) {
            native_setLangCode(i, lowerCase);
        }
    }

    public static void setRegId(String str, String str2) {
        if (TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            str = str2;
        }
        for (int i = 0; i < 3; i++) {
            native_setRegId(i, str);
        }
    }

    public static void setSystemLangCode(String str) {
        String lowerCase = str.replace('_', '-').toLowerCase();
        for (int i = 0; i < 3; i++) {
            native_setSystemLangCode(i, lowerCase);
        }
    }

    public void switchBackend() {
        MessagesController.getGlobalMainSettings().edit().remove("language_showed2").commit();
        native_switchBackend(this.currentAccount);
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(this.currentAccount, true);
    }

    public void updateDcSettings() {
        native_updateDcSettings(this.currentAccount);
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public long checkProxy(String str, int i, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return native_checkProxy(this.currentAccount, str == null ? "" : str, i, str2 == null ? "" : str2, str3 == null ? "" : str3, str4 == null ? "" : str4, requestTimeDelegate);
    }

    public void setAppPaused(boolean z, boolean z2) {
        if (!z2) {
            this.appPaused = z;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app paused = " + z);
            }
            if (z) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app resume count " + this.appResumeCount);
            }
            if (this.appResumeCount < 0) {
                this.appResumeCount = 0;
            }
        }
        if (this.appResumeCount == 0) {
            if (this.lastPauseTime == 0) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork(this.currentAccount);
        } else if (!this.appPaused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset app pause time");
            }
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > 5000) {
                getContactsController().checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long j, int i) {
        try {
            NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
            wrap.reused = true;
            int readInt32 = wrap.readInt32(true);
            TLObject TLdeserialize = TLClassStore.Instance().TLdeserialize(wrap, readInt32, true);
            if (TLdeserialize instanceof TLRPC$Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("java received " + TLdeserialize);
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new Runnable(i, TLdeserialize) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ TLObject f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AccountInstance.getInstance(this.f$0).getMessagesController().processUpdates((TLRPC$Updates) this.f$1, false);
                    }
                });
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", new Object[]{Integer.valueOf(readInt32)}));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdate(int i) {
        Utilities.stageQueue.postRunnable(new Runnable(i) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                AccountInstance.getInstance(this.f$0).getMessagesController().updateTimerProc();
            }
        });
    }

    public static void onSessionCreated(int i) {
        Utilities.stageQueue.postRunnable(new Runnable(i) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                AccountInstance.getInstance(this.f$0).getMessagesController().getDifference();
            }
        });
    }

    public static void onConnectionStateChanged(int i, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable(i2, i) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.lambda$onConnectionStateChanged$6(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$onConnectionStateChanged$6(int i, int i2) {
        getInstance(i).connectionState = i2;
        AccountInstance.getInstance(i).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ConnectionsManager.lambda$onLogout$7(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$onLogout$7(int i) {
        AccountInstance instance = AccountInstance.getInstance(i);
        if (instance.getUserConfig().getClientUserId() != 0) {
            instance.getUserConfig().clearConfig();
            instance.getMessagesController().performLogout(0);
        }
    }

    public static int getInitFlags() {
        int i = EmuDetector.with(ApplicationLoader.applicationContext).detect() ? 1024 : 0;
        try {
            return "com.android.vending".equals(ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName())) ? i | 2048 : i;
        } catch (Throwable unused) {
            return i;
        }
    }

    public static void onBytesSent(int i, int i2, int i3) {
        try {
            AccountInstance.getInstance(i3).getStatsController().incrementSentBytesCount(i2, 6, (long) i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onRequestNewServerIpAndPort(int i, int i2) {
        Utilities.stageQueue.postRunnable(new Runnable(i, i2) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.lambda$onRequestNewServerIpAndPort$8(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$onRequestNewServerIpAndPort$8(int i, int i2) {
        if (currentTask == null && ((i != 0 || Math.abs(lastDnsRequestTime - System.currentTimeMillis()) >= 10000) && ApplicationLoader.isNetworkOnline())) {
            lastDnsRequestTime = System.currentTimeMillis();
            if (i == 3) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start mozilla txt task");
                }
                MozillaDnsLoadTask mozillaDnsLoadTask = new MozillaDnsLoadTask(i2);
                mozillaDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = mozillaDnsLoadTask;
            } else if (i == 2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start google txt task");
                }
                GoogleDnsLoadTask googleDnsLoadTask = new GoogleDnsLoadTask(i2);
                googleDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = googleDnsLoadTask;
            } else if (i == 1) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask dnsTxtLoadTask = new DnsTxtLoadTask(i2);
                dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = dnsTxtLoadTask;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start firebase task");
                }
                FirebaseTask firebaseTask = new FirebaseTask(i2);
                firebaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = firebaseTask;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("don't start task, current task = " + currentTask + " next task = " + i + " time diff = " + Math.abs(lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ApplicationLoader.isNetworkOnline());
        }
    }

    public static void onProxyError() {
        AndroidUtilities.runOnUIThread($$Lambda$ConnectionsManager$24reh3bpM2JkWgNeS0uACIxcdSU.INSTANCE);
    }

    public static void getHostByName(String str, long j) {
        AndroidUtilities.runOnUIThread(new Runnable(str, j) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ long f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.lambda$getHostByName$10(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$getHostByName$10(String str, long j) {
        ResolvedDomain resolvedDomain = dnsCache.get(str);
        if (resolvedDomain == null || SystemClock.elapsedRealtime() - resolvedDomain.ttl >= 300000) {
            ResolveHostByNameTask resolveHostByNameTask = resolvingHostnameTasks.get(str);
            if (resolveHostByNameTask == null) {
                resolveHostByNameTask = new ResolveHostByNameTask(str);
                try {
                    resolveHostByNameTask.executeOnExecutor(DNS_THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    resolvingHostnameTasks.put(str, resolveHostByNameTask);
                } catch (Throwable th) {
                    FileLog.e(th);
                    native_onHostNameResolved(str, j, "");
                    return;
                }
            }
            resolveHostByNameTask.addAddress(j);
            return;
        }
        native_onHostNameResolved(str, j, resolvedDomain.getAddress());
    }

    public static void onBytesReceived(int i, int i2, int i3) {
        try {
            StatsController.getInstance(i3).incrementReceivedBytesCount(i2, 6, (long) i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdateConfig(long j, int i) {
        try {
            NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
            wrap.reused = true;
            TLRPC$TL_config TLdeserialize = TLRPC$TL_config.TLdeserialize(wrap, wrap.readInt32(true), true);
            if (TLdeserialize != null) {
                Utilities.stageQueue.postRunnable(new Runnable(i, TLdeserialize) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ TLRPC$TL_config f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AccountInstance.getInstance(this.f$0).getMessagesController().updateConfig(this.f$1);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onInternalPushReceived(int i) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean z, String str, int i, String str2, String str3, String str4) {
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str3 == null) {
            str3 = "";
        }
        if (str4 == null) {
            str4 = "";
        }
        for (int i2 = 0; i2 < 3; i2++) {
            if (!z || TextUtils.isEmpty(str)) {
                native_setProxySettings(i2, "", 1080, "", "", "");
            } else {
                native_setProxySettings(i2, str, i, str2, str3, str4);
            }
            AccountInstance instance = AccountInstance.getInstance(i2);
            if (instance.getUserConfig().isClientActivated()) {
                instance.getMessagesController().checkPromoInfo(true);
            }
        }
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public void setIsUpdating(boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ConnectionsManager.this.lambda$setIsUpdating$12$ConnectionsManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setIsUpdating$12$ConnectionsManager(boolean z) {
        if (this.isUpdating != z) {
            this.isUpdating = z;
            if (this.connectionState == 3) {
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
            }
        }
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        if (BuildVars.LOGS_ENABLED) {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface nextElement = networkInterfaces.nextElement();
                    if (nextElement.isUp() && !nextElement.isLoopback()) {
                        if (!nextElement.getInterfaceAddresses().isEmpty()) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("valid interface: " + nextElement);
                            }
                            List<InterfaceAddress> interfaceAddresses = nextElement.getInterfaceAddresses();
                            for (int i = 0; i < interfaceAddresses.size(); i++) {
                                InetAddress address = interfaceAddresses.get(i).getAddress();
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("address: " + address.getHostAddress());
                                }
                                if (!address.isLinkLocalAddress() && !address.isLoopbackAddress()) {
                                    if (!address.isMulticastAddress()) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("address is good");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            boolean z = false;
            boolean z2 = false;
            while (networkInterfaces2.hasMoreElements()) {
                NetworkInterface nextElement2 = networkInterfaces2.nextElement();
                if (nextElement2.isUp()) {
                    if (!nextElement2.isLoopback()) {
                        List<InterfaceAddress> interfaceAddresses2 = nextElement2.getInterfaceAddresses();
                        for (int i2 = 0; i2 < interfaceAddresses2.size(); i2++) {
                            InetAddress address2 = interfaceAddresses2.get(i2).getAddress();
                            if (!address2.isLinkLocalAddress() && !address2.isLoopbackAddress()) {
                                if (!address2.isMulticastAddress()) {
                                    if (address2 instanceof Inet6Address) {
                                        z2 = true;
                                    } else if ((address2 instanceof Inet4Address) && !address2.getHostAddress().startsWith("192.0.0.")) {
                                        z = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (z || !z2) {
                return false;
            }
            return true;
        } catch (Throwable th2) {
            FileLog.e(th2);
        }
    }

    private static class ResolveHostByNameTask extends AsyncTask<Void, Void, ResolvedDomain> {
        private ArrayList<Long> addresses = new ArrayList<>();
        private String currentHostName;

        public ResolveHostByNameTask(String str) {
            this.currentHostName = str;
        }

        public void addAddress(long j) {
            if (!this.addresses.contains(Long.valueOf(j))) {
                this.addresses.add(Long.valueOf(j));
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x00c3 A[SYNTHETIC, Splitter:B:45:0x00c3] */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00cd A[SYNTHETIC, Splitter:B:50:0x00cd] */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00d4 A[SYNTHETIC, Splitter:B:54:0x00d4] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.ConnectionsManager.ResolvedDomain doInBackground(java.lang.Void... r11) {
            /*
                r10 = this;
                java.lang.String r11 = "Answer"
                r0 = 1
                r1 = 0
                r2 = 0
                java.net.URL r3 = new java.net.URL     // Catch:{ all -> 0x00bb }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bb }
                r4.<init>()     // Catch:{ all -> 0x00bb }
                java.lang.String r5 = "https://www.google.com/resolve?name="
                r4.append(r5)     // Catch:{ all -> 0x00bb }
                java.lang.String r5 = r10.currentHostName     // Catch:{ all -> 0x00bb }
                r4.append(r5)     // Catch:{ all -> 0x00bb }
                java.lang.String r5 = "&type=A"
                r4.append(r5)     // Catch:{ all -> 0x00bb }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00bb }
                r3.<init>(r4)     // Catch:{ all -> 0x00bb }
                java.net.URLConnection r3 = r3.openConnection()     // Catch:{ all -> 0x00bb }
                java.lang.String r4 = "User-Agent"
                java.lang.String r5 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r3.addRequestProperty(r4, r5)     // Catch:{ all -> 0x00bb }
                java.lang.String r4 = "Host"
                java.lang.String r5 = "dns.google.com"
                r3.addRequestProperty(r4, r5)     // Catch:{ all -> 0x00bb }
                r4 = 1000(0x3e8, float:1.401E-42)
                r3.setConnectTimeout(r4)     // Catch:{ all -> 0x00bb }
                r4 = 2000(0x7d0, float:2.803E-42)
                r3.setReadTimeout(r4)     // Catch:{ all -> 0x00bb }
                r3.connect()     // Catch:{ all -> 0x00bb }
                java.io.InputStream r3 = r3.getInputStream()     // Catch:{ all -> 0x00bb }
                java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00b8 }
                r4.<init>()     // Catch:{ all -> 0x00b8 }
                r5 = 32768(0x8000, float:4.5918E-41)
                byte[] r5 = new byte[r5]     // Catch:{ all -> 0x00b6 }
            L_0x004f:
                int r6 = r3.read(r5)     // Catch:{ all -> 0x00b6 }
                if (r6 <= 0) goto L_0x0059
                r4.write(r5, r1, r6)     // Catch:{ all -> 0x00b6 }
                goto L_0x004f
            L_0x0059:
                r5 = -1
                org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x00b6 }
                java.lang.String r6 = new java.lang.String     // Catch:{ all -> 0x00b6 }
                byte[] r7 = r4.toByteArray()     // Catch:{ all -> 0x00b6 }
                r6.<init>(r7)     // Catch:{ all -> 0x00b6 }
                r5.<init>(r6)     // Catch:{ all -> 0x00b6 }
                boolean r6 = r5.has(r11)     // Catch:{ all -> 0x00b6 }
                if (r6 == 0) goto L_0x00a7
                org.json.JSONArray r11 = r5.getJSONArray(r11)     // Catch:{ all -> 0x00b6 }
                int r5 = r11.length()     // Catch:{ all -> 0x00b6 }
                if (r5 <= 0) goto L_0x00a7
                java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x00b6 }
                r6.<init>(r5)     // Catch:{ all -> 0x00b6 }
                r7 = 0
            L_0x007e:
                if (r7 >= r5) goto L_0x0090
                org.json.JSONObject r8 = r11.getJSONObject(r7)     // Catch:{ all -> 0x00b6 }
                java.lang.String r9 = "data"
                java.lang.String r8 = r8.getString(r9)     // Catch:{ all -> 0x00b6 }
                r6.add(r8)     // Catch:{ all -> 0x00b6 }
                int r7 = r7 + 1
                goto L_0x007e
            L_0x0090:
                org.telegram.tgnet.ConnectionsManager$ResolvedDomain r11 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ all -> 0x00b6 }
                long r7 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00b6 }
                r11.<init>(r6, r7)     // Catch:{ all -> 0x00b6 }
                if (r3 == 0) goto L_0x00a3
                r3.close()     // Catch:{ all -> 0x009f }
                goto L_0x00a3
            L_0x009f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00a3:
                r4.close()     // Catch:{ Exception -> 0x00a6 }
            L_0x00a6:
                return r11
            L_0x00a7:
                if (r3 == 0) goto L_0x00b1
                r3.close()     // Catch:{ all -> 0x00ad }
                goto L_0x00b1
            L_0x00ad:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x00b1:
                r4.close()     // Catch:{ Exception -> 0x00b4 }
            L_0x00b4:
                r1 = 1
                goto L_0x00d2
            L_0x00b6:
                r11 = move-exception
                goto L_0x00be
            L_0x00b8:
                r11 = move-exception
                r4 = r2
                goto L_0x00be
            L_0x00bb:
                r11 = move-exception
                r3 = r2
                r4 = r3
            L_0x00be:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)     // Catch:{ all -> 0x00f5 }
                if (r3 == 0) goto L_0x00cb
                r3.close()     // Catch:{ all -> 0x00c7 }
                goto L_0x00cb
            L_0x00c7:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x00cb:
                if (r4 == 0) goto L_0x00d2
                r4.close()     // Catch:{ Exception -> 0x00d1 }
                goto L_0x00d2
            L_0x00d1:
            L_0x00d2:
                if (r1 != 0) goto L_0x00f4
                java.lang.String r11 = r10.currentHostName     // Catch:{ Exception -> 0x00f0 }
                java.net.InetAddress r11 = java.net.InetAddress.getByName(r11)     // Catch:{ Exception -> 0x00f0 }
                java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x00f0 }
                r1.<init>(r0)     // Catch:{ Exception -> 0x00f0 }
                java.lang.String r11 = r11.getHostAddress()     // Catch:{ Exception -> 0x00f0 }
                r1.add(r11)     // Catch:{ Exception -> 0x00f0 }
                org.telegram.tgnet.ConnectionsManager$ResolvedDomain r11 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ Exception -> 0x00f0 }
                long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x00f0 }
                r11.<init>(r1, r3)     // Catch:{ Exception -> 0x00f0 }
                return r11
            L_0x00f0:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x00f4:
                return r2
            L_0x00f5:
                r11 = move-exception
                if (r3 == 0) goto L_0x0100
                r3.close()     // Catch:{ all -> 0x00fc }
                goto L_0x0100
            L_0x00fc:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0100:
                if (r4 == 0) goto L_0x0105
                r4.close()     // Catch:{ Exception -> 0x0105 }
            L_0x0105:
                goto L_0x0107
            L_0x0106:
                throw r11
            L_0x0107:
                goto L_0x0106
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.ResolveHostByNameTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.ConnectionsManager$ResolvedDomain");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(ResolvedDomain resolvedDomain) {
            int i = 0;
            if (resolvedDomain != null) {
                ConnectionsManager.dnsCache.put(this.currentHostName, resolvedDomain);
                int size = this.addresses.size();
                while (i < size) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(i).longValue(), resolvedDomain.getAddress());
                    i++;
                }
            } else {
                int size2 = this.addresses.size();
                while (i < size2) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(i).longValue(), "");
                    i++;
                }
            }
            ConnectionsManager.resolvingHostnameTasks.remove(this.currentHostName);
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public DnsTxtLoadTask(int i) {
            this.currentAccount = i;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x014a A[SYNTHETIC, Splitter:B:55:0x014a] */
        /* JADX WARNING: Removed duplicated region for block: B:60:0x0154 A[SYNTHETIC, Splitter:B:60:0x0154] */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x0157 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r14) {
            /*
                r13 = this;
                r14 = 0
                r0 = 0
                r2 = r14
                r3 = r2
                r1 = 0
            L_0x0005:
                r4 = 3
                if (r1 >= r4) goto L_0x016c
                if (r1 != 0) goto L_0x0010
                java.lang.String r4 = "www.google.com"
                goto L_0x0018
            L_0x000d:
                r4 = move-exception
                goto L_0x0145
            L_0x0010:
                r4 = 1
                if (r1 != r4) goto L_0x0016
                java.lang.String r4 = "www.google.ru"
                goto L_0x0018
            L_0x0016:
                java.lang.String r4 = "google.com"
            L_0x0018:
                int r5 = r13.currentAccount     // Catch:{ all -> 0x000d }
                int r5 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r5)     // Catch:{ all -> 0x000d }
                if (r5 == 0) goto L_0x0023
                java.lang.String r5 = "tapv3.stel.com"
                goto L_0x002f
            L_0x0023:
                int r5 = r13.currentAccount     // Catch:{ all -> 0x000d }
                org.telegram.messenger.AccountInstance r5 = org.telegram.messenger.AccountInstance.getInstance(r5)     // Catch:{ all -> 0x000d }
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()     // Catch:{ all -> 0x000d }
                java.lang.String r5 = r5.dcDomainName     // Catch:{ all -> 0x000d }
            L_0x002f:
                java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x000d }
                r7 = 116(0x74, float:1.63E-43)
                int r6 = r6.nextInt(r7)     // Catch:{ all -> 0x000d }
                int r6 = r6 + 13
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x000d }
                r7.<init>(r6)     // Catch:{ all -> 0x000d }
                r8 = 0
            L_0x003f:
                if (r8 >= r6) goto L_0x0055
                java.lang.String r9 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM"
                java.security.SecureRandom r10 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x000d }
                r11 = 62
                int r10 = r10.nextInt(r11)     // Catch:{ all -> 0x000d }
                char r9 = r9.charAt(r10)     // Catch:{ all -> 0x000d }
                r7.append(r9)     // Catch:{ all -> 0x000d }
                int r8 = r8 + 1
                goto L_0x003f
            L_0x0055:
                java.net.URL r6 = new java.net.URL     // Catch:{ all -> 0x000d }
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x000d }
                r8.<init>()     // Catch:{ all -> 0x000d }
                java.lang.String r9 = "https://"
                r8.append(r9)     // Catch:{ all -> 0x000d }
                r8.append(r4)     // Catch:{ all -> 0x000d }
                java.lang.String r4 = "/resolve?name="
                r8.append(r4)     // Catch:{ all -> 0x000d }
                r8.append(r5)     // Catch:{ all -> 0x000d }
                java.lang.String r4 = "&type=ANY&random_padding="
                r8.append(r4)     // Catch:{ all -> 0x000d }
                r8.append(r7)     // Catch:{ all -> 0x000d }
                java.lang.String r4 = r8.toString()     // Catch:{ all -> 0x000d }
                r6.<init>(r4)     // Catch:{ all -> 0x000d }
                java.net.URLConnection r4 = r6.openConnection()     // Catch:{ all -> 0x000d }
                java.lang.String r5 = "User-Agent"
                java.lang.String r6 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r4.addRequestProperty(r5, r6)     // Catch:{ all -> 0x000d }
                java.lang.String r5 = "Host"
                java.lang.String r6 = "dns.google.com"
                r4.addRequestProperty(r5, r6)     // Catch:{ all -> 0x000d }
                r5 = 5000(0x1388, float:7.006E-42)
                r4.setConnectTimeout(r5)     // Catch:{ all -> 0x000d }
                r4.setReadTimeout(r5)     // Catch:{ all -> 0x000d }
                r4.connect()     // Catch:{ all -> 0x000d }
                java.io.InputStream r3 = r4.getInputStream()     // Catch:{ all -> 0x000d }
                long r4 = r4.getDate()     // Catch:{ all -> 0x000d }
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r5 = (int) r4     // Catch:{ all -> 0x000d }
                r13.responseDate = r5     // Catch:{ all -> 0x000d }
                java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x000d }
                r4.<init>()     // Catch:{ all -> 0x000d }
                r2 = 32768(0x8000, float:4.5918E-41)
                byte[] r2 = new byte[r2]     // Catch:{ all -> 0x0141 }
            L_0x00b0:
                boolean r5 = r13.isCancelled()     // Catch:{ all -> 0x0141 }
                if (r5 == 0) goto L_0x00b7
                goto L_0x00c2
            L_0x00b7:
                int r5 = r3.read(r2)     // Catch:{ all -> 0x0141 }
                if (r5 <= 0) goto L_0x00c1
                r4.write(r2, r0, r5)     // Catch:{ all -> 0x0141 }
                goto L_0x00b0
            L_0x00c1:
                r2 = -1
            L_0x00c2:
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x0141 }
                java.lang.String r5 = new java.lang.String     // Catch:{ all -> 0x0141 }
                byte[] r6 = r4.toByteArray()     // Catch:{ all -> 0x0141 }
                r5.<init>(r6)     // Catch:{ all -> 0x0141 }
                r2.<init>(r5)     // Catch:{ all -> 0x0141 }
                java.lang.String r5 = "Answer"
                org.json.JSONArray r2 = r2.getJSONArray(r5)     // Catch:{ all -> 0x0141 }
                int r5 = r2.length()     // Catch:{ all -> 0x0141 }
                java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0141 }
                r6.<init>(r5)     // Catch:{ all -> 0x0141 }
                r7 = 0
            L_0x00e0:
                if (r7 >= r5) goto L_0x00fd
                org.json.JSONObject r8 = r2.getJSONObject(r7)     // Catch:{ all -> 0x0141 }
                java.lang.String r9 = "type"
                int r9 = r8.getInt(r9)     // Catch:{ all -> 0x0141 }
                r10 = 16
                if (r9 == r10) goto L_0x00f1
                goto L_0x00fa
            L_0x00f1:
                java.lang.String r9 = "data"
                java.lang.String r8 = r8.getString(r9)     // Catch:{ all -> 0x0141 }
                r6.add(r8)     // Catch:{ all -> 0x0141 }
            L_0x00fa:
                int r7 = r7 + 1
                goto L_0x00e0
            L_0x00fd:
                org.telegram.tgnet.-$$Lambda$ConnectionsManager$DnsTxtLoadTask$BEcjqZFmP4raPbtfXzTVfRUBAsw r2 = org.telegram.tgnet.$$Lambda$ConnectionsManager$DnsTxtLoadTask$BEcjqZFmP4raPbtfXzTVfRUBAsw.INSTANCE     // Catch:{ all -> 0x0141 }
                java.util.Collections.sort(r6, r2)     // Catch:{ all -> 0x0141 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0141 }
                r2.<init>()     // Catch:{ all -> 0x0141 }
                r5 = 0
            L_0x0108:
                int r7 = r6.size()     // Catch:{ all -> 0x0141 }
                if (r5 >= r7) goto L_0x0122
                java.lang.Object r7 = r6.get(r5)     // Catch:{ all -> 0x0141 }
                java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0141 }
                java.lang.String r8 = "\""
                java.lang.String r9 = ""
                java.lang.String r7 = r7.replace(r8, r9)     // Catch:{ all -> 0x0141 }
                r2.append(r7)     // Catch:{ all -> 0x0141 }
                int r5 = r5 + 1
                goto L_0x0108
            L_0x0122:
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0141 }
                byte[] r2 = android.util.Base64.decode(r2, r0)     // Catch:{ all -> 0x0141 }
                org.telegram.tgnet.NativeByteBuffer r5 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x0141 }
                int r6 = r2.length     // Catch:{ all -> 0x0141 }
                r5.<init>((int) r6)     // Catch:{ all -> 0x0141 }
                r5.writeBytes((byte[]) r2)     // Catch:{ all -> 0x0141 }
                if (r3 == 0) goto L_0x013d
                r3.close()     // Catch:{ all -> 0x0139 }
                goto L_0x013d
            L_0x0139:
                r14 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
            L_0x013d:
                r4.close()     // Catch:{ Exception -> 0x0140 }
            L_0x0140:
                return r5
            L_0x0141:
                r2 = move-exception
                r12 = r4
                r4 = r2
                r2 = r12
            L_0x0145:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x015b }
                if (r3 == 0) goto L_0x0152
                r3.close()     // Catch:{ all -> 0x014e }
                goto L_0x0152
            L_0x014e:
                r4 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
            L_0x0152:
                if (r2 == 0) goto L_0x0157
                r2.close()     // Catch:{ Exception -> 0x0157 }
            L_0x0157:
                int r1 = r1 + 1
                goto L_0x0005
            L_0x015b:
                r14 = move-exception
                if (r3 == 0) goto L_0x0166
                r3.close()     // Catch:{ all -> 0x0162 }
                goto L_0x0166
            L_0x0162:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0166:
                if (r2 == 0) goto L_0x016b
                r2.close()     // Catch:{ Exception -> 0x016b }
            L_0x016b:
                throw r14
            L_0x016c:
                return r14
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.DnsTxtLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new Runnable(nativeByteBuffer) {
                private final /* synthetic */ NativeByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ConnectionsManager.DnsTxtLoadTask.this.lambda$onPostExecute$1$ConnectionsManager$DnsTxtLoadTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$DnsTxtLoadTask(NativeByteBuffer nativeByteBuffer) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (nativeByteBuffer != null) {
                int i = this.currentAccount;
                ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
                FileLog.d("start google task");
            }
            GoogleDnsLoadTask googleDnsLoadTask = new GoogleDnsLoadTask(this.currentAccount);
            googleDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            AsyncTask unused2 = ConnectionsManager.currentTask = googleDnsLoadTask;
        }
    }

    private static class GoogleDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public GoogleDnsLoadTask(int i) {
            this.currentAccount = i;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x012e A[SYNTHETIC, Splitter:B:52:0x012e] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x0138 A[SYNTHETIC, Splitter:B:57:0x0138] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r12) {
            /*
                r11 = this;
                r12 = 0
                int r0 = r11.currentAccount     // Catch:{ all -> 0x0126 }
                int r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0)     // Catch:{ all -> 0x0126 }
                if (r0 == 0) goto L_0x000c
                java.lang.String r0 = "tapv3.stel.com"
                goto L_0x0018
            L_0x000c:
                int r0 = r11.currentAccount     // Catch:{ all -> 0x0126 }
                org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ all -> 0x0126 }
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ all -> 0x0126 }
                java.lang.String r0 = r0.dcDomainName     // Catch:{ all -> 0x0126 }
            L_0x0018:
                java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x0126 }
                r2 = 116(0x74, float:1.63E-43)
                int r1 = r1.nextInt(r2)     // Catch:{ all -> 0x0126 }
                int r1 = r1 + 13
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0126 }
                r2.<init>(r1)     // Catch:{ all -> 0x0126 }
                r3 = 0
                r4 = 0
            L_0x0029:
                if (r4 >= r1) goto L_0x003f
                java.lang.String r5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM"
                java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x0126 }
                r7 = 62
                int r6 = r6.nextInt(r7)     // Catch:{ all -> 0x0126 }
                char r5 = r5.charAt(r6)     // Catch:{ all -> 0x0126 }
                r2.append(r5)     // Catch:{ all -> 0x0126 }
                int r4 = r4 + 1
                goto L_0x0029
            L_0x003f:
                java.net.URL r1 = new java.net.URL     // Catch:{ all -> 0x0126 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0126 }
                r4.<init>()     // Catch:{ all -> 0x0126 }
                java.lang.String r5 = "https://dns.google.com/resolve?name="
                r4.append(r5)     // Catch:{ all -> 0x0126 }
                r4.append(r0)     // Catch:{ all -> 0x0126 }
                java.lang.String r0 = "&type=ANY&random_padding="
                r4.append(r0)     // Catch:{ all -> 0x0126 }
                r4.append(r2)     // Catch:{ all -> 0x0126 }
                java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0126 }
                r1.<init>(r0)     // Catch:{ all -> 0x0126 }
                java.net.URLConnection r0 = r1.openConnection()     // Catch:{ all -> 0x0126 }
                java.lang.String r1 = "User-Agent"
                java.lang.String r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x0126 }
                r1 = 5000(0x1388, float:7.006E-42)
                r0.setConnectTimeout(r1)     // Catch:{ all -> 0x0126 }
                r0.setReadTimeout(r1)     // Catch:{ all -> 0x0126 }
                r0.connect()     // Catch:{ all -> 0x0126 }
                java.io.InputStream r1 = r0.getInputStream()     // Catch:{ all -> 0x0126 }
                long r4 = r0.getDate()     // Catch:{ all -> 0x0122 }
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r0 = (int) r4     // Catch:{ all -> 0x0122 }
                r11.responseDate = r0     // Catch:{ all -> 0x0122 }
                java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0122 }
                r0.<init>()     // Catch:{ all -> 0x0122 }
                r2 = 32768(0x8000, float:4.5918E-41)
                byte[] r2 = new byte[r2]     // Catch:{ all -> 0x011c }
            L_0x008b:
                boolean r4 = r11.isCancelled()     // Catch:{ all -> 0x011c }
                if (r4 == 0) goto L_0x0092
                goto L_0x009d
            L_0x0092:
                int r4 = r1.read(r2)     // Catch:{ all -> 0x011c }
                if (r4 <= 0) goto L_0x009c
                r0.write(r2, r3, r4)     // Catch:{ all -> 0x011c }
                goto L_0x008b
            L_0x009c:
                r2 = -1
            L_0x009d:
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x011c }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x011c }
                byte[] r5 = r0.toByteArray()     // Catch:{ all -> 0x011c }
                r4.<init>(r5)     // Catch:{ all -> 0x011c }
                r2.<init>(r4)     // Catch:{ all -> 0x011c }
                java.lang.String r4 = "Answer"
                org.json.JSONArray r2 = r2.getJSONArray(r4)     // Catch:{ all -> 0x011c }
                int r4 = r2.length()     // Catch:{ all -> 0x011c }
                java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x011c }
                r5.<init>(r4)     // Catch:{ all -> 0x011c }
                r6 = 0
            L_0x00bb:
                if (r6 >= r4) goto L_0x00d8
                org.json.JSONObject r7 = r2.getJSONObject(r6)     // Catch:{ all -> 0x011c }
                java.lang.String r8 = "type"
                int r8 = r7.getInt(r8)     // Catch:{ all -> 0x011c }
                r9 = 16
                if (r8 == r9) goto L_0x00cc
                goto L_0x00d5
            L_0x00cc:
                java.lang.String r8 = "data"
                java.lang.String r7 = r7.getString(r8)     // Catch:{ all -> 0x011c }
                r5.add(r7)     // Catch:{ all -> 0x011c }
            L_0x00d5:
                int r6 = r6 + 1
                goto L_0x00bb
            L_0x00d8:
                org.telegram.tgnet.-$$Lambda$ConnectionsManager$GoogleDnsLoadTask$7geEd5QmUa4Hgb9F_2w_Jc5Hu4M r2 = org.telegram.tgnet.$$Lambda$ConnectionsManager$GoogleDnsLoadTask$7geEd5QmUa4Hgb9F_2w_Jc5Hu4M.INSTANCE     // Catch:{ all -> 0x011c }
                java.util.Collections.sort(r5, r2)     // Catch:{ all -> 0x011c }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x011c }
                r2.<init>()     // Catch:{ all -> 0x011c }
                r4 = 0
            L_0x00e3:
                int r6 = r5.size()     // Catch:{ all -> 0x011c }
                if (r4 >= r6) goto L_0x00fd
                java.lang.Object r6 = r5.get(r4)     // Catch:{ all -> 0x011c }
                java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x011c }
                java.lang.String r7 = "\""
                java.lang.String r8 = ""
                java.lang.String r6 = r6.replace(r7, r8)     // Catch:{ all -> 0x011c }
                r2.append(r6)     // Catch:{ all -> 0x011c }
                int r4 = r4 + 1
                goto L_0x00e3
            L_0x00fd:
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x011c }
                byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x011c }
                org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x011c }
                int r4 = r2.length     // Catch:{ all -> 0x011c }
                r3.<init>((int) r4)     // Catch:{ all -> 0x011c }
                r3.writeBytes((byte[]) r2)     // Catch:{ all -> 0x011c }
                if (r1 == 0) goto L_0x0118
                r1.close()     // Catch:{ all -> 0x0114 }
                goto L_0x0118
            L_0x0114:
                r12 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            L_0x0118:
                r0.close()     // Catch:{ Exception -> 0x011b }
            L_0x011b:
                return r3
            L_0x011c:
                r2 = move-exception
                r10 = r1
                r1 = r0
                r0 = r2
                r2 = r10
                goto L_0x0129
            L_0x0122:
                r0 = move-exception
                r2 = r1
                r1 = r12
                goto L_0x0129
            L_0x0126:
                r0 = move-exception
                r1 = r12
                r2 = r1
            L_0x0129:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x013c }
                if (r2 == 0) goto L_0x0136
                r2.close()     // Catch:{ all -> 0x0132 }
                goto L_0x0136
            L_0x0132:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0136:
                if (r1 == 0) goto L_0x013b
                r1.close()     // Catch:{ Exception -> 0x013b }
            L_0x013b:
                return r12
            L_0x013c:
                r12 = move-exception
                if (r2 == 0) goto L_0x0147
                r2.close()     // Catch:{ all -> 0x0143 }
                goto L_0x0147
            L_0x0143:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0147:
                if (r1 == 0) goto L_0x014c
                r1.close()     // Catch:{ Exception -> 0x014c }
            L_0x014c:
                goto L_0x014e
            L_0x014d:
                throw r12
            L_0x014e:
                goto L_0x014d
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.GoogleDnsLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new Runnable(nativeByteBuffer) {
                private final /* synthetic */ NativeByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ConnectionsManager.GoogleDnsLoadTask.this.lambda$onPostExecute$1$ConnectionsManager$GoogleDnsLoadTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$GoogleDnsLoadTask(NativeByteBuffer nativeByteBuffer) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (nativeByteBuffer != null) {
                int i = this.currentAccount;
                ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get google result");
                FileLog.d("start mozilla task");
            }
            MozillaDnsLoadTask mozillaDnsLoadTask = new MozillaDnsLoadTask(this.currentAccount);
            mozillaDnsLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            AsyncTask unused2 = ConnectionsManager.currentTask = mozillaDnsLoadTask;
        }
    }

    private static class MozillaDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public MozillaDnsLoadTask(int i) {
            this.currentAccount = i;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0135 A[SYNTHETIC, Splitter:B:52:0x0135] */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x013f A[SYNTHETIC, Splitter:B:57:0x013f] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r12) {
            /*
                r11 = this;
                r12 = 0
                int r0 = r11.currentAccount     // Catch:{ all -> 0x012d }
                int r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0)     // Catch:{ all -> 0x012d }
                if (r0 == 0) goto L_0x000c
                java.lang.String r0 = "tapv3.stel.com"
                goto L_0x0018
            L_0x000c:
                int r0 = r11.currentAccount     // Catch:{ all -> 0x012d }
                org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ all -> 0x012d }
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ all -> 0x012d }
                java.lang.String r0 = r0.dcDomainName     // Catch:{ all -> 0x012d }
            L_0x0018:
                java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x012d }
                r2 = 116(0x74, float:1.63E-43)
                int r1 = r1.nextInt(r2)     // Catch:{ all -> 0x012d }
                int r1 = r1 + 13
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x012d }
                r2.<init>(r1)     // Catch:{ all -> 0x012d }
                r3 = 0
                r4 = 0
            L_0x0029:
                if (r4 >= r1) goto L_0x003f
                java.lang.String r5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM"
                java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x012d }
                r7 = 62
                int r6 = r6.nextInt(r7)     // Catch:{ all -> 0x012d }
                char r5 = r5.charAt(r6)     // Catch:{ all -> 0x012d }
                r2.append(r5)     // Catch:{ all -> 0x012d }
                int r4 = r4 + 1
                goto L_0x0029
            L_0x003f:
                java.net.URL r1 = new java.net.URL     // Catch:{ all -> 0x012d }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x012d }
                r4.<init>()     // Catch:{ all -> 0x012d }
                java.lang.String r5 = "https://mozilla.cloudflare-dns.com/dns-query?name="
                r4.append(r5)     // Catch:{ all -> 0x012d }
                r4.append(r0)     // Catch:{ all -> 0x012d }
                java.lang.String r0 = "&type=TXT&random_padding="
                r4.append(r0)     // Catch:{ all -> 0x012d }
                r4.append(r2)     // Catch:{ all -> 0x012d }
                java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x012d }
                r1.<init>(r0)     // Catch:{ all -> 0x012d }
                java.net.URLConnection r0 = r1.openConnection()     // Catch:{ all -> 0x012d }
                java.lang.String r1 = "User-Agent"
                java.lang.String r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x012d }
                java.lang.String r1 = "accept"
                java.lang.String r2 = "application/dns-json"
                r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x012d }
                r1 = 5000(0x1388, float:7.006E-42)
                r0.setConnectTimeout(r1)     // Catch:{ all -> 0x012d }
                r0.setReadTimeout(r1)     // Catch:{ all -> 0x012d }
                r0.connect()     // Catch:{ all -> 0x012d }
                java.io.InputStream r1 = r0.getInputStream()     // Catch:{ all -> 0x012d }
                long r4 = r0.getDate()     // Catch:{ all -> 0x0129 }
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r0 = (int) r4     // Catch:{ all -> 0x0129 }
                r11.responseDate = r0     // Catch:{ all -> 0x0129 }
                java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0129 }
                r0.<init>()     // Catch:{ all -> 0x0129 }
                r2 = 32768(0x8000, float:4.5918E-41)
                byte[] r2 = new byte[r2]     // Catch:{ all -> 0x0123 }
            L_0x0092:
                boolean r4 = r11.isCancelled()     // Catch:{ all -> 0x0123 }
                if (r4 == 0) goto L_0x0099
                goto L_0x00a4
            L_0x0099:
                int r4 = r1.read(r2)     // Catch:{ all -> 0x0123 }
                if (r4 <= 0) goto L_0x00a3
                r0.write(r2, r3, r4)     // Catch:{ all -> 0x0123 }
                goto L_0x0092
            L_0x00a3:
                r2 = -1
            L_0x00a4:
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x0123 }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x0123 }
                byte[] r5 = r0.toByteArray()     // Catch:{ all -> 0x0123 }
                r4.<init>(r5)     // Catch:{ all -> 0x0123 }
                r2.<init>(r4)     // Catch:{ all -> 0x0123 }
                java.lang.String r4 = "Answer"
                org.json.JSONArray r2 = r2.getJSONArray(r4)     // Catch:{ all -> 0x0123 }
                int r4 = r2.length()     // Catch:{ all -> 0x0123 }
                java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0123 }
                r5.<init>(r4)     // Catch:{ all -> 0x0123 }
                r6 = 0
            L_0x00c2:
                if (r6 >= r4) goto L_0x00df
                org.json.JSONObject r7 = r2.getJSONObject(r6)     // Catch:{ all -> 0x0123 }
                java.lang.String r8 = "type"
                int r8 = r7.getInt(r8)     // Catch:{ all -> 0x0123 }
                r9 = 16
                if (r8 == r9) goto L_0x00d3
                goto L_0x00dc
            L_0x00d3:
                java.lang.String r8 = "data"
                java.lang.String r7 = r7.getString(r8)     // Catch:{ all -> 0x0123 }
                r5.add(r7)     // Catch:{ all -> 0x0123 }
            L_0x00dc:
                int r6 = r6 + 1
                goto L_0x00c2
            L_0x00df:
                org.telegram.tgnet.-$$Lambda$ConnectionsManager$MozillaDnsLoadTask$ef_f-SQUiYt6RD6fDvDkND7eCLASSNAME r2 = org.telegram.tgnet.$$Lambda$ConnectionsManager$MozillaDnsLoadTask$ef_fSQUiYt6RD6fDvDkND7eCLASSNAME.INSTANCE     // Catch:{ all -> 0x0123 }
                java.util.Collections.sort(r5, r2)     // Catch:{ all -> 0x0123 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0123 }
                r2.<init>()     // Catch:{ all -> 0x0123 }
                r4 = 0
            L_0x00ea:
                int r6 = r5.size()     // Catch:{ all -> 0x0123 }
                if (r4 >= r6) goto L_0x0104
                java.lang.Object r6 = r5.get(r4)     // Catch:{ all -> 0x0123 }
                java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x0123 }
                java.lang.String r7 = "\""
                java.lang.String r8 = ""
                java.lang.String r6 = r6.replace(r7, r8)     // Catch:{ all -> 0x0123 }
                r2.append(r6)     // Catch:{ all -> 0x0123 }
                int r4 = r4 + 1
                goto L_0x00ea
            L_0x0104:
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0123 }
                byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x0123 }
                org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x0123 }
                int r4 = r2.length     // Catch:{ all -> 0x0123 }
                r3.<init>((int) r4)     // Catch:{ all -> 0x0123 }
                r3.writeBytes((byte[]) r2)     // Catch:{ all -> 0x0123 }
                if (r1 == 0) goto L_0x011f
                r1.close()     // Catch:{ all -> 0x011b }
                goto L_0x011f
            L_0x011b:
                r12 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            L_0x011f:
                r0.close()     // Catch:{ Exception -> 0x0122 }
            L_0x0122:
                return r3
            L_0x0123:
                r2 = move-exception
                r10 = r1
                r1 = r0
                r0 = r2
                r2 = r10
                goto L_0x0130
            L_0x0129:
                r0 = move-exception
                r2 = r1
                r1 = r12
                goto L_0x0130
            L_0x012d:
                r0 = move-exception
                r1 = r12
                r2 = r1
            L_0x0130:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0143 }
                if (r2 == 0) goto L_0x013d
                r2.close()     // Catch:{ all -> 0x0139 }
                goto L_0x013d
            L_0x0139:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x013d:
                if (r1 == 0) goto L_0x0142
                r1.close()     // Catch:{ Exception -> 0x0142 }
            L_0x0142:
                return r12
            L_0x0143:
                r12 = move-exception
                if (r2 == 0) goto L_0x014e
                r2.close()     // Catch:{ all -> 0x014a }
                goto L_0x014e
            L_0x014a:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x014e:
                if (r1 == 0) goto L_0x0153
                r1.close()     // Catch:{ Exception -> 0x0153 }
            L_0x0153:
                goto L_0x0155
            L_0x0154:
                throw r12
            L_0x0155:
                goto L_0x0154
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.MozillaDnsLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new Runnable(nativeByteBuffer) {
                private final /* synthetic */ NativeByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ConnectionsManager.MozillaDnsLoadTask.this.lambda$onPostExecute$1$ConnectionsManager$MozillaDnsLoadTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$1$ConnectionsManager$MozillaDnsLoadTask(NativeByteBuffer nativeByteBuffer) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (nativeByteBuffer != null) {
                int i = this.currentAccount;
                ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), this.responseDate);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get mozilla txt result");
            }
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
        }

        public FirebaseTask(int i) {
            this.currentAccount = i;
        }

        /* access modifiers changed from: protected */
        public NativeByteBuffer doInBackground(Void... voidArr) {
            try {
                if (ConnectionsManager.native_isTestBackend(this.currentAccount) == 0) {
                    this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                    FirebaseRemoteConfigSettings.Builder builder = new FirebaseRemoteConfigSettings.Builder();
                    builder.setDeveloperModeEnabled(false);
                    this.firebaseRemoteConfig.setConfigSettings(builder.build());
                    String string = this.firebaseRemoteConfig.getString("ipconfigv3");
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("current firebase value = " + string);
                    }
                    this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new OnCompleteListener() {
                        public final void onComplete(Task task) {
                            ConnectionsManager.FirebaseTask.this.lambda$doInBackground$1$ConnectionsManager$FirebaseTask(task);
                        }
                    });
                    return null;
                }
                throw new Exception("test backend");
            } catch (Throwable th) {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public final void run() {
                        ConnectionsManager.FirebaseTask.this.lambda$doInBackground$2$ConnectionsManager$FirebaseTask();
                    }
                });
                FileLog.e(th);
                return null;
            }
        }

        public /* synthetic */ void lambda$doInBackground$1$ConnectionsManager$FirebaseTask(Task task) {
            Utilities.stageQueue.postRunnable(new Runnable(task.isSuccessful()) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ConnectionsManager.FirebaseTask.this.lambda$null$0$ConnectionsManager$FirebaseTask(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ConnectionsManager$FirebaseTask(boolean z) {
            String str;
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (z) {
                this.firebaseRemoteConfig.activateFetched();
                str = this.firebaseRemoteConfig.getString("ipconfigv3");
            } else {
                str = null;
            }
            if (!TextUtils.isEmpty(str)) {
                byte[] decode = Base64.decode(str, 0);
                try {
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(decode.length);
                    nativeByteBuffer.writeBytes(decode);
                    ConnectionsManager.native_applyDnsConfig(this.currentAccount, nativeByteBuffer.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), (int) (this.firebaseRemoteConfig.getInfo().getFetchTimeMillis() / 1000));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to get firebase result");
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask dnsTxtLoadTask = new DnsTxtLoadTask(this.currentAccount);
                dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                AsyncTask unused2 = ConnectionsManager.currentTask = dnsTxtLoadTask;
            }
        }

        public /* synthetic */ void lambda$doInBackground$2$ConnectionsManager$FirebaseTask() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get firebase result");
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask dnsTxtLoadTask = new DnsTxtLoadTask(this.currentAccount);
            dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            AsyncTask unused = ConnectionsManager.currentTask = dnsTxtLoadTask;
        }
    }
}
