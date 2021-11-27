package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
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
    private static final int MAXIMUM_POOL_SIZE;
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    public static final byte USE_IPV4_IPV6_RANDOM = 2;
    public static final byte USE_IPV4_ONLY = 0;
    public static final byte USE_IPV6_ONLY = 1;
    /* access modifiers changed from: private */
    public static AsyncTask currentTask;
    /* access modifiers changed from: private */
    public static HashMap<String, ResolvedDomain> dnsCache = new HashMap<>();
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    /* access modifiers changed from: private */
    public static HashMap<String, ResolveHostByNameTask> resolvingHostnameTasks = new HashMap<>();
    private static final BlockingQueue<Runnable> sPoolWorkQueue;
    private static final ThreadFactory sThreadFactory;
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

    public static native int native_getCurrentDatacenterId(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i5, long j, boolean z, boolean z2, int i6);

    public static native int native_isTestBackend(int i);

    public static native void native_onHostNameResolved(String str, long j, String str2);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_seSystemLangCode(int i, String str);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setIpStrategy(int i, byte b);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2, boolean z2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3, String str4);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setRegId(int i, String str);

    public static native void native_setSystemLangCode(int i, String str);

    public static native void native_setUserId(int i, long j);

    public static native void native_switchBackend(int i, boolean z);

    public static native void native_updateDcSettings(int i);

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        int max = Math.max(2, Math.min(availableProcessors - 1, 4));
        CORE_POOL_SIZE = max;
        int i = (availableProcessors * 2) + 1;
        MAXIMUM_POOL_SIZE = i;
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(128);
        sPoolWorkQueue = linkedBlockingQueue;
        AnonymousClass1 r10 = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "DnsAsyncTask #" + this.mCount.getAndIncrement());
            }
        };
        sThreadFactory = r10;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(max, i, 30, TimeUnit.SECONDS, linkedBlockingQueue, r10);
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
        init(BuildVars.BUILD_VERSION, 135, BuildVars.APP_ID, str7, str9, str8, str, str6, file2, FileLog.getNetworkLogPath(), getRegId(), AndroidUtilities.getCertificateSHA256Fingerprint(), (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000, getUserConfig().getClientUserId(), isPushConnectionEnabled);
    }

    private String getRegId() {
        String str = SharedConfig.pushString;
        if (TextUtils.isEmpty(str) && !TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            str = SharedConfig.pushStringStatus;
        }
        if (!TextUtils.isEmpty(str)) {
            return str;
        }
        String str2 = "__FIREBASE_GENERATING_SINCE_" + getCurrentTime() + "__";
        SharedConfig.pushStringStatus = str2;
        return str2;
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

    public int getCurrentDatacenterId() {
        return native_getCurrentDatacenterId(this.currentAccount);
    }

    public int getTimeDifference() {
        return native_getTimeDifference(this.currentAccount);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate) {
        return sendRequest(tLObject, requestDelegate, (QuickAckDelegate) null, 0);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, (RequestDelegateTimestamp) null, (QuickAckDelegate) null, (WriteToSocketDelegate) null, i, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i, int i2) {
        return sendRequest(tLObject, requestDelegate, (RequestDelegateTimestamp) null, (QuickAckDelegate) null, (WriteToSocketDelegate) null, i, Integer.MAX_VALUE, i2, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegateTimestamp requestDelegateTimestamp, int i, int i2, int i3) {
        return sendRequest(tLObject, (RequestDelegate) null, requestDelegateTimestamp, (QuickAckDelegate) null, (WriteToSocketDelegate) null, i, i3, i2, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, (RequestDelegateTimestamp) null, quickAckDelegate, (WriteToSocketDelegate) null, i, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i, int i2, int i3, boolean z) {
        return sendRequest(tLObject, requestDelegate, (RequestDelegateTimestamp) null, quickAckDelegate, writeToSocketDelegate, i, i2, i3, z);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, RequestDelegateTimestamp requestDelegateTimestamp, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i, int i2, int i3, boolean z) {
        int andIncrement = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda9(this, tLObject, andIncrement, i2, requestDelegate, requestDelegateTimestamp, quickAckDelegate, writeToSocketDelegate, i, i3, z));
        return andIncrement;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendRequest$2(TLObject tLObject, int i, int i2, RequestDelegate requestDelegate, RequestDelegateTimestamp requestDelegateTimestamp, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i3, int i4, boolean z) {
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
                native_sendRequest(this.currentAccount, nativeByteBuffer.address, new ConnectionsManager$$ExternalSyntheticLambda13(tLObject2, i2, requestDelegate, requestDelegateTimestamp), quickAckDelegate, writeToSocketDelegate, i3, i2, i4, z, i);
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRequest$1(TLObject tLObject, int i, RequestDelegate requestDelegate, RequestDelegateTimestamp requestDelegateTimestamp, long j, int i2, String str, int i3, long j2) {
        TLRPC$TL_error tLRPC$TL_error;
        TLObject tLObject2;
        String str2 = str;
        try {
            if ((tLObject instanceof TLRPC$TL_upload_getFile) && i != 2) {
                Log.d("kek", "error");
            }
            if (j != 0) {
                NativeByteBuffer wrap = NativeByteBuffer.wrap(j);
                wrap.reused = true;
                tLObject2 = tLObject.deserializeResponse(wrap, wrap.readInt32(true), true);
                tLRPC$TL_error = null;
            } else if (str2 != null) {
                TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
                tLRPC$TL_error2.code = i2;
                tLRPC$TL_error2.text = str2;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(tLObject + " got error " + tLRPC$TL_error2.code + " " + tLRPC$TL_error2.text);
                }
                tLRPC$TL_error = tLRPC$TL_error2;
                tLObject2 = null;
            } else {
                tLObject2 = null;
                tLRPC$TL_error = null;
            }
            if (tLObject2 != null) {
                tLObject2.networkType = i3;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("java received " + tLObject2 + " error = " + tLRPC$TL_error);
            }
            Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda11(requestDelegate, tLObject2, tLRPC$TL_error, requestDelegateTimestamp, j2));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendRequest$0(RequestDelegate requestDelegate, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, RequestDelegateTimestamp requestDelegateTimestamp, long j) {
        if (requestDelegate != null) {
            requestDelegate.run(tLObject, tLRPC$TL_error);
        } else if (requestDelegateTimestamp != null) {
            requestDelegateTimestamp.run(tLObject, tLRPC$TL_error, j);
        }
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
        int i = this.connectionState;
        if (i != 3 || !this.isUpdating) {
            return i;
        }
        return 5;
    }

    public void setUserId(long j) {
        native_setUserId(this.currentAccount, j);
    }

    public void checkConnection() {
        native_setIpStrategy(this.currentAccount, getIpStrategy());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean z) {
        native_setPushConnectionEnabled(this.currentAccount, z);
    }

    public void init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i4, long j, boolean z) {
        String str10;
        String str11;
        String str12;
        String str13;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String string = sharedPreferences.getString("proxy_ip", "");
        String string2 = sharedPreferences.getString("proxy_user", "");
        String string3 = sharedPreferences.getString("proxy_pass", "");
        String string4 = sharedPreferences.getString("proxy_secret", "");
        int i5 = sharedPreferences.getInt("proxy_port", 1080);
        if (sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(string)) {
            native_setProxySettings(this.currentAccount, string, i5, string2, string3, string4);
        }
        try {
            str10 = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
        } catch (Throwable unused) {
            str10 = "";
        }
        if (str10 == null) {
            str11 = "";
        } else {
            str11 = str10;
        }
        try {
            str12 = ApplicationLoader.applicationContext.getPackageName();
        } catch (Throwable unused2) {
            str12 = "";
        }
        if (str12 == null) {
            str13 = "";
        } else {
            str13 = str12;
        }
        native_init(this.currentAccount, i, i2, i3, str, str2, str3, str4, str5, str6, str7, str8, str9, str11, str13, i4, j, z, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
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
        if (TextUtils.isEmpty(str)) {
            str = "__FIREBASE_GENERATING_SINCE_" + getInstance(0).getCurrentTime() + "__";
            SharedConfig.pushStringStatus = str;
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

    public void switchBackend(boolean z) {
        MessagesController.getGlobalMainSettings().edit().remove("language_showed2").commit();
        native_switchBackend(this.currentAccount, z);
    }

    public boolean isTestBackend() {
        return native_isTestBackend(this.currentAccount) != 0;
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
                Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda5(i, TLdeserialize));
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", new Object[]{Integer.valueOf(readInt32)}));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdate(int i) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda1(i));
    }

    public static void onSessionCreated(int i) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda2(i));
    }

    public static void onConnectionStateChanged(int i, int i2) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda3(i2, i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onConnectionStateChanged$6(int i, int i2) {
        getInstance(i).connectionState = i2;
        AccountInstance.getInstance(i).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(int i) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda0(i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onLogout$7(int i) {
        AccountInstance instance = AccountInstance.getInstance(i);
        if (instance.getUserConfig().getClientUserId() != 0) {
            instance.getUserConfig().clearConfig();
            instance.getMessagesController().performLogout(0);
        }
    }

    public static int getInitFlags() {
        if (!EmuDetector.with(ApplicationLoader.applicationContext).detect()) {
            return 0;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("detected emu");
        }
        return 1024;
    }

    public static void onBytesSent(int i, int i2, int i3) {
        try {
            AccountInstance.getInstance(i3).getStatsController().incrementSentBytesCount(i2, 6, (long) i);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onRequestNewServerIpAndPort(int i, int i2) {
        Utilities.globalQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda4(i, i2));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onRequestNewServerIpAndPort$8(int i, boolean z, int i2) {
        if (currentTask == null && ((i != 0 || Math.abs(lastDnsRequestTime - System.currentTimeMillis()) >= 10000) && z)) {
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
        AndroidUtilities.runOnUIThread(ConnectionsManager$$ExternalSyntheticLambda12.INSTANCE);
    }

    public static void getHostByName(String str, long j) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda8(str, j));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$getHostByName$11(String str, long j) {
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
                Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda6(i, TLdeserialize));
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
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda10(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setIsUpdating$13(boolean z) {
        if (this.isUpdating != z) {
            this.isUpdating = z;
            if (this.connectionState == 3) {
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
            }
        }
    }

    @SuppressLint({"NewApi"})
    protected static byte getIpStrategy() {
        if (Build.VERSION.SDK_INT < 19) {
            return 0;
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
            boolean z3 = false;
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
                                        z = true;
                                    } else if (address2 instanceof Inet4Address) {
                                        if (!address2.getHostAddress().startsWith("192.0.0.")) {
                                            z3 = true;
                                        } else {
                                            z2 = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (z) {
                if (z2) {
                    return 2;
                }
                if (!z3) {
                    return 1;
                }
            }
        } catch (Throwable th2) {
            FileLog.e(th2);
        }
        return 0;
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
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00be A[SYNTHETIC, Splitter:B:43:0x00be] */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00c8 A[SYNTHETIC, Splitter:B:48:0x00c8] */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x00cf A[SYNTHETIC, Splitter:B:52:0x00cf] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.ConnectionsManager.ResolvedDomain doInBackground(java.lang.Void... r11) {
            /*
                r10 = this;
                java.lang.String r11 = "Answer"
                r0 = 1
                r1 = 0
                r2 = 0
                java.net.URL r3 = new java.net.URL     // Catch:{ all -> 0x00b6 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b6 }
                r4.<init>()     // Catch:{ all -> 0x00b6 }
                java.lang.String r5 = "https://www.google.com/resolve?name="
                r4.append(r5)     // Catch:{ all -> 0x00b6 }
                java.lang.String r5 = r10.currentHostName     // Catch:{ all -> 0x00b6 }
                r4.append(r5)     // Catch:{ all -> 0x00b6 }
                java.lang.String r5 = "&type=A"
                r4.append(r5)     // Catch:{ all -> 0x00b6 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00b6 }
                r3.<init>(r4)     // Catch:{ all -> 0x00b6 }
                java.net.URLConnection r3 = r3.openConnection()     // Catch:{ all -> 0x00b6 }
                java.lang.String r4 = "User-Agent"
                java.lang.String r5 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r3.addRequestProperty(r4, r5)     // Catch:{ all -> 0x00b6 }
                java.lang.String r4 = "Host"
                java.lang.String r5 = "dns.google.com"
                r3.addRequestProperty(r4, r5)     // Catch:{ all -> 0x00b6 }
                r4 = 1000(0x3e8, float:1.401E-42)
                r3.setConnectTimeout(r4)     // Catch:{ all -> 0x00b6 }
                r4 = 2000(0x7d0, float:2.803E-42)
                r3.setReadTimeout(r4)     // Catch:{ all -> 0x00b6 }
                r3.connect()     // Catch:{ all -> 0x00b6 }
                java.io.InputStream r3 = r3.getInputStream()     // Catch:{ all -> 0x00b6 }
                java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00b3 }
                r4.<init>()     // Catch:{ all -> 0x00b3 }
                r5 = 32768(0x8000, float:4.5918E-41)
                byte[] r5 = new byte[r5]     // Catch:{ all -> 0x00b1 }
            L_0x004f:
                int r6 = r3.read(r5)     // Catch:{ all -> 0x00b1 }
                if (r6 <= 0) goto L_0x0059
                r4.write(r5, r1, r6)     // Catch:{ all -> 0x00b1 }
                goto L_0x004f
            L_0x0059:
                org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ all -> 0x00b1 }
                java.lang.String r6 = new java.lang.String     // Catch:{ all -> 0x00b1 }
                byte[] r7 = r4.toByteArray()     // Catch:{ all -> 0x00b1 }
                r6.<init>(r7)     // Catch:{ all -> 0x00b1 }
                r5.<init>(r6)     // Catch:{ all -> 0x00b1 }
                boolean r6 = r5.has(r11)     // Catch:{ all -> 0x00b1 }
                if (r6 == 0) goto L_0x00a4
                org.json.JSONArray r11 = r5.getJSONArray(r11)     // Catch:{ all -> 0x00b1 }
                int r5 = r11.length()     // Catch:{ all -> 0x00b1 }
                if (r5 <= 0) goto L_0x00a4
                java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x00b1 }
                r6.<init>(r5)     // Catch:{ all -> 0x00b1 }
                r7 = 0
            L_0x007d:
                if (r7 >= r5) goto L_0x008f
                org.json.JSONObject r8 = r11.getJSONObject(r7)     // Catch:{ all -> 0x00b1 }
                java.lang.String r9 = "data"
                java.lang.String r8 = r8.getString(r9)     // Catch:{ all -> 0x00b1 }
                r6.add(r8)     // Catch:{ all -> 0x00b1 }
                int r7 = r7 + 1
                goto L_0x007d
            L_0x008f:
                org.telegram.tgnet.ConnectionsManager$ResolvedDomain r11 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ all -> 0x00b1 }
                long r7 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00b1 }
                r11.<init>(r6, r7)     // Catch:{ all -> 0x00b1 }
                r3.close()     // Catch:{ all -> 0x009c }
                goto L_0x00a0
            L_0x009c:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00a0:
                r4.close()     // Catch:{ Exception -> 0x00a3 }
            L_0x00a3:
                return r11
            L_0x00a4:
                r3.close()     // Catch:{ all -> 0x00a8 }
                goto L_0x00ac
            L_0x00a8:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x00ac:
                r4.close()     // Catch:{ Exception -> 0x00af }
            L_0x00af:
                r1 = 1
                goto L_0x00cd
            L_0x00b1:
                r11 = move-exception
                goto L_0x00b9
            L_0x00b3:
                r11 = move-exception
                r4 = r2
                goto L_0x00b9
            L_0x00b6:
                r11 = move-exception
                r3 = r2
                r4 = r3
            L_0x00b9:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)     // Catch:{ all -> 0x00f0 }
                if (r3 == 0) goto L_0x00c6
                r3.close()     // Catch:{ all -> 0x00c2 }
                goto L_0x00c6
            L_0x00c2:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x00c6:
                if (r4 == 0) goto L_0x00cd
                r4.close()     // Catch:{ Exception -> 0x00cc }
                goto L_0x00cd
            L_0x00cc:
            L_0x00cd:
                if (r1 != 0) goto L_0x00ef
                java.lang.String r11 = r10.currentHostName     // Catch:{ Exception -> 0x00eb }
                java.net.InetAddress r11 = java.net.InetAddress.getByName(r11)     // Catch:{ Exception -> 0x00eb }
                java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x00eb }
                r1.<init>(r0)     // Catch:{ Exception -> 0x00eb }
                java.lang.String r11 = r11.getHostAddress()     // Catch:{ Exception -> 0x00eb }
                r1.add(r11)     // Catch:{ Exception -> 0x00eb }
                org.telegram.tgnet.ConnectionsManager$ResolvedDomain r11 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ Exception -> 0x00eb }
                long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x00eb }
                r11.<init>(r1, r3)     // Catch:{ Exception -> 0x00eb }
                return r11
            L_0x00eb:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x00ef:
                return r2
            L_0x00f0:
                r11 = move-exception
                if (r3 == 0) goto L_0x00fb
                r3.close()     // Catch:{ all -> 0x00f7 }
                goto L_0x00fb
            L_0x00f7:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00fb:
                if (r4 == 0) goto L_0x0100
                r4.close()     // Catch:{ Exception -> 0x0100 }
            L_0x0100:
                goto L_0x0102
            L_0x0101:
                throw r11
            L_0x0102:
                goto L_0x0101
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
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0149 A[SYNTHETIC, Splitter:B:54:0x0149] */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0153 A[SYNTHETIC, Splitter:B:59:0x0153] */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x0156 A[SYNTHETIC] */
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
                if (r1 >= r4) goto L_0x016b
                if (r1 != 0) goto L_0x0010
                java.lang.String r4 = "www.google.com"
                goto L_0x0018
            L_0x000d:
                r4 = move-exception
                goto L_0x0144
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
                byte[] r2 = new byte[r2]     // Catch:{ all -> 0x0140 }
            L_0x00b0:
                boolean r5 = r13.isCancelled()     // Catch:{ all -> 0x0140 }
                if (r5 == 0) goto L_0x00b7
                goto L_0x00c1
            L_0x00b7:
                int r5 = r3.read(r2)     // Catch:{ all -> 0x0140 }
                if (r5 <= 0) goto L_0x00c1
                r4.write(r2, r0, r5)     // Catch:{ all -> 0x0140 }
                goto L_0x00b0
            L_0x00c1:
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x0140 }
                java.lang.String r5 = new java.lang.String     // Catch:{ all -> 0x0140 }
                byte[] r6 = r4.toByteArray()     // Catch:{ all -> 0x0140 }
                r5.<init>(r6)     // Catch:{ all -> 0x0140 }
                r2.<init>(r5)     // Catch:{ all -> 0x0140 }
                java.lang.String r5 = "Answer"
                org.json.JSONArray r2 = r2.getJSONArray(r5)     // Catch:{ all -> 0x0140 }
                int r5 = r2.length()     // Catch:{ all -> 0x0140 }
                java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0140 }
                r6.<init>(r5)     // Catch:{ all -> 0x0140 }
                r7 = 0
            L_0x00df:
                if (r7 >= r5) goto L_0x00fc
                org.json.JSONObject r8 = r2.getJSONObject(r7)     // Catch:{ all -> 0x0140 }
                java.lang.String r9 = "type"
                int r9 = r8.getInt(r9)     // Catch:{ all -> 0x0140 }
                r10 = 16
                if (r9 == r10) goto L_0x00f0
                goto L_0x00f9
            L_0x00f0:
                java.lang.String r9 = "data"
                java.lang.String r8 = r8.getString(r9)     // Catch:{ all -> 0x0140 }
                r6.add(r8)     // Catch:{ all -> 0x0140 }
            L_0x00f9:
                int r7 = r7 + 1
                goto L_0x00df
            L_0x00fc:
                org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda1 r2 = org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda1.INSTANCE     // Catch:{ all -> 0x0140 }
                java.util.Collections.sort(r6, r2)     // Catch:{ all -> 0x0140 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0140 }
                r2.<init>()     // Catch:{ all -> 0x0140 }
                r5 = 0
            L_0x0107:
                int r7 = r6.size()     // Catch:{ all -> 0x0140 }
                if (r5 >= r7) goto L_0x0121
                java.lang.Object r7 = r6.get(r5)     // Catch:{ all -> 0x0140 }
                java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0140 }
                java.lang.String r8 = "\""
                java.lang.String r9 = ""
                java.lang.String r7 = r7.replace(r8, r9)     // Catch:{ all -> 0x0140 }
                r2.append(r7)     // Catch:{ all -> 0x0140 }
                int r5 = r5 + 1
                goto L_0x0107
            L_0x0121:
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0140 }
                byte[] r2 = android.util.Base64.decode(r2, r0)     // Catch:{ all -> 0x0140 }
                org.telegram.tgnet.NativeByteBuffer r5 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x0140 }
                int r6 = r2.length     // Catch:{ all -> 0x0140 }
                r5.<init>((int) r6)     // Catch:{ all -> 0x0140 }
                r5.writeBytes((byte[]) r2)     // Catch:{ all -> 0x0140 }
                if (r3 == 0) goto L_0x013c
                r3.close()     // Catch:{ all -> 0x0138 }
                goto L_0x013c
            L_0x0138:
                r14 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
            L_0x013c:
                r4.close()     // Catch:{ Exception -> 0x013f }
            L_0x013f:
                return r5
            L_0x0140:
                r2 = move-exception
                r12 = r4
                r4 = r2
                r2 = r12
            L_0x0144:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r4, (boolean) r0)     // Catch:{ all -> 0x015a }
                if (r3 == 0) goto L_0x0151
                r3.close()     // Catch:{ all -> 0x014d }
                goto L_0x0151
            L_0x014d:
                r4 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
            L_0x0151:
                if (r2 == 0) goto L_0x0156
                r2.close()     // Catch:{ Exception -> 0x0156 }
            L_0x0156:
                int r1 = r1 + 1
                goto L_0x0005
            L_0x015a:
                r14 = move-exception
                if (r3 == 0) goto L_0x0165
                r3.close()     // Catch:{ all -> 0x0161 }
                goto L_0x0165
            L_0x0161:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0165:
                if (r2 == 0) goto L_0x016a
                r2.close()     // Catch:{ Exception -> 0x016a }
            L_0x016a:
                throw r14
            L_0x016b:
                return r14
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.DnsTxtLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda0(this, nativeByteBuffer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$1(NativeByteBuffer nativeByteBuffer) {
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
        /* JADX WARNING: Removed duplicated region for block: B:51:0x012d A[SYNTHETIC, Splitter:B:51:0x012d] */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0137 A[SYNTHETIC, Splitter:B:56:0x0137] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r12) {
            /*
                r11 = this;
                r12 = 0
                int r0 = r11.currentAccount     // Catch:{ all -> 0x0125 }
                int r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0)     // Catch:{ all -> 0x0125 }
                if (r0 == 0) goto L_0x000c
                java.lang.String r0 = "tapv3.stel.com"
                goto L_0x0018
            L_0x000c:
                int r0 = r11.currentAccount     // Catch:{ all -> 0x0125 }
                org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ all -> 0x0125 }
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ all -> 0x0125 }
                java.lang.String r0 = r0.dcDomainName     // Catch:{ all -> 0x0125 }
            L_0x0018:
                java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x0125 }
                r2 = 116(0x74, float:1.63E-43)
                int r1 = r1.nextInt(r2)     // Catch:{ all -> 0x0125 }
                int r1 = r1 + 13
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0125 }
                r2.<init>(r1)     // Catch:{ all -> 0x0125 }
                r3 = 0
                r4 = 0
            L_0x0029:
                if (r4 >= r1) goto L_0x003f
                java.lang.String r5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM"
                java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x0125 }
                r7 = 62
                int r6 = r6.nextInt(r7)     // Catch:{ all -> 0x0125 }
                char r5 = r5.charAt(r6)     // Catch:{ all -> 0x0125 }
                r2.append(r5)     // Catch:{ all -> 0x0125 }
                int r4 = r4 + 1
                goto L_0x0029
            L_0x003f:
                java.net.URL r1 = new java.net.URL     // Catch:{ all -> 0x0125 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0125 }
                r4.<init>()     // Catch:{ all -> 0x0125 }
                java.lang.String r5 = "https://dns.google.com/resolve?name="
                r4.append(r5)     // Catch:{ all -> 0x0125 }
                r4.append(r0)     // Catch:{ all -> 0x0125 }
                java.lang.String r0 = "&type=ANY&random_padding="
                r4.append(r0)     // Catch:{ all -> 0x0125 }
                r4.append(r2)     // Catch:{ all -> 0x0125 }
                java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0125 }
                r1.<init>(r0)     // Catch:{ all -> 0x0125 }
                java.net.URLConnection r0 = r1.openConnection()     // Catch:{ all -> 0x0125 }
                java.lang.String r1 = "User-Agent"
                java.lang.String r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x0125 }
                r1 = 5000(0x1388, float:7.006E-42)
                r0.setConnectTimeout(r1)     // Catch:{ all -> 0x0125 }
                r0.setReadTimeout(r1)     // Catch:{ all -> 0x0125 }
                r0.connect()     // Catch:{ all -> 0x0125 }
                java.io.InputStream r1 = r0.getInputStream()     // Catch:{ all -> 0x0125 }
                long r4 = r0.getDate()     // Catch:{ all -> 0x0121 }
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r0 = (int) r4     // Catch:{ all -> 0x0121 }
                r11.responseDate = r0     // Catch:{ all -> 0x0121 }
                java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0121 }
                r0.<init>()     // Catch:{ all -> 0x0121 }
                r2 = 32768(0x8000, float:4.5918E-41)
                byte[] r2 = new byte[r2]     // Catch:{ all -> 0x011b }
            L_0x008b:
                boolean r4 = r11.isCancelled()     // Catch:{ all -> 0x011b }
                if (r4 == 0) goto L_0x0092
                goto L_0x009c
            L_0x0092:
                int r4 = r1.read(r2)     // Catch:{ all -> 0x011b }
                if (r4 <= 0) goto L_0x009c
                r0.write(r2, r3, r4)     // Catch:{ all -> 0x011b }
                goto L_0x008b
            L_0x009c:
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x011b }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x011b }
                byte[] r5 = r0.toByteArray()     // Catch:{ all -> 0x011b }
                r4.<init>(r5)     // Catch:{ all -> 0x011b }
                r2.<init>(r4)     // Catch:{ all -> 0x011b }
                java.lang.String r4 = "Answer"
                org.json.JSONArray r2 = r2.getJSONArray(r4)     // Catch:{ all -> 0x011b }
                int r4 = r2.length()     // Catch:{ all -> 0x011b }
                java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x011b }
                r5.<init>(r4)     // Catch:{ all -> 0x011b }
                r6 = 0
            L_0x00ba:
                if (r6 >= r4) goto L_0x00d7
                org.json.JSONObject r7 = r2.getJSONObject(r6)     // Catch:{ all -> 0x011b }
                java.lang.String r8 = "type"
                int r8 = r7.getInt(r8)     // Catch:{ all -> 0x011b }
                r9 = 16
                if (r8 == r9) goto L_0x00cb
                goto L_0x00d4
            L_0x00cb:
                java.lang.String r8 = "data"
                java.lang.String r7 = r7.getString(r8)     // Catch:{ all -> 0x011b }
                r5.add(r7)     // Catch:{ all -> 0x011b }
            L_0x00d4:
                int r6 = r6 + 1
                goto L_0x00ba
            L_0x00d7:
                org.telegram.tgnet.ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda1 r2 = org.telegram.tgnet.ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda1.INSTANCE     // Catch:{ all -> 0x011b }
                java.util.Collections.sort(r5, r2)     // Catch:{ all -> 0x011b }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x011b }
                r2.<init>()     // Catch:{ all -> 0x011b }
                r4 = 0
            L_0x00e2:
                int r6 = r5.size()     // Catch:{ all -> 0x011b }
                if (r4 >= r6) goto L_0x00fc
                java.lang.Object r6 = r5.get(r4)     // Catch:{ all -> 0x011b }
                java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x011b }
                java.lang.String r7 = "\""
                java.lang.String r8 = ""
                java.lang.String r6 = r6.replace(r7, r8)     // Catch:{ all -> 0x011b }
                r2.append(r6)     // Catch:{ all -> 0x011b }
                int r4 = r4 + 1
                goto L_0x00e2
            L_0x00fc:
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x011b }
                byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x011b }
                org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x011b }
                int r4 = r2.length     // Catch:{ all -> 0x011b }
                r3.<init>((int) r4)     // Catch:{ all -> 0x011b }
                r3.writeBytes((byte[]) r2)     // Catch:{ all -> 0x011b }
                if (r1 == 0) goto L_0x0117
                r1.close()     // Catch:{ all -> 0x0113 }
                goto L_0x0117
            L_0x0113:
                r12 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            L_0x0117:
                r0.close()     // Catch:{ Exception -> 0x011a }
            L_0x011a:
                return r3
            L_0x011b:
                r2 = move-exception
                r10 = r1
                r1 = r0
                r0 = r2
                r2 = r10
                goto L_0x0128
            L_0x0121:
                r0 = move-exception
                r2 = r1
                r1 = r12
                goto L_0x0128
            L_0x0125:
                r0 = move-exception
                r1 = r12
                r2 = r1
            L_0x0128:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x013b }
                if (r2 == 0) goto L_0x0135
                r2.close()     // Catch:{ all -> 0x0131 }
                goto L_0x0135
            L_0x0131:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0135:
                if (r1 == 0) goto L_0x013a
                r1.close()     // Catch:{ Exception -> 0x013a }
            L_0x013a:
                return r12
            L_0x013b:
                r12 = move-exception
                if (r2 == 0) goto L_0x0146
                r2.close()     // Catch:{ all -> 0x0142 }
                goto L_0x0146
            L_0x0142:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0146:
                if (r1 == 0) goto L_0x014b
                r1.close()     // Catch:{ Exception -> 0x014b }
            L_0x014b:
                goto L_0x014d
            L_0x014c:
                throw r12
            L_0x014d:
                goto L_0x014c
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.GoogleDnsLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda0(this, nativeByteBuffer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$1(NativeByteBuffer nativeByteBuffer) {
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
        /* JADX WARNING: Removed duplicated region for block: B:51:0x0134 A[SYNTHETIC, Splitter:B:51:0x0134] */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x013e A[SYNTHETIC, Splitter:B:56:0x013e] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r12) {
            /*
                r11 = this;
                r12 = 0
                int r0 = r11.currentAccount     // Catch:{ all -> 0x012c }
                int r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0)     // Catch:{ all -> 0x012c }
                if (r0 == 0) goto L_0x000c
                java.lang.String r0 = "tapv3.stel.com"
                goto L_0x0018
            L_0x000c:
                int r0 = r11.currentAccount     // Catch:{ all -> 0x012c }
                org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ all -> 0x012c }
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ all -> 0x012c }
                java.lang.String r0 = r0.dcDomainName     // Catch:{ all -> 0x012c }
            L_0x0018:
                java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x012c }
                r2 = 116(0x74, float:1.63E-43)
                int r1 = r1.nextInt(r2)     // Catch:{ all -> 0x012c }
                int r1 = r1 + 13
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x012c }
                r2.<init>(r1)     // Catch:{ all -> 0x012c }
                r3 = 0
                r4 = 0
            L_0x0029:
                if (r4 >= r1) goto L_0x003f
                java.lang.String r5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM"
                java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x012c }
                r7 = 62
                int r6 = r6.nextInt(r7)     // Catch:{ all -> 0x012c }
                char r5 = r5.charAt(r6)     // Catch:{ all -> 0x012c }
                r2.append(r5)     // Catch:{ all -> 0x012c }
                int r4 = r4 + 1
                goto L_0x0029
            L_0x003f:
                java.net.URL r1 = new java.net.URL     // Catch:{ all -> 0x012c }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x012c }
                r4.<init>()     // Catch:{ all -> 0x012c }
                java.lang.String r5 = "https://mozilla.cloudflare-dns.com/dns-query?name="
                r4.append(r5)     // Catch:{ all -> 0x012c }
                r4.append(r0)     // Catch:{ all -> 0x012c }
                java.lang.String r0 = "&type=TXT&random_padding="
                r4.append(r0)     // Catch:{ all -> 0x012c }
                r4.append(r2)     // Catch:{ all -> 0x012c }
                java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x012c }
                r1.<init>(r0)     // Catch:{ all -> 0x012c }
                java.net.URLConnection r0 = r1.openConnection()     // Catch:{ all -> 0x012c }
                java.lang.String r1 = "User-Agent"
                java.lang.String r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x012c }
                java.lang.String r1 = "accept"
                java.lang.String r2 = "application/dns-json"
                r0.addRequestProperty(r1, r2)     // Catch:{ all -> 0x012c }
                r1 = 5000(0x1388, float:7.006E-42)
                r0.setConnectTimeout(r1)     // Catch:{ all -> 0x012c }
                r0.setReadTimeout(r1)     // Catch:{ all -> 0x012c }
                r0.connect()     // Catch:{ all -> 0x012c }
                java.io.InputStream r1 = r0.getInputStream()     // Catch:{ all -> 0x012c }
                long r4 = r0.getDate()     // Catch:{ all -> 0x0128 }
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r0 = (int) r4     // Catch:{ all -> 0x0128 }
                r11.responseDate = r0     // Catch:{ all -> 0x0128 }
                java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0128 }
                r0.<init>()     // Catch:{ all -> 0x0128 }
                r2 = 32768(0x8000, float:4.5918E-41)
                byte[] r2 = new byte[r2]     // Catch:{ all -> 0x0122 }
            L_0x0092:
                boolean r4 = r11.isCancelled()     // Catch:{ all -> 0x0122 }
                if (r4 == 0) goto L_0x0099
                goto L_0x00a3
            L_0x0099:
                int r4 = r1.read(r2)     // Catch:{ all -> 0x0122 }
                if (r4 <= 0) goto L_0x00a3
                r0.write(r2, r3, r4)     // Catch:{ all -> 0x0122 }
                goto L_0x0092
            L_0x00a3:
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x0122 }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x0122 }
                byte[] r5 = r0.toByteArray()     // Catch:{ all -> 0x0122 }
                r4.<init>(r5)     // Catch:{ all -> 0x0122 }
                r2.<init>(r4)     // Catch:{ all -> 0x0122 }
                java.lang.String r4 = "Answer"
                org.json.JSONArray r2 = r2.getJSONArray(r4)     // Catch:{ all -> 0x0122 }
                int r4 = r2.length()     // Catch:{ all -> 0x0122 }
                java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0122 }
                r5.<init>(r4)     // Catch:{ all -> 0x0122 }
                r6 = 0
            L_0x00c1:
                if (r6 >= r4) goto L_0x00de
                org.json.JSONObject r7 = r2.getJSONObject(r6)     // Catch:{ all -> 0x0122 }
                java.lang.String r8 = "type"
                int r8 = r7.getInt(r8)     // Catch:{ all -> 0x0122 }
                r9 = 16
                if (r8 == r9) goto L_0x00d2
                goto L_0x00db
            L_0x00d2:
                java.lang.String r8 = "data"
                java.lang.String r7 = r7.getString(r8)     // Catch:{ all -> 0x0122 }
                r5.add(r7)     // Catch:{ all -> 0x0122 }
            L_0x00db:
                int r6 = r6 + 1
                goto L_0x00c1
            L_0x00de:
                org.telegram.tgnet.ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda1 r2 = org.telegram.tgnet.ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda1.INSTANCE     // Catch:{ all -> 0x0122 }
                java.util.Collections.sort(r5, r2)     // Catch:{ all -> 0x0122 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0122 }
                r2.<init>()     // Catch:{ all -> 0x0122 }
                r4 = 0
            L_0x00e9:
                int r6 = r5.size()     // Catch:{ all -> 0x0122 }
                if (r4 >= r6) goto L_0x0103
                java.lang.Object r6 = r5.get(r4)     // Catch:{ all -> 0x0122 }
                java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x0122 }
                java.lang.String r7 = "\""
                java.lang.String r8 = ""
                java.lang.String r6 = r6.replace(r7, r8)     // Catch:{ all -> 0x0122 }
                r2.append(r6)     // Catch:{ all -> 0x0122 }
                int r4 = r4 + 1
                goto L_0x00e9
            L_0x0103:
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0122 }
                byte[] r2 = android.util.Base64.decode(r2, r3)     // Catch:{ all -> 0x0122 }
                org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x0122 }
                int r4 = r2.length     // Catch:{ all -> 0x0122 }
                r3.<init>((int) r4)     // Catch:{ all -> 0x0122 }
                r3.writeBytes((byte[]) r2)     // Catch:{ all -> 0x0122 }
                if (r1 == 0) goto L_0x011e
                r1.close()     // Catch:{ all -> 0x011a }
                goto L_0x011e
            L_0x011a:
                r12 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            L_0x011e:
                r0.close()     // Catch:{ Exception -> 0x0121 }
            L_0x0121:
                return r3
            L_0x0122:
                r2 = move-exception
                r10 = r1
                r1 = r0
                r0 = r2
                r2 = r10
                goto L_0x012f
            L_0x0128:
                r0 = move-exception
                r2 = r1
                r1 = r12
                goto L_0x012f
            L_0x012c:
                r0 = move-exception
                r1 = r12
                r2 = r1
            L_0x012f:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0142 }
                if (r2 == 0) goto L_0x013c
                r2.close()     // Catch:{ all -> 0x0138 }
                goto L_0x013c
            L_0x0138:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x013c:
                if (r1 == 0) goto L_0x0141
                r1.close()     // Catch:{ Exception -> 0x0141 }
            L_0x0141:
                return r12
            L_0x0142:
                r12 = move-exception
                if (r2 == 0) goto L_0x014d
                r2.close()     // Catch:{ all -> 0x0149 }
                goto L_0x014d
            L_0x0149:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x014d:
                if (r1 == 0) goto L_0x0152
                r1.close()     // Catch:{ Exception -> 0x0152 }
            L_0x0152:
                goto L_0x0154
            L_0x0153:
                throw r12
            L_0x0154:
                goto L_0x0153
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.MozillaDnsLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ int lambda$doInBackground$0(String str, String str2) {
            int length = str.length();
            int length2 = str2.length();
            if (length > length2) {
                return -1;
            }
            return length < length2 ? 1 : 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda0(this, nativeByteBuffer));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$1(NativeByteBuffer nativeByteBuffer) {
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
                    FirebaseRemoteConfig instance = FirebaseRemoteConfig.getInstance();
                    this.firebaseRemoteConfig = instance;
                    String string = instance.getString("ipconfigv3");
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("current firebase value = " + string);
                    }
                    this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda1(this));
                    return null;
                }
                throw new Exception("test backend");
            } catch (Throwable th) {
                Utilities.stageQueue.postRunnable(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda2(this));
                FileLog.e(th);
                return null;
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$doInBackground$2(Task task) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda3(this, task.isSuccessful()));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$doInBackground$1(boolean z) {
            if (z) {
                this.firebaseRemoteConfig.activate().addOnCompleteListener(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$doInBackground$0(Task task) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            String string = this.firebaseRemoteConfig.getString("ipconfigv3");
            if (!TextUtils.isEmpty(string)) {
                byte[] decode = Base64.decode(string, 0);
                try {
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(decode.length);
                    nativeByteBuffer.writeBytes(decode);
                    int fetchTimeMillis = (int) (this.firebaseRemoteConfig.getInfo().getFetchTimeMillis() / 1000);
                    int i = this.currentAccount;
                    ConnectionsManager.native_applyDnsConfig(i, nativeByteBuffer.address, AccountInstance.getInstance(i).getUserConfig().getClientPhone(), fetchTimeMillis);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$doInBackground$3() {
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
