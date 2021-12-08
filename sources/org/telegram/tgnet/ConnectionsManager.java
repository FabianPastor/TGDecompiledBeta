package org.telegram.tgnet;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
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
import org.json.JSONArray;
import org.json.JSONObject;
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
import org.telegram.tgnet.TLRPC;

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

            public Thread newThread(Runnable r) {
                return new Thread(r, "DnsAsyncTask #" + this.mCount.getAndIncrement());
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

        public ResolvedDomain(ArrayList<String> a, long t) {
            this.addresses = a;
            this.ttl = t;
        }

        public String getAddress() {
            return this.addresses.get(Utilities.random.nextInt(this.addresses.size()));
        }
    }

    public static ConnectionsManager getInstance(int num) {
        ConnectionsManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    ConnectionsManager[] connectionsManagerArr = Instance;
                    ConnectionsManager connectionsManager = new ConnectionsManager(num);
                    localInstance = connectionsManager;
                    connectionsManagerArr[num] = connectionsManager;
                }
            }
        }
        return localInstance;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ConnectionsManager(int instance) {
        super(instance);
        File config;
        String langCode;
        String appVersion;
        String langCode2;
        String systemVersion;
        String systemLangCode;
        String deviceModel;
        String appVersion2;
        String systemVersion2;
        int i = instance;
        File config2 = ApplicationLoader.getFilesDirFixed();
        if (i != 0) {
            File config3 = new File(config2, "account" + i);
            config3.mkdirs();
            config = config3;
        } else {
            config = config2;
        }
        String configPath = config.toString();
        boolean enablePushConnection = isPushConnectionEnabled();
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            String langCode3 = LocaleController.getLocaleStringIso639().toLowerCase();
            langCode2 = Build.MANUFACTURER + Build.MODEL;
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                appVersion = appVersion + " pbeta";
            } else if (BuildVars.DEBUG_VERSION) {
                appVersion = appVersion + " beta";
            }
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
            langCode = langCode3;
        } catch (Exception e) {
            appVersion = "App version unknown";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
            systemLangCode = "en";
            langCode = "";
            langCode2 = "Android unknown";
        }
        systemLangCode = systemLangCode.trim().length() == 0 ? "en" : systemLangCode;
        if (langCode2.trim().length() == 0) {
            deviceModel = "Android unknown";
        } else {
            deviceModel = langCode2;
        }
        if (appVersion.trim().length() == 0) {
            appVersion2 = "App version unknown";
        } else {
            appVersion2 = appVersion;
        }
        if (systemVersion.trim().length() == 0) {
            systemVersion2 = "SDK Unknown";
        } else {
            systemVersion2 = systemVersion;
        }
        getUserConfig().loadConfig();
        int timezoneOffset = (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000;
        int i2 = timezoneOffset;
        init(BuildVars.BUILD_VERSION, 135, BuildVars.APP_ID, deviceModel, systemVersion2, appVersion2, langCode, systemLangCode, configPath, FileLog.getNetworkLogPath(), getRegId(), AndroidUtilities.getCertificateSHA256Fingerprint(), timezoneOffset, getUserConfig().getClientUserId(), enablePushConnection);
    }

    private String getRegId() {
        String pushString = SharedConfig.pushString;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            pushString = SharedConfig.pushStringStatus;
        }
        if (!TextUtils.isEmpty(pushString)) {
            return pushString;
        }
        String pushString2 = "__FIREBASE_GENERATING_SINCE_" + getCurrentTime() + "__";
        SharedConfig.pushStringStatus = pushString2;
        return pushString2;
    }

    public boolean isPushConnectionEnabled() {
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        if (preferences.contains("pushConnection")) {
            return preferences.getBoolean("pushConnection", true);
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

    public int sendRequest(TLObject object, RequestDelegate completionBlock) {
        return sendRequest(object, completionBlock, (QuickAckDelegate) null, 0);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags) {
        return sendRequest(object, completionBlock, (RequestDelegateTimestamp) null, (QuickAckDelegate) null, (WriteToSocketDelegate) null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, (RequestDelegateTimestamp) null, (QuickAckDelegate) null, (WriteToSocketDelegate) null, flags, Integer.MAX_VALUE, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegateTimestamp completionBlock, int flags, int connetionType, int datacenterId) {
        return sendRequest(object, (RequestDelegate) null, completionBlock, (QuickAckDelegate) null, (WriteToSocketDelegate) null, flags, datacenterId, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, (RequestDelegateTimestamp) null, quickAckBlock, (WriteToSocketDelegate) null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        return sendRequest(object, onComplete, (RequestDelegateTimestamp) null, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, RequestDelegateTimestamp onCompleteTimestamp, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        int requestToken = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda13(this, object, requestToken, onComplete, onCompleteTimestamp, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate));
        return requestToken;
    }

    /* renamed from: lambda$sendRequest$2$org-telegram-tgnet-ConnectionsManager  reason: not valid java name */
    public /* synthetic */ void m497lambda$sendRequest$2$orgtelegramtgnetConnectionsManager(TLObject object, int requestToken, RequestDelegate onComplete, RequestDelegateTimestamp onCompleteTimestamp, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        TLObject tLObject = object;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("send request " + tLObject + " with token = " + requestToken);
        } else {
            int i = requestToken;
        }
        try {
            NativeByteBuffer buffer = new NativeByteBuffer(object.getObjectSize());
            tLObject.serializeToStream(buffer);
            object.freeResources();
            try {
                try {
                    native_sendRequest(this.currentAccount, buffer.address, new ConnectionsManager$$ExternalSyntheticLambda4(tLObject, onComplete, onCompleteTimestamp), onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate, requestToken);
                } catch (Exception e) {
                    e = e;
                }
            } catch (Exception e2) {
                e = e2;
                RequestDelegate requestDelegate = onComplete;
                FileLog.e((Throwable) e);
            }
        } catch (Exception e3) {
            e = e3;
            RequestDelegate requestDelegate2 = onComplete;
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$sendRequest$1(TLObject object, RequestDelegate onComplete, RequestDelegateTimestamp onCompleteTimestamp, long response, int errorCode, String errorText, int networkType, long timestamp) {
        TLObject tLObject = object;
        String str = errorText;
        TLObject resp = null;
        TLRPC.TL_error error = null;
        if (response != 0) {
            try {
                NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                buff.reused = true;
                resp = object.deserializeResponse(buff, buff.readInt32(true), true);
                int i = errorCode;
            } catch (Exception e) {
                e = e;
                int i2 = errorCode;
                int i3 = networkType;
                FileLog.e((Throwable) e);
                return;
            }
        } else if (str != null) {
            error = new TLRPC.TL_error();
            try {
                error.code = errorCode;
                error.text = str;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(object + " got error " + error.code + " " + error.text);
                }
            } catch (Exception e2) {
                e = e2;
                int i32 = networkType;
                FileLog.e((Throwable) e);
                return;
            }
        } else {
            int i4 = errorCode;
        }
        if (resp != null) {
            try {
                resp.networkType = networkType;
            } catch (Exception e3) {
                e = e3;
                FileLog.e((Throwable) e);
                return;
            }
        } else {
            int i5 = networkType;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("java received " + resp + " error = " + error);
        }
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda2(onComplete, resp, error, onCompleteTimestamp, timestamp));
    }

    static /* synthetic */ void lambda$sendRequest$0(RequestDelegate onComplete, TLObject finalResponse, TLRPC.TL_error finalError, RequestDelegateTimestamp onCompleteTimestamp, long timestamp) {
        if (onComplete != null) {
            onComplete.run(finalResponse, finalError);
        } else if (onCompleteTimestamp != null) {
            onCompleteTimestamp.run(finalResponse, finalError, timestamp);
        }
        if (finalResponse != null) {
            finalResponse.freeResources();
        }
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(this.currentAccount, token, notifyServer);
    }

    public void cleanup(boolean resetKeys) {
        native_cleanUp(this.currentAccount, resetKeys);
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(this.currentAccount, guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(this.currentAccount, requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port) {
        native_applyDatacenterAddress(this.currentAccount, datacenterId, ipAddress, port);
    }

    public int getConnectionState() {
        int i = this.connectionState;
        if (i != 3 || !this.isUpdating) {
            return i;
        }
        return 5;
    }

    public void setUserId(long id) {
        native_setUserId(this.currentAccount, id);
    }

    public void checkConnection() {
        native_setIpStrategy(this.currentAccount, getIpStrategy());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(this.currentAccount, value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, String regId, String cFingerprint, int timezoneOffset, long userId, boolean enablePushConnection) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", "");
        String proxyUsername = preferences.getString("proxy_user", "");
        String proxyPassword = preferences.getString("proxy_pass", "");
        String proxySecret = preferences.getString("proxy_secret", "");
        int proxyPort = preferences.getInt("proxy_port", 1080);
        if (preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(proxyAddress)) {
            native_setProxySettings(this.currentAccount, proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
        }
        String installer = "";
        try {
            installer = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
        } catch (Throwable th) {
        }
        if (installer == null) {
            installer = "";
        }
        String packageId = "";
        try {
            packageId = ApplicationLoader.applicationContext.getPackageName();
        } catch (Throwable th2) {
        }
        if (packageId == null) {
            packageId = "";
        }
        native_init(this.currentAccount, version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, regId, cFingerprint, installer, packageId, timezoneOffset, userId, enablePushConnection, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
        checkConnection();
    }

    public static void setLangCode(String langCode) {
        String langCode2 = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setLangCode(a, langCode2);
        }
    }

    public static void setRegId(String regId, String status) {
        String pushString = regId;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(status)) {
            pushString = status;
        }
        if (TextUtils.isEmpty(pushString)) {
            String str = "__FIREBASE_GENERATING_SINCE_" + getInstance(0).getCurrentTime() + "__";
            SharedConfig.pushStringStatus = str;
            pushString = str;
        }
        for (int a = 0; a < 3; a++) {
            native_setRegId(a, pushString);
        }
    }

    public static void setSystemLangCode(String langCode) {
        String langCode2 = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 3; a++) {
            native_setSystemLangCode(a, langCode2);
        }
    }

    public void switchBackend(boolean restart) {
        MessagesController.getGlobalMainSettings().edit().remove("language_showed2").commit();
        native_switchBackend(this.currentAccount, restart);
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

    public long checkProxy(String address, int port, String username, String password, String secret, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(address)) {
            return 0;
        }
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        return native_checkProxy(this.currentAccount, address, port, username, password, secret, requestTimeDelegate);
    }

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            this.appPaused = value;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app paused = " + value);
            }
            if (value) {
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

    public static void onUnparsedMessageReceived(long address, int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            int constructor = buff.readInt32(true);
            TLObject message = TLClassStore.Instance().TLdeserialize(buff, constructor, true);
            if (message instanceof TLRPC.Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("java received " + message);
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda9(currentAccount, message));
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", new Object[]{Integer.valueOf(constructor)}));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdate(int currentAccount) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda6(currentAccount));
    }

    public static void onSessionCreated(int currentAccount) {
        Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda5(currentAccount));
    }

    public static void onConnectionStateChanged(int state, int currentAccount) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda7(currentAccount, state));
    }

    static /* synthetic */ void lambda$onConnectionStateChanged$6(int currentAccount, int state) {
        getInstance(currentAccount).connectionState = state;
        AccountInstance.getInstance(currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(int currentAccount) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda0(currentAccount));
    }

    static /* synthetic */ void lambda$onLogout$7(int currentAccount) {
        AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
        if (accountInstance.getUserConfig().getClientUserId() != 0) {
            accountInstance.getUserConfig().clearConfig();
            accountInstance.getMessagesController().performLogout(0);
        }
    }

    public static int getInitFlags() {
        if (!EmuDetector.with(ApplicationLoader.applicationContext).detect()) {
            return 0;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("detected emu");
        }
        return 0 | 1024;
    }

    public static void onBytesSent(int amount, int networkType, int currentAccount) {
        try {
            AccountInstance.getInstance(currentAccount).getStatsController().incrementSentBytesCount(networkType, 6, (long) amount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onRequestNewServerIpAndPort(int second, int currentAccount) {
        Utilities.globalQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda8(second, currentAccount));
    }

    static /* synthetic */ void lambda$onRequestNewServerIpAndPort$8(int second, boolean networkOnline, int currentAccount) {
        if (currentTask == null && ((second != 0 || Math.abs(lastDnsRequestTime - System.currentTimeMillis()) >= 10000) && networkOnline)) {
            lastDnsRequestTime = System.currentTimeMillis();
            if (second == 3) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start mozilla txt task");
                }
                MozillaDnsLoadTask task = new MozillaDnsLoadTask(currentAccount);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task;
            } else if (second == 2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start google txt task");
                }
                GoogleDnsLoadTask task2 = new GoogleDnsLoadTask(currentAccount);
                task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task2;
            } else if (second == 1) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask task3 = new DnsTxtLoadTask(currentAccount);
                task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task3;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start firebase task");
                }
                FirebaseTask task4 = new FirebaseTask(currentAccount);
                task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                currentTask = task4;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("don't start task, current task = " + currentTask + " next task = " + second + " time diff = " + Math.abs(lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ApplicationLoader.isNetworkOnline());
        }
    }

    public static void onProxyError() {
        AndroidUtilities.runOnUIThread(ConnectionsManager$$ExternalSyntheticLambda3.INSTANCE);
    }

    public static void getHostByName(String hostName, long address) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda12(hostName, address));
    }

    static /* synthetic */ void lambda$getHostByName$11(String hostName, long address) {
        ResolvedDomain resolvedDomain = dnsCache.get(hostName);
        if (resolvedDomain == null || SystemClock.elapsedRealtime() - resolvedDomain.ttl >= 300000) {
            ResolveHostByNameTask task = resolvingHostnameTasks.get(hostName);
            if (task == null) {
                task = new ResolveHostByNameTask(hostName);
                try {
                    task.executeOnExecutor(DNS_THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    resolvingHostnameTasks.put(hostName, task);
                } catch (Throwable e) {
                    FileLog.e(e);
                    native_onHostNameResolved(hostName, address, "");
                    return;
                }
            }
            task.addAddress(address);
            return;
        }
        native_onHostNameResolved(hostName, address, resolvedDomain.getAddress());
    }

    public static void onBytesReceived(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementReceivedBytesCount(networkType, 6, (long) amount);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onUpdateConfig(long address, int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            TLRPC.TL_config message = TLRPC.TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new ConnectionsManager$$ExternalSyntheticLambda10(currentAccount, message));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void onInternalPushReceived(int currentAccount) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean enabled, String address, int port, String username, String password, String secret) {
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        for (int a = 0; a < 3; a++) {
            if (!enabled || TextUtils.isEmpty(address)) {
                native_setProxySettings(a, "", 1080, "", "", "");
            } else {
                native_setProxySettings(a, address, port, username, password, secret);
            }
            AccountInstance accountInstance = AccountInstance.getInstance(a);
            if (accountInstance.getUserConfig().isClientActivated()) {
                accountInstance.getMessagesController().checkPromoInfo(true);
            }
        }
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public void setIsUpdating(boolean value) {
        AndroidUtilities.runOnUIThread(new ConnectionsManager$$ExternalSyntheticLambda1(this, value));
    }

    /* renamed from: lambda$setIsUpdating$13$org-telegram-tgnet-ConnectionsManager  reason: not valid java name */
    public /* synthetic */ void m498lambda$setIsUpdating$13$orgtelegramtgnetConnectionsManager(boolean value) {
        if (this.isUpdating != value) {
            this.isUpdating = value;
            if (this.connectionState == 3) {
                AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
            }
        }
    }

    protected static byte getIpStrategy() {
        if (Build.VERSION.SDK_INT < 19) {
            return 0;
        }
        if (BuildVars.LOGS_ENABLED) {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("valid interface: " + networkInterface);
                            }
                            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                            for (int a = 0; a < interfaceAddresses.size(); a++) {
                                InetAddress inetAddress = interfaceAddresses.get(a).getAddress();
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("address: " + inetAddress.getHostAddress());
                                }
                                if (!inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress()) {
                                    if (!inetAddress.isMulticastAddress()) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("address is good");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            boolean hasStrangeIpv4 = false;
            while (networkInterfaces2.hasMoreElements()) {
                NetworkInterface networkInterface2 = networkInterfaces2.nextElement();
                if (networkInterface2.isUp()) {
                    if (!networkInterface2.isLoopback()) {
                        List<InterfaceAddress> interfaceAddresses2 = networkInterface2.getInterfaceAddresses();
                        for (int a2 = 0; a2 < interfaceAddresses2.size(); a2++) {
                            InetAddress inetAddress2 = interfaceAddresses2.get(a2).getAddress();
                            if (!inetAddress2.isLinkLocalAddress() && !inetAddress2.isLoopbackAddress()) {
                                if (!inetAddress2.isMulticastAddress()) {
                                    if (inetAddress2 instanceof Inet6Address) {
                                        hasIpv6 = true;
                                    } else if (inetAddress2 instanceof Inet4Address) {
                                        if (!inetAddress2.getHostAddress().startsWith("192.0.0.")) {
                                            hasIpv4 = true;
                                        } else {
                                            hasStrangeIpv4 = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (hasIpv6) {
                if (hasStrangeIpv4) {
                    return 2;
                }
                if (!hasIpv4) {
                    return 1;
                }
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        return 0;
    }

    private static class ResolveHostByNameTask extends AsyncTask<Void, Void, ResolvedDomain> {
        private ArrayList<Long> addresses = new ArrayList<>();
        private String currentHostName;

        public ResolveHostByNameTask(String hostName) {
            this.currentHostName = hostName;
        }

        public void addAddress(long address) {
            if (!this.addresses.contains(Long.valueOf(address))) {
                this.addresses.add(Long.valueOf(address));
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x0072, code lost:
            r0 = r7.getJSONArray("Answer");
         */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00dc A[SYNTHETIC, Splitter:B:44:0x00dc] */
        /* JADX WARNING: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.ConnectionsManager.ResolvedDomain doInBackground(java.lang.Void... r14) {
            /*
                r13 = this;
                java.lang.String r0 = "Answer"
                r1 = 0
                r2 = 0
                r3 = 0
                java.net.URL r4 = new java.net.URL     // Catch:{ all -> 0x00c4 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c4 }
                r5.<init>()     // Catch:{ all -> 0x00c4 }
                java.lang.String r6 = "https://www.google.com/resolve?name="
                r5.append(r6)     // Catch:{ all -> 0x00c4 }
                java.lang.String r6 = r13.currentHostName     // Catch:{ all -> 0x00c4 }
                r5.append(r6)     // Catch:{ all -> 0x00c4 }
                java.lang.String r6 = "&type=A"
                r5.append(r6)     // Catch:{ all -> 0x00c4 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00c4 }
                r4.<init>(r5)     // Catch:{ all -> 0x00c4 }
                java.net.URLConnection r5 = r4.openConnection()     // Catch:{ all -> 0x00c4 }
                java.lang.String r6 = "User-Agent"
                java.lang.String r7 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r5.addRequestProperty(r6, r7)     // Catch:{ all -> 0x00c4 }
                java.lang.String r6 = "Host"
                java.lang.String r7 = "dns.google.com"
                r5.addRequestProperty(r6, r7)     // Catch:{ all -> 0x00c4 }
                r6 = 1000(0x3e8, float:1.401E-42)
                r5.setConnectTimeout(r6)     // Catch:{ all -> 0x00c4 }
                r6 = 2000(0x7d0, float:2.803E-42)
                r5.setReadTimeout(r6)     // Catch:{ all -> 0x00c4 }
                r5.connect()     // Catch:{ all -> 0x00c4 }
                java.io.InputStream r6 = r5.getInputStream()     // Catch:{ all -> 0x00c4 }
                r2 = r6
                java.io.ByteArrayOutputStream r6 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00c4 }
                r6.<init>()     // Catch:{ all -> 0x00c4 }
                r1 = r6
                r6 = 32768(0x8000, float:4.5918E-41)
                byte[] r6 = new byte[r6]     // Catch:{ all -> 0x00c4 }
            L_0x0051:
                int r7 = r2.read(r6)     // Catch:{ all -> 0x00c4 }
                if (r7 <= 0) goto L_0x005c
                r8 = 0
                r1.write(r6, r8, r7)     // Catch:{ all -> 0x00c4 }
                goto L_0x0051
            L_0x005c:
                org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ all -> 0x00c4 }
                java.lang.String r8 = new java.lang.String     // Catch:{ all -> 0x00c4 }
                byte[] r9 = r1.toByteArray()     // Catch:{ all -> 0x00c4 }
                r8.<init>(r9)     // Catch:{ all -> 0x00c4 }
                r7.<init>(r8)     // Catch:{ all -> 0x00c4 }
                boolean r8 = r7.has(r0)     // Catch:{ all -> 0x00c4 }
                if (r8 == 0) goto L_0x00b0
                org.json.JSONArray r0 = r7.getJSONArray(r0)     // Catch:{ all -> 0x00c4 }
                int r8 = r0.length()     // Catch:{ all -> 0x00c4 }
                if (r8 <= 0) goto L_0x00b0
                java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ all -> 0x00c4 }
                r9.<init>(r8)     // Catch:{ all -> 0x00c4 }
                r10 = 0
            L_0x0082:
                if (r10 >= r8) goto L_0x0094
                org.json.JSONObject r11 = r0.getJSONObject(r10)     // Catch:{ all -> 0x00c4 }
                java.lang.String r12 = "data"
                java.lang.String r11 = r11.getString(r12)     // Catch:{ all -> 0x00c4 }
                r9.add(r11)     // Catch:{ all -> 0x00c4 }
                int r10 = r10 + 1
                goto L_0x0082
            L_0x0094:
                org.telegram.tgnet.ConnectionsManager$ResolvedDomain r10 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ all -> 0x00c4 }
                long r11 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00c4 }
                r10.<init>(r9, r11)     // Catch:{ all -> 0x00c4 }
                if (r2 == 0) goto L_0x00a8
                r2.close()     // Catch:{ all -> 0x00a3 }
                goto L_0x00a8
            L_0x00a3:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
                goto L_0x00a9
            L_0x00a8:
            L_0x00a9:
                r1.close()     // Catch:{ Exception -> 0x00ae }
                goto L_0x00af
            L_0x00ae:
                r11 = move-exception
            L_0x00af:
                return r10
            L_0x00b0:
                r3 = 1
                if (r2 == 0) goto L_0x00bc
                r2.close()     // Catch:{ all -> 0x00b7 }
                goto L_0x00bc
            L_0x00b7:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x00bd
            L_0x00bc:
            L_0x00bd:
                r1.close()     // Catch:{ Exception -> 0x00c2 }
            L_0x00c1:
                goto L_0x00da
            L_0x00c2:
                r0 = move-exception
                goto L_0x00da
            L_0x00c4:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00ff }
                if (r2 == 0) goto L_0x00d3
                r2.close()     // Catch:{ all -> 0x00ce }
                goto L_0x00d3
            L_0x00ce:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x00d4
            L_0x00d3:
            L_0x00d4:
                if (r1 == 0) goto L_0x00c1
                r1.close()     // Catch:{ Exception -> 0x00c2 }
                goto L_0x00c1
            L_0x00da:
                if (r3 != 0) goto L_0x00fd
                java.lang.String r0 = r13.currentHostName     // Catch:{ Exception -> 0x00f9 }
                java.net.InetAddress r0 = java.net.InetAddress.getByName(r0)     // Catch:{ Exception -> 0x00f9 }
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x00f9 }
                r5 = 1
                r4.<init>(r5)     // Catch:{ Exception -> 0x00f9 }
                java.lang.String r5 = r0.getHostAddress()     // Catch:{ Exception -> 0x00f9 }
                r4.add(r5)     // Catch:{ Exception -> 0x00f9 }
                org.telegram.tgnet.ConnectionsManager$ResolvedDomain r5 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain     // Catch:{ Exception -> 0x00f9 }
                long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x00f9 }
                r5.<init>(r4, r6)     // Catch:{ Exception -> 0x00f9 }
                return r5
            L_0x00f9:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00fd:
                r0 = 0
                return r0
            L_0x00ff:
                r0 = move-exception
                if (r2 == 0) goto L_0x010b
                r2.close()     // Catch:{ all -> 0x0106 }
                goto L_0x010b
            L_0x0106:
                r4 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
                goto L_0x010c
            L_0x010b:
            L_0x010c:
                if (r1 == 0) goto L_0x0114
                r1.close()     // Catch:{ Exception -> 0x0112 }
                goto L_0x0114
            L_0x0112:
                r4 = move-exception
                goto L_0x0115
            L_0x0114:
            L_0x0115:
                goto L_0x0117
            L_0x0116:
                throw r0
            L_0x0117:
                goto L_0x0116
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.ResolveHostByNameTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.ConnectionsManager$ResolvedDomain");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(ResolvedDomain result) {
            if (result != null) {
                ConnectionsManager.dnsCache.put(this.currentHostName, result);
                int N = this.addresses.size();
                for (int a = 0; a < N; a++) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(a).longValue(), result.getAddress());
                }
            } else {
                int N2 = this.addresses.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(a2).longValue(), "");
                }
            }
            ConnectionsManager.resolvingHostnameTasks.remove(this.currentHostName);
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public DnsTxtLoadTask(int instance) {
            this.currentAccount = instance;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0190 A[SYNTHETIC, Splitter:B:56:0x0190] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x019f A[SYNTHETIC, Splitter:B:61:0x019f] */
        /* JADX WARNING: Removed duplicated region for block: B:81:0x01a6 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r22) {
            /*
                r21 = this;
                r1 = r21
                java.lang.String r2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM"
                r0 = 0
                r3 = 0
                r4 = 0
                r5 = r4
                r4 = r3
                r3 = r0
            L_0x000a:
                r0 = 3
                if (r5 >= r0) goto L_0x01c9
                r6 = 0
                if (r5 != 0) goto L_0x0019
                java.lang.String r0 = "www.google.com"
                r7 = r0
                goto L_0x0023
            L_0x0014:
                r0 = move-exception
                r18 = r2
                goto L_0x018a
            L_0x0019:
                r0 = 1
                if (r5 != r0) goto L_0x0020
                java.lang.String r0 = "www.google.ru"
                r7 = r0
                goto L_0x0023
            L_0x0020:
                java.lang.String r0 = "google.com"
                r7 = r0
            L_0x0023:
                int r0 = r1.currentAccount     // Catch:{ all -> 0x0014 }
                int r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0)     // Catch:{ all -> 0x0014 }
                if (r0 == 0) goto L_0x002e
                java.lang.String r0 = "tapv3.stel.com"
                goto L_0x003a
            L_0x002e:
                int r0 = r1.currentAccount     // Catch:{ all -> 0x0014 }
                org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ all -> 0x0014 }
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()     // Catch:{ all -> 0x0014 }
                java.lang.String r0 = r0.dcDomainName     // Catch:{ all -> 0x0014 }
            L_0x003a:
                r8 = r0
                java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x0014 }
                r9 = 116(0x74, float:1.63E-43)
                int r0 = r0.nextInt(r9)     // Catch:{ all -> 0x0014 }
                int r0 = r0 + 13
                r9 = r2
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x0014 }
                r10.<init>(r0)     // Catch:{ all -> 0x0014 }
                r11 = 0
            L_0x004c:
                if (r11 >= r0) goto L_0x0062
                java.security.SecureRandom r12 = org.telegram.messenger.Utilities.random     // Catch:{ all -> 0x0014 }
                int r13 = r2.length()     // Catch:{ all -> 0x0014 }
                int r12 = r12.nextInt(r13)     // Catch:{ all -> 0x0014 }
                char r12 = r2.charAt(r12)     // Catch:{ all -> 0x0014 }
                r10.append(r12)     // Catch:{ all -> 0x0014 }
                int r11 = r11 + 1
                goto L_0x004c
            L_0x0062:
                java.net.URL r11 = new java.net.URL     // Catch:{ all -> 0x0014 }
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0014 }
                r12.<init>()     // Catch:{ all -> 0x0014 }
                java.lang.String r13 = "https://"
                r12.append(r13)     // Catch:{ all -> 0x0014 }
                r12.append(r7)     // Catch:{ all -> 0x0014 }
                java.lang.String r13 = "/resolve?name="
                r12.append(r13)     // Catch:{ all -> 0x0014 }
                r12.append(r8)     // Catch:{ all -> 0x0014 }
                java.lang.String r13 = "&type=ANY&random_padding="
                r12.append(r13)     // Catch:{ all -> 0x0014 }
                r12.append(r10)     // Catch:{ all -> 0x0014 }
                java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0014 }
                r11.<init>(r12)     // Catch:{ all -> 0x0014 }
                java.net.URLConnection r12 = r11.openConnection()     // Catch:{ all -> 0x0014 }
                java.lang.String r13 = "User-Agent"
                java.lang.String r14 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r12.addRequestProperty(r13, r14)     // Catch:{ all -> 0x0014 }
                java.lang.String r13 = "Host"
                java.lang.String r14 = "dns.google.com"
                r12.addRequestProperty(r13, r14)     // Catch:{ all -> 0x0014 }
                r13 = 5000(0x1388, float:7.006E-42)
                r12.setConnectTimeout(r13)     // Catch:{ all -> 0x0014 }
                r12.setReadTimeout(r13)     // Catch:{ all -> 0x0014 }
                r12.connect()     // Catch:{ all -> 0x0014 }
                java.io.InputStream r13 = r12.getInputStream()     // Catch:{ all -> 0x0014 }
                r4 = r13
                long r13 = r12.getDate()     // Catch:{ all -> 0x0014 }
                r15 = 1000(0x3e8, double:4.94E-321)
                long r13 = r13 / r15
                int r14 = (int) r13     // Catch:{ all -> 0x0014 }
                r1.responseDate = r14     // Catch:{ all -> 0x0014 }
                java.io.ByteArrayOutputStream r13 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x0014 }
                r13.<init>()     // Catch:{ all -> 0x0014 }
                r3 = r13
                r13 = 32768(0x8000, float:4.5918E-41)
                byte[] r13 = new byte[r13]     // Catch:{ all -> 0x0014 }
            L_0x00bf:
                boolean r14 = r21.isCancelled()     // Catch:{ all -> 0x0014 }
                if (r14 == 0) goto L_0x00c6
                goto L_0x00d2
            L_0x00c6:
                int r14 = r4.read(r13)     // Catch:{ all -> 0x0014 }
                if (r14 <= 0) goto L_0x00d0
                r3.write(r13, r6, r14)     // Catch:{ all -> 0x0014 }
                goto L_0x00bf
            L_0x00d0:
            L_0x00d2:
                org.json.JSONObject r14 = new org.json.JSONObject     // Catch:{ all -> 0x0014 }
                java.lang.String r15 = new java.lang.String     // Catch:{ all -> 0x0014 }
                byte[] r6 = r3.toByteArray()     // Catch:{ all -> 0x0014 }
                r15.<init>(r6)     // Catch:{ all -> 0x0014 }
                r14.<init>(r15)     // Catch:{ all -> 0x0014 }
                r6 = r14
                java.lang.String r14 = "Answer"
                org.json.JSONArray r14 = r6.getJSONArray(r14)     // Catch:{ all -> 0x0014 }
                int r15 = r14.length()     // Catch:{ all -> 0x0014 }
                java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0014 }
                r0.<init>(r15)     // Catch:{ all -> 0x0014 }
                r17 = r0
                r0 = 0
            L_0x00f3:
                if (r0 >= r15) goto L_0x0126
                org.json.JSONObject r18 = r14.getJSONObject(r0)     // Catch:{ all -> 0x0014 }
                r19 = r18
                java.lang.String r1 = "type"
                r18 = r2
                r2 = r19
                int r1 = r2.getInt(r1)     // Catch:{ all -> 0x0189 }
                r19 = r6
                r6 = 16
                if (r1 == r6) goto L_0x010e
                r1 = r17
                goto L_0x011b
            L_0x010e:
                java.lang.String r6 = "data"
                java.lang.String r6 = r2.getString(r6)     // Catch:{ all -> 0x0189 }
                r20 = r1
                r1 = r17
                r1.add(r6)     // Catch:{ all -> 0x0189 }
            L_0x011b:
                int r0 = r0 + 1
                r17 = r1
                r2 = r18
                r6 = r19
                r1 = r21
                goto L_0x00f3
            L_0x0126:
                r18 = r2
                r19 = r6
                r1 = r17
                org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda1 r0 = org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda1.INSTANCE     // Catch:{ all -> 0x0189 }
                java.util.Collections.sort(r1, r0)     // Catch:{ all -> 0x0189 }
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0189 }
                r0.<init>()     // Catch:{ all -> 0x0189 }
                r2 = r0
                r0 = 0
            L_0x0138:
                int r6 = r1.size()     // Catch:{ all -> 0x0189 }
                if (r0 >= r6) goto L_0x015a
                java.lang.Object r6 = r1.get(r0)     // Catch:{ all -> 0x0189 }
                java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x0189 }
                r17 = r1
                java.lang.String r1 = "\""
                r20 = r7
                java.lang.String r7 = ""
                java.lang.String r1 = r6.replace(r1, r7)     // Catch:{ all -> 0x0189 }
                r2.append(r1)     // Catch:{ all -> 0x0189 }
                int r0 = r0 + 1
                r1 = r17
                r7 = r20
                goto L_0x0138
            L_0x015a:
                r17 = r1
                r20 = r7
                java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x0189 }
                r1 = 0
                byte[] r0 = android.util.Base64.decode(r0, r1)     // Catch:{ all -> 0x0189 }
                r1 = r0
                org.telegram.tgnet.NativeByteBuffer r0 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x0189 }
                int r6 = r1.length     // Catch:{ all -> 0x0189 }
                r0.<init>((int) r6)     // Catch:{ all -> 0x0189 }
                r6 = r0
                r6.writeBytes((byte[]) r1)     // Catch:{ all -> 0x0189 }
                if (r4 == 0) goto L_0x0181
                r4.close()     // Catch:{ all -> 0x0179 }
                goto L_0x0181
            L_0x0179:
                r0 = move-exception
                r7 = r0
                r0 = r7
                r7 = 0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r7)
                goto L_0x0182
            L_0x0181:
            L_0x0182:
                r3.close()     // Catch:{ Exception -> 0x0187 }
                goto L_0x0188
            L_0x0187:
                r0 = move-exception
            L_0x0188:
                return r6
            L_0x0189:
                r0 = move-exception
            L_0x018a:
                r1 = 0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r1)     // Catch:{ all -> 0x01ae }
                if (r4 == 0) goto L_0x019c
                r4.close()     // Catch:{ all -> 0x0194 }
                goto L_0x019c
            L_0x0194:
                r0 = move-exception
                r1 = r0
                r0 = r1
                r1 = 0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r1)
                goto L_0x019d
            L_0x019c:
            L_0x019d:
                if (r3 == 0) goto L_0x01a5
                r3.close()     // Catch:{ Exception -> 0x01a3 }
                goto L_0x01a5
            L_0x01a3:
                r0 = move-exception
                goto L_0x01a6
            L_0x01a5:
            L_0x01a6:
                int r5 = r5 + 1
                r1 = r21
                r2 = r18
                goto L_0x000a
            L_0x01ae:
                r0 = move-exception
                r1 = r0
                if (r4 == 0) goto L_0x01be
                r4.close()     // Catch:{ all -> 0x01b6 }
                goto L_0x01be
            L_0x01b6:
                r0 = move-exception
                r2 = r0
                r0 = r2
                r2 = 0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r2)
                goto L_0x01bf
            L_0x01be:
            L_0x01bf:
                if (r3 == 0) goto L_0x01c7
                r3.close()     // Catch:{ Exception -> 0x01c5 }
                goto L_0x01c7
            L_0x01c5:
                r0 = move-exception
                goto L_0x01c8
            L_0x01c7:
            L_0x01c8:
                throw r1
            L_0x01c9:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.DnsTxtLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        static /* synthetic */ int lambda$doInBackground$0(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda0(this, result));
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-tgnet-ConnectionsManager$DnsTxtLoadTask  reason: not valid java name */
        public /* synthetic */ void m1280xfe7b2956(NativeByteBuffer result) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
                FileLog.d("start google task");
            }
            GoogleDnsLoadTask task = new GoogleDnsLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            AsyncTask unused2 = ConnectionsManager.currentTask = task;
        }
    }

    private static class GoogleDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public GoogleDnsLoadTask(int instance) {
            this.currentAccount = instance;
        }

        /* access modifiers changed from: protected */
        public NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                String domain = ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : AccountInstance.getInstance(this.currentAccount).getMessagesController().dcDomainName;
                int len = Utilities.random.nextInt(116) + 13;
                Object obj = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM";
                StringBuilder padding = new StringBuilder(len);
                for (int a = 0; a < len; a++) {
                    padding.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM".charAt(Utilities.random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM".length())));
                }
                URLConnection httpConnection = new URL("https://dns.google.com/resolve?name=" + domain + "&type=ANY&random_padding=" + padding).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                httpConnection.connect();
                InputStream httpConnectionStream2 = httpConnection.getInputStream();
                this.responseDate = (int) (httpConnection.getDate() / 1000);
                ByteArrayOutputStream outbuf2 = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (true) {
                    if (!isCancelled()) {
                        int read = httpConnectionStream2.read(data);
                        if (read <= 0) {
                            break;
                        }
                        outbuf2.write(data, 0, read);
                    } else {
                        break;
                    }
                }
                JSONArray array = new JSONObject(new String(outbuf2.toByteArray())).getJSONArray("Answer");
                int len2 = array.length();
                ArrayList arrayList = new ArrayList(len2);
                int a2 = 0;
                while (a2 < len2) {
                    JSONObject object = array.getJSONObject(a2);
                    if (object.getInt("type") == 16) {
                        arrayList.add(object.getString("data"));
                    }
                    a2++;
                }
                Collections.sort(arrayList, ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda1.INSTANCE);
                StringBuilder builder = new StringBuilder();
                int a3 = 0;
                while (a3 < arrayList.size()) {
                    builder.append(((String) arrayList.get(a3)).replace("\"", ""));
                    a3++;
                    domain = domain;
                }
                byte[] bytes = Base64.decode(builder.toString(), 0);
                NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                if (httpConnectionStream2 != null) {
                    try {
                        httpConnectionStream2.close();
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                try {
                    outbuf2.close();
                } catch (Exception e) {
                }
                return buffer;
            } catch (Throwable th2) {
                FileLog.e(th2);
            }
            if (outbuf == null) {
                return null;
            }
            try {
                outbuf.close();
                return null;
            } catch (Exception e2) {
                return null;
            }
        }

        static /* synthetic */ int lambda$doInBackground$0(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda0(this, result));
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-tgnet-ConnectionsManager$GoogleDnsLoadTask  reason: not valid java name */
        public /* synthetic */ void m1285x742a8d37(NativeByteBuffer result) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get google result");
                FileLog.d("start mozilla task");
            }
            MozillaDnsLoadTask task = new MozillaDnsLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            AsyncTask unused2 = ConnectionsManager.currentTask = task;
        }
    }

    private static class MozillaDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public MozillaDnsLoadTask(int instance) {
            this.currentAccount = instance;
        }

        /* access modifiers changed from: protected */
        public NativeByteBuffer doInBackground(Void... voids) {
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                String domain = ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : AccountInstance.getInstance(this.currentAccount).getMessagesController().dcDomainName;
                int len = Utilities.random.nextInt(116) + 13;
                Object obj = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM";
                StringBuilder padding = new StringBuilder(len);
                for (int a = 0; a < len; a++) {
                    padding.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM".charAt(Utilities.random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzNUM".length())));
                }
                URLConnection httpConnection = new URL("https://mozilla.cloudflare-dns.com/dns-query?name=" + domain + "&type=TXT&random_padding=" + padding).openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("accept", "application/dns-json");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                httpConnection.connect();
                InputStream httpConnectionStream2 = httpConnection.getInputStream();
                this.responseDate = (int) (httpConnection.getDate() / 1000);
                ByteArrayOutputStream outbuf2 = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (true) {
                    if (!isCancelled()) {
                        int read = httpConnectionStream2.read(data);
                        if (read <= 0) {
                            break;
                        }
                        outbuf2.write(data, 0, read);
                    } else {
                        break;
                    }
                }
                JSONArray array = new JSONObject(new String(outbuf2.toByteArray())).getJSONArray("Answer");
                int len2 = array.length();
                ArrayList arrayList = new ArrayList(len2);
                int a2 = 0;
                while (a2 < len2) {
                    JSONObject object = array.getJSONObject(a2);
                    if (object.getInt("type") == 16) {
                        arrayList.add(object.getString("data"));
                    }
                    a2++;
                }
                Collections.sort(arrayList, ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda1.INSTANCE);
                StringBuilder builder = new StringBuilder();
                int a3 = 0;
                while (a3 < arrayList.size()) {
                    builder.append(((String) arrayList.get(a3)).replace("\"", ""));
                    a3++;
                    domain = domain;
                }
                byte[] bytes = Base64.decode(builder.toString(), 0);
                NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                if (httpConnectionStream2 != null) {
                    try {
                        httpConnectionStream2.close();
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                try {
                    outbuf2.close();
                } catch (Exception e) {
                }
                return buffer;
            } catch (Throwable th2) {
                FileLog.e(th2);
            }
            if (outbuf == null) {
                return null;
            }
            try {
                outbuf.close();
                return null;
            } catch (Exception e2) {
                return null;
            }
        }

        static /* synthetic */ int lambda$doInBackground$0(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda0(this, result));
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-tgnet-ConnectionsManager$MozillaDnsLoadTask  reason: not valid java name */
        public /* synthetic */ void m1286x8645eae8(NativeByteBuffer result) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), this.responseDate);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get mozilla txt result");
            }
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        public FirebaseTask(int instance) {
            this.currentAccount = instance;
        }

        /* access modifiers changed from: protected */
        public NativeByteBuffer doInBackground(Void... voids) {
            try {
                if (ConnectionsManager.native_isTestBackend(this.currentAccount) == 0) {
                    FirebaseRemoteConfig instance = FirebaseRemoteConfig.getInstance();
                    this.firebaseRemoteConfig = instance;
                    String currentValue = instance.getString("ipconfigv3");
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("current firebase value = " + currentValue);
                    }
                    this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda1(this));
                    return null;
                }
                throw new Exception("test backend");
            } catch (Throwable e) {
                Utilities.stageQueue.postRunnable(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda2(this));
                FileLog.e(e);
                return null;
            }
        }

        /* renamed from: lambda$doInBackground$2$org-telegram-tgnet-ConnectionsManager$FirebaseTask  reason: not valid java name */
        public /* synthetic */ void m1283xe0d4db4d(Task finishedTask) {
            Utilities.stageQueue.postRunnable(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda3(this, finishedTask.isSuccessful()));
        }

        /* renamed from: lambda$doInBackground$1$org-telegram-tgnet-ConnectionsManager$FirebaseTask  reason: not valid java name */
        public /* synthetic */ void m1282xb2fCLASSNAMEee(boolean success) {
            if (success) {
                this.firebaseRemoteConfig.activate().addOnCompleteListener(new ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda0(this));
            }
        }

        /* renamed from: lambda$doInBackground$0$org-telegram-tgnet-ConnectionsManager$FirebaseTask  reason: not valid java name */
        public /* synthetic */ void m1281x8523a68f(Task finishedTask2) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            String config = this.firebaseRemoteConfig.getString("ipconfigv3");
            if (!TextUtils.isEmpty(config)) {
                byte[] bytes = Base64.decode(config, 0);
                try {
                    NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                    buffer.writeBytes(bytes);
                    ConnectionsManager.native_applyDnsConfig(this.currentAccount, buffer.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), (int) (this.firebaseRemoteConfig.getInfo().getFetchTimeMillis() / 1000));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("failed to get firebase result");
                    FileLog.d("start dns txt task");
                }
                DnsTxtLoadTask task = new DnsTxtLoadTask(this.currentAccount);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                AsyncTask unused2 = ConnectionsManager.currentTask = task;
            }
        }

        /* renamed from: lambda$doInBackground$3$org-telegram-tgnet-ConnectionsManager$FirebaseTask  reason: not valid java name */
        public /* synthetic */ void m1284xead75ac() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get firebase result");
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask task = new DnsTxtLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            AsyncTask unused = ConnectionsManager.currentTask = task;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(NativeByteBuffer result) {
        }
    }
}
