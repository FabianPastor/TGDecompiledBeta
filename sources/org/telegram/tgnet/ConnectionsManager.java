package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;

public class ConnectionsManager {
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
    public static final int FileTypeAudio = 50331648;
    public static final int FileTypeFile = 67108864;
    public static final int FileTypePhoto = 16777216;
    public static final int FileTypeVideo = 33554432;
    private static volatile ConnectionsManager[] Instance = new ConnectionsManager[3];
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    private static AsyncTask currentTask = null;
    private static ThreadLocal<HashMap<String, ResolvedDomain>> dnsCache = new C06911();
    private static final int dnsConfigVersion = 0;
    private static int lastClassGuid = 1;
    private static long lastDnsRequestTime;
    private boolean appPaused = true;
    private int appResumeCount;
    private int connectionState;
    private int currentAccount;
    private boolean isUpdating;
    private long lastPauseTime;
    private AtomicInteger lastRequestToken;

    /* renamed from: org.telegram.tgnet.ConnectionsManager$1 */
    static class C06911 extends ThreadLocal<HashMap<String, ResolvedDomain>> {
        C06911() {
        }

        protected HashMap<String, ResolvedDomain> initialValue() {
            return new HashMap();
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager$3 */
    class C06943 extends BroadcastReceiver {
        C06943() {
        }

        public void onReceive(Context context, Intent intent) {
            ConnectionsManager.this.checkConnection();
        }
    }

    private static class AzureLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;

        public AzureLoadTask(int i) {
            this.currentAccount = i;
        }

        protected org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r7) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r6 = this;
            r7 = 0;
            r0 = r6.currentAccount;	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r0);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            if (r0 == 0) goto L_0x0011;	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
        L_0x0009:
            r0 = new java.net.URL;	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r1 = "https://software-download.microsoft.com/test/config.txt";	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            goto L_0x0018;	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
        L_0x0011:
            r0 = new java.net.URL;	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r1 = "https://software-download.microsoft.com/prod/config.txt";	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
        L_0x0018:
            r0 = r0.openConnection();	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r1 = "User-Agent";	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.addRequestProperty(r1, r2);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r1 = "Host";	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r2 = "tcdnb.azureedge.net";	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.addRequestProperty(r1, r2);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.setConnectTimeout(r1);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.setReadTimeout(r1);	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0.connect();	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r0 = r0.getInputStream();	 Catch:{ Throwable -> 0x0091, all -> 0x008e }
            r1 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x0088, all -> 0x0082 }
            r1.<init>();	 Catch:{ Throwable -> 0x0088, all -> 0x0082 }
            r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r2 = new byte[r2];	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
        L_0x0043:
            r3 = r6.isCancelled();	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            r4 = 0;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            if (r3 == 0) goto L_0x004b;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
        L_0x004a:
            goto L_0x0056;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
        L_0x004b:
            r3 = r0.read(r2);	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            if (r3 <= 0) goto L_0x0055;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
        L_0x0051:
            r1.write(r2, r4, r3);	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            goto L_0x0043;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
        L_0x0055:
            r2 = -1;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
        L_0x0056:
            r2 = r1.toByteArray();	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            r2 = android.util.Base64.decode(r2, r4);	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            r4 = r2.length;	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            r3.<init>(r4);	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            r3.writeBytes(r2);	 Catch:{ Throwable -> 0x007c, all -> 0x0077 }
            if (r0 == 0) goto L_0x0071;
        L_0x0069:
            r0.close();	 Catch:{ Throwable -> 0x006d }
            goto L_0x0071;
        L_0x006d:
            r7 = move-exception;
            org.telegram.messenger.FileLog.m3e(r7);
        L_0x0071:
            if (r1 == 0) goto L_0x0076;
        L_0x0073:
            r1.close();	 Catch:{ Exception -> 0x0076 }
        L_0x0076:
            return r3;
        L_0x0077:
            r7 = move-exception;
            r5 = r0;
            r0 = r7;
            r7 = r5;
            goto L_0x00aa;
        L_0x007c:
            r2 = move-exception;
            r5 = r1;
            r1 = r0;
            r0 = r2;
            r2 = r5;
            goto L_0x0094;
        L_0x0082:
            r1 = move-exception;
            r5 = r1;
            r1 = r7;
            r7 = r0;
            r0 = r5;
            goto L_0x00aa;
        L_0x0088:
            r1 = move-exception;
            r2 = r7;
            r5 = r1;
            r1 = r0;
            r0 = r5;
            goto L_0x0094;
        L_0x008e:
            r0 = move-exception;
            r1 = r7;
            goto L_0x00aa;
        L_0x0091:
            r0 = move-exception;
            r1 = r7;
            r2 = r1;
        L_0x0094:
            org.telegram.messenger.FileLog.m3e(r0);	 Catch:{ all -> 0x00a7 }
            if (r1 == 0) goto L_0x00a1;
        L_0x0099:
            r1.close();	 Catch:{ Throwable -> 0x009d }
            goto L_0x00a1;
        L_0x009d:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x00a1:
            if (r2 == 0) goto L_0x00a6;
        L_0x00a3:
            r2.close();	 Catch:{ Exception -> 0x00a6 }
        L_0x00a6:
            return r7;
        L_0x00a7:
            r0 = move-exception;
            r7 = r1;
            r1 = r2;
        L_0x00aa:
            if (r7 == 0) goto L_0x00b4;
        L_0x00ac:
            r7.close();	 Catch:{ Throwable -> 0x00b0 }
            goto L_0x00b4;
        L_0x00b0:
            r7 = move-exception;
            org.telegram.messenger.FileLog.m3e(r7);
        L_0x00b4:
            if (r1 == 0) goto L_0x00b9;
        L_0x00b6:
            r1.close();	 Catch:{ Exception -> 0x00b9 }
        L_0x00b9:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.AzureLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        protected void onPostExecute(final NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    if (nativeByteBuffer != null) {
                        ConnectionsManager.currentTask = null;
                        ConnectionsManager.native_applyDnsConfig(AzureLoadTask.this.currentAccount, nativeByteBuffer.address);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("failed to get azure result");
                        FileLog.m0d("start dns txt task");
                    }
                    AsyncTask dnsTxtLoadTask = new DnsTxtLoadTask(AzureLoadTask.this.currentAccount);
                    dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    ConnectionsManager.currentTask = dnsTxtLoadTask;
                }
            });
        }
    }

    private static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;

        /* renamed from: org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$1 */
        class C07021 implements Comparator<String> {
            C07021() {
            }

            public int compare(String str, String str2) {
                str = str.length();
                str2 = str2.length();
                if (str > str2) {
                    return -1;
                }
                return str < str2 ? 1 : null;
            }
        }

        public DnsTxtLoadTask(int i) {
            this.currentAccount = i;
        }

        protected org.telegram.tgnet.NativeByteBuffer doInBackground(java.lang.Void... r11) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r10 = this;
            r11 = 0;
            r0 = java.util.Locale.US;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = r10.currentAccount;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = org.telegram.tgnet.ConnectionsManager.native_isTestBackend(r1);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            if (r1 == 0) goto L_0x000e;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
        L_0x000b:
            r1 = "tap%1$s.stel.com";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            goto L_0x0010;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
        L_0x000e:
            r1 = "ap%1$s.stel.com";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
        L_0x0010:
            r2 = 1;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2 = new java.lang.Object[r2];	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r3 = "";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r4 = 0;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2[r4] = r3;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0 = java.lang.String.format(r0, r1, r2);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = new java.net.URL;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2.<init>();	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r3 = "https://google.com/resolve?name=";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2.append(r3);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2.append(r0);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0 = "&type=16";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2.append(r0);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0 = r2.toString();	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1.<init>(r0);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0 = r1.openConnection();	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = "User-Agent";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0.addRequestProperty(r1, r2);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = "Host";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r2 = "dns.google.com";	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0.addRequestProperty(r1, r2);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0.setConnectTimeout(r1);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0.setReadTimeout(r1);	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0.connect();	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r0 = r0.getInputStream();	 Catch:{ Throwable -> 0x00ff, all -> 0x00fa }
            r1 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x00f6, all -> 0x00f1 }
            r1.<init>();	 Catch:{ Throwable -> 0x00f6, all -> 0x00f1 }
            r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r2 = new byte[r2];	 Catch:{ Throwable -> 0x00ef }
        L_0x0062:
            r3 = r10.isCancelled();	 Catch:{ Throwable -> 0x00ef }
            if (r3 == 0) goto L_0x0069;	 Catch:{ Throwable -> 0x00ef }
        L_0x0068:
            goto L_0x0074;	 Catch:{ Throwable -> 0x00ef }
        L_0x0069:
            r3 = r0.read(r2);	 Catch:{ Throwable -> 0x00ef }
            if (r3 <= 0) goto L_0x0073;	 Catch:{ Throwable -> 0x00ef }
        L_0x006f:
            r1.write(r2, r4, r3);	 Catch:{ Throwable -> 0x00ef }
            goto L_0x0062;	 Catch:{ Throwable -> 0x00ef }
        L_0x0073:
            r2 = -1;	 Catch:{ Throwable -> 0x00ef }
        L_0x0074:
            r2 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x00ef }
            r3 = new java.lang.String;	 Catch:{ Throwable -> 0x00ef }
            r5 = r1.toByteArray();	 Catch:{ Throwable -> 0x00ef }
            r6 = "UTF-8";	 Catch:{ Throwable -> 0x00ef }
            r3.<init>(r5, r6);	 Catch:{ Throwable -> 0x00ef }
            r2.<init>(r3);	 Catch:{ Throwable -> 0x00ef }
            r3 = "Answer";	 Catch:{ Throwable -> 0x00ef }
            r2 = r2.getJSONArray(r3);	 Catch:{ Throwable -> 0x00ef }
            r3 = r2.length();	 Catch:{ Throwable -> 0x00ef }
            r5 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x00ef }
            r5.<init>(r3);	 Catch:{ Throwable -> 0x00ef }
            r6 = r4;	 Catch:{ Throwable -> 0x00ef }
        L_0x0094:
            if (r6 >= r3) goto L_0x00a6;	 Catch:{ Throwable -> 0x00ef }
        L_0x0096:
            r7 = r2.getJSONObject(r6);	 Catch:{ Throwable -> 0x00ef }
            r8 = "data";	 Catch:{ Throwable -> 0x00ef }
            r7 = r7.getString(r8);	 Catch:{ Throwable -> 0x00ef }
            r5.add(r7);	 Catch:{ Throwable -> 0x00ef }
            r6 = r6 + 1;	 Catch:{ Throwable -> 0x00ef }
            goto L_0x0094;	 Catch:{ Throwable -> 0x00ef }
        L_0x00a6:
            r2 = new org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$1;	 Catch:{ Throwable -> 0x00ef }
            r2.<init>();	 Catch:{ Throwable -> 0x00ef }
            java.util.Collections.sort(r5, r2);	 Catch:{ Throwable -> 0x00ef }
            r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00ef }
            r2.<init>();	 Catch:{ Throwable -> 0x00ef }
            r3 = r4;	 Catch:{ Throwable -> 0x00ef }
        L_0x00b4:
            r6 = r5.size();	 Catch:{ Throwable -> 0x00ef }
            if (r3 >= r6) goto L_0x00ce;	 Catch:{ Throwable -> 0x00ef }
        L_0x00ba:
            r6 = r5.get(r3);	 Catch:{ Throwable -> 0x00ef }
            r6 = (java.lang.String) r6;	 Catch:{ Throwable -> 0x00ef }
            r7 = "\"";	 Catch:{ Throwable -> 0x00ef }
            r8 = "";	 Catch:{ Throwable -> 0x00ef }
            r6 = r6.replace(r7, r8);	 Catch:{ Throwable -> 0x00ef }
            r2.append(r6);	 Catch:{ Throwable -> 0x00ef }
            r3 = r3 + 1;	 Catch:{ Throwable -> 0x00ef }
            goto L_0x00b4;	 Catch:{ Throwable -> 0x00ef }
        L_0x00ce:
            r2 = r2.toString();	 Catch:{ Throwable -> 0x00ef }
            r2 = android.util.Base64.decode(r2, r4);	 Catch:{ Throwable -> 0x00ef }
            r3 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Throwable -> 0x00ef }
            r4 = r2.length;	 Catch:{ Throwable -> 0x00ef }
            r3.<init>(r4);	 Catch:{ Throwable -> 0x00ef }
            r3.writeBytes(r2);	 Catch:{ Throwable -> 0x00ef }
            if (r0 == 0) goto L_0x00e9;
        L_0x00e1:
            r0.close();	 Catch:{ Throwable -> 0x00e5 }
            goto L_0x00e9;
        L_0x00e5:
            r11 = move-exception;
            org.telegram.messenger.FileLog.m3e(r11);
        L_0x00e9:
            if (r1 == 0) goto L_0x00ee;
        L_0x00eb:
            r1.close();	 Catch:{ Exception -> 0x00ee }
        L_0x00ee:
            return r3;
        L_0x00ef:
            r2 = move-exception;
            goto L_0x0103;
        L_0x00f1:
            r1 = move-exception;
            r9 = r1;
            r1 = r11;
            r11 = r9;
            goto L_0x0117;
        L_0x00f6:
            r1 = move-exception;
            r2 = r1;
            r1 = r11;
            goto L_0x0103;
        L_0x00fa:
            r0 = move-exception;
            r1 = r11;
            r11 = r0;
            r0 = r1;
            goto L_0x0117;
        L_0x00ff:
            r0 = move-exception;
            r1 = r11;
            r2 = r0;
            r0 = r1;
        L_0x0103:
            org.telegram.messenger.FileLog.m3e(r2);	 Catch:{ all -> 0x0116 }
            if (r0 == 0) goto L_0x0110;
        L_0x0108:
            r0.close();	 Catch:{ Throwable -> 0x010c }
            goto L_0x0110;
        L_0x010c:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x0110:
            if (r1 == 0) goto L_0x0115;
        L_0x0112:
            r1.close();	 Catch:{ Exception -> 0x0115 }
        L_0x0115:
            return r11;
        L_0x0116:
            r11 = move-exception;
        L_0x0117:
            if (r0 == 0) goto L_0x0121;
        L_0x0119:
            r0.close();	 Catch:{ Throwable -> 0x011d }
            goto L_0x0121;
        L_0x011d:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x0121:
            if (r1 == 0) goto L_0x0126;
        L_0x0123:
            r1.close();	 Catch:{ Exception -> 0x0126 }
        L_0x0126:
            throw r11;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.DnsTxtLoadTask.doInBackground(java.lang.Void[]):org.telegram.tgnet.NativeByteBuffer");
        }

        protected void onPostExecute(final NativeByteBuffer nativeByteBuffer) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    if (nativeByteBuffer != null) {
                        ConnectionsManager.native_applyDnsConfig(DnsTxtLoadTask.this.currentAccount, nativeByteBuffer.address);
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("failed to get dns txt result");
                    }
                    ConnectionsManager.currentTask = null;
                }
            });
        }
    }

    private static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        /* renamed from: org.telegram.tgnet.ConnectionsManager$FirebaseTask$2 */
        class C07052 implements Runnable {
            C07052() {
            }

            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("failed to get firebase result");
                    FileLog.m0d("start azure task");
                }
                AsyncTask azureLoadTask = new AzureLoadTask(FirebaseTask.this.currentAccount);
                azureLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                ConnectionsManager.currentTask = azureLoadTask;
            }
        }

        /* renamed from: org.telegram.tgnet.ConnectionsManager$FirebaseTask$1 */
        class C18881 implements OnCompleteListener<Void> {
            C18881() {
            }

            public void onComplete(Task<Void> task) {
                task = task.isSuccessful();
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        Object string;
                        ConnectionsManager.currentTask = null;
                        if (task) {
                            FirebaseTask.this.firebaseRemoteConfig.activateFetched();
                            string = FirebaseTask.this.firebaseRemoteConfig.getString("ipconfig");
                        } else {
                            string = null;
                        }
                        if (TextUtils.isEmpty(string)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("failed to get firebase result");
                                FileLog.m0d("start azure task");
                            }
                            AsyncTask azureLoadTask = new AzureLoadTask(FirebaseTask.this.currentAccount);
                            azureLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = azureLoadTask;
                            return;
                        }
                        byte[] decode = Base64.decode(string, 0);
                        try {
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(decode.length);
                            nativeByteBuffer.writeBytes(decode);
                            ConnectionsManager.native_applyDnsConfig(FirebaseTask.this.currentAccount, nativeByteBuffer.address);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
            }
        }

        protected void onPostExecute(NativeByteBuffer nativeByteBuffer) {
        }

        public FirebaseTask(int i) {
            this.currentAccount = i;
        }

        protected NativeByteBuffer doInBackground(Void... voidArr) {
            try {
                this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                this.firebaseRemoteConfig.setConfigSettings(new Builder().setDeveloperModeEnabled(false).build());
                voidArr = this.firebaseRemoteConfig.getString("ipconfig");
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("current firebase value = ");
                    stringBuilder.append(voidArr);
                    FileLog.m0d(stringBuilder.toString());
                }
                this.firebaseRemoteConfig.fetch(0).addOnCompleteListener(new C18881());
            } catch (Throwable th) {
                Utilities.stageQueue.postRunnable(new C07052());
                FileLog.m3e(th);
            }
            return null;
        }
    }

    private static class ResolvedDomain {
        public String address;
        long ttl;

        public ResolvedDomain(String str, long j) {
            this.address = str;
            this.ttl = j;
        }
    }

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native void native_cleanUp(int i);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i5, boolean z, boolean z2, int i6);

    public static native int native_isTestBackend(int i);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setUseIpv6(int i, boolean z);

    public static native void native_setUserId(int i, int i2);

    public static native void native_switchBackend(int i);

    public static native void native_updateDcSettings(int i);

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

    public ConnectionsManager(int r16) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r15 = this;
        r13 = r15;
        r0 = r16;
        r13.<init>();
        r1 = java.lang.System.currentTimeMillis();
        r13.lastPauseTime = r1;
        r1 = 1;
        r13.appPaused = r1;
        r2 = new java.util.concurrent.atomic.AtomicInteger;
        r2.<init>(r1);
        r13.lastRequestToken = r2;
        r13.currentAccount = r0;
        r2 = r13.currentAccount;
        r2 = native_getConnectionState(r2);
        r13.connectionState = r2;
        r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        if (r0 == 0) goto L_0x0040;
    L_0x0026:
        r3 = new java.io.File;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "account";
        r4.append(r5);
        r4.append(r0);
        r0 = r4.toString();
        r3.<init>(r2, r0);
        r3.mkdirs();
        r2 = r3;
    L_0x0040:
        r9 = r2.toString();
        r0 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings();
        r2 = "pushConnection";
        r12 = r0.getBoolean(r2, r1);
        r0 = org.telegram.messenger.LocaleController.getSystemLocaleStringIso639();	 Catch:{ Exception -> 0x00b3 }
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x00b3 }
        r1 = org.telegram.messenger.LocaleController.getLocaleStringIso639();	 Catch:{ Exception -> 0x00b3 }
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x00b3 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b3 }
        r2.<init>();	 Catch:{ Exception -> 0x00b3 }
        r3 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x00b3 }
        r2.append(r3);	 Catch:{ Exception -> 0x00b3 }
        r3 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x00b3 }
        r2.append(r3);	 Catch:{ Exception -> 0x00b3 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x00b3 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00b3 }
        r3 = r3.getPackageManager();	 Catch:{ Exception -> 0x00b3 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00b3 }
        r4 = r4.getPackageName();	 Catch:{ Exception -> 0x00b3 }
        r5 = 0;	 Catch:{ Exception -> 0x00b3 }
        r3 = r3.getPackageInfo(r4, r5);	 Catch:{ Exception -> 0x00b3 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b3 }
        r4.<init>();	 Catch:{ Exception -> 0x00b3 }
        r5 = r3.versionName;	 Catch:{ Exception -> 0x00b3 }
        r4.append(r5);	 Catch:{ Exception -> 0x00b3 }
        r5 = " (";	 Catch:{ Exception -> 0x00b3 }
        r4.append(r5);	 Catch:{ Exception -> 0x00b3 }
        r3 = r3.versionCode;	 Catch:{ Exception -> 0x00b3 }
        r4.append(r3);	 Catch:{ Exception -> 0x00b3 }
        r3 = ")";	 Catch:{ Exception -> 0x00b3 }
        r4.append(r3);	 Catch:{ Exception -> 0x00b3 }
        r3 = r4.toString();	 Catch:{ Exception -> 0x00b3 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b3 }
        r4.<init>();	 Catch:{ Exception -> 0x00b3 }
        r5 = "SDK ";	 Catch:{ Exception -> 0x00b3 }
        r4.append(r5);	 Catch:{ Exception -> 0x00b3 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00b3 }
        r4.append(r5);	 Catch:{ Exception -> 0x00b3 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x00b3 }
        goto L_0x00ce;
    L_0x00b3:
        r0 = "en";
        r1 = "";
        r2 = "Android unknown";
        r3 = "App version unknown";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "SDK ";
        r4.append(r5);
        r5 = android.os.Build.VERSION.SDK_INT;
        r4.append(r5);
        r4 = r4.toString();
    L_0x00ce:
        r7 = r1;
        r1 = r0.trim();
        r1 = r1.length();
        if (r1 != 0) goto L_0x00db;
    L_0x00d9:
        r0 = "en";
    L_0x00db:
        r8 = r0;
        r0 = r2.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x00ea;
    L_0x00e6:
        r0 = "Android unknown";
        r5 = r0;
        goto L_0x00eb;
    L_0x00ea:
        r5 = r2;
    L_0x00eb:
        r0 = r3.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x00f9;
    L_0x00f5:
        r0 = "App version unknown";
        r6 = r0;
        goto L_0x00fa;
    L_0x00f9:
        r6 = r3;
    L_0x00fa:
        r0 = r4.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x0108;
    L_0x0104:
        r0 = "SDK Unknown";
        r10 = r0;
        goto L_0x0109;
    L_0x0108:
        r10 = r4;
    L_0x0109:
        r0 = r13.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0.loadConfig();
        r1 = org.telegram.messenger.BuildVars.BUILD_VERSION;
        r2 = 76;
        r3 = org.telegram.messenger.BuildVars.APP_ID;
        r11 = org.telegram.messenger.FileLog.getNetworkLogPath();
        r0 = r13.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r14 = r0.getClientUserId();
        r0 = r13;
        r4 = r5;
        r5 = r10;
        r10 = r11;
        r11 = r14;
        r0.init(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.<init>(int):void");
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
        return sendRequest(tLObject, requestDelegate, null, 0);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, null, null, i, DEFAULT_DATACENTER_ID, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i, int i2) {
        return sendRequest(tLObject, requestDelegate, null, null, i, DEFAULT_DATACENTER_ID, i2, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, quickAckDelegate, null, i, DEFAULT_DATACENTER_ID, 1, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i, int i2, int i3, boolean z) {
        int andIncrement = this.lastRequestToken.getAndIncrement();
        final TLObject tLObject2 = tLObject;
        final int i4 = andIncrement;
        final RequestDelegate requestDelegate2 = requestDelegate;
        final QuickAckDelegate quickAckDelegate2 = quickAckDelegate;
        final WriteToSocketDelegate writeToSocketDelegate2 = writeToSocketDelegate;
        final int i5 = i;
        final int i6 = i2;
        final int i7 = i3;
        final boolean z2 = z;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.tgnet.ConnectionsManager$2$1 */
            class C18871 implements RequestDelegateInternal {
                C18871() {
                }

                public void run(long j, int i, String str, int i2) {
                    TLObject tLObject = null;
                    if (j != 0) {
                        try {
                            j = NativeByteBuffer.wrap(j);
                            j.reused = true;
                            tLObject = tLObject2.deserializeResponse(j, j.readInt32(true), true);
                            j = null;
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            return;
                        }
                    } else if (str != null) {
                        j = new TL_error();
                        j.code = i;
                        j.text = str;
                        if (BuildVars.LOGS_ENABLED) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(tLObject2);
                            stringBuilder.append(" got error ");
                            stringBuilder.append(j.code);
                            stringBuilder.append(" ");
                            stringBuilder.append(j.text);
                            FileLog.m1e(stringBuilder.toString());
                        }
                    } else {
                        j = 0;
                    }
                    if (tLObject != null) {
                        tLObject.networkType = i2;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("java received ");
                        stringBuilder.append(tLObject);
                        stringBuilder.append(" error = ");
                        stringBuilder.append(j);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            requestDelegate2.run(tLObject, j);
                            if (tLObject != null) {
                                tLObject.freeResources();
                            }
                        }
                    });
                }
            }

            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("send request ");
                    stringBuilder.append(tLObject2);
                    stringBuilder.append(" with token = ");
                    stringBuilder.append(i4);
                    FileLog.m0d(stringBuilder.toString());
                }
                try {
                    AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(tLObject2.getObjectSize());
                    tLObject2.serializeToStream(nativeByteBuffer);
                    tLObject2.freeResources();
                    ConnectionsManager.native_sendRequest(ConnectionsManager.this.currentAccount, nativeByteBuffer.address, new C18871(), quickAckDelegate2, writeToSocketDelegate2, i5, i6, i7, z2, i4);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
        return andIncrement;
    }

    public void cancelRequest(int i, boolean z) {
        native_cancelRequest(this.currentAccount, i, z);
    }

    public void cleanup() {
        native_cleanUp(this.currentAccount);
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
        if (this.connectionState == 3 && this.isUpdating) {
            return 5;
        }
        return this.connectionState;
    }

    public void setUserId(int i) {
        native_setUserId(this.currentAccount, i);
    }

    private void checkConnection() {
        native_setUseIpv6(this.currentAccount, useIpv6Address());
        native_setNetworkAvailable(this.currentAccount, isNetworkOnline(), getCurrentNetworkType());
    }

    public void setPushConnectionEnabled(boolean z) {
        native_setPushConnectionEnabled(this.currentAccount, z);
    }

    public void init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, String str7, int i4, boolean z) {
        ConnectionsManager connectionsManager = this;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        Object string = sharedPreferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID);
        String string2 = sharedPreferences.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID);
        String string3 = sharedPreferences.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID);
        int i5 = sharedPreferences.getInt("proxy_port", 1080);
        if (sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(string)) {
            native_setProxySettings(connectionsManager.currentAccount, string, i5, string2, string3);
        }
        native_init(connectionsManager.currentAccount, i, i2, i3, str, str2, str3, str4, str5, str6, str7, i4, z, isNetworkOnline(), getCurrentNetworkType());
        checkConnection();
        ApplicationLoader.applicationContext.registerReceiver(new C06943(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public static void setLangCode(String str) {
        str = str.replace('_', '-').toLowerCase();
        for (int i = 0; i < 3; i++) {
            native_setLangCode(i, str);
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

    public void setAppPaused(boolean z, boolean z2) {
        if (!z2) {
            this.appPaused = z;
            if (BuildVars.LOGS_ENABLED) {
                z2 = new StringBuilder();
                z2.append("app paused = ");
                z2.append(z);
                FileLog.m0d(z2.toString());
            }
            if (z) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                z = new StringBuilder();
                z.append("app resume count ");
                z.append(this.appResumeCount);
                FileLog.m0d(z.toString());
            }
            if (this.appResumeCount >= false) {
                this.appResumeCount = 0;
            }
        }
        if (!this.appResumeCount) {
            if (!this.lastPauseTime) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork(this.currentAccount);
        } else if (!this.appPaused) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("reset app pause time");
            }
            if (this.lastPauseTime && System.currentTimeMillis() - this.lastPauseTime > true) {
                ContactsController.getInstance(this.currentAccount).checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long j, final int i) {
        try {
            j = NativeByteBuffer.wrap(j);
            j.reused = true;
            j = TLClassStore.Instance().TLdeserialize(j, j.readInt32(true), true);
            if (j instanceof Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("java received ");
                    stringBuilder.append(j);
                    FileLog.m0d(stringBuilder.toString());
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesController.getInstance(i).processUpdates((Updates) j, false);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onUpdate(final int i) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.getInstance(i).updateTimerProc();
            }
        });
    }

    public static void onSessionCreated(final int i) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.getInstance(i).getDifference();
            }
        });
    }

    public static void onConnectionStateChanged(final int i, final int i2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ConnectionsManager.getInstance(i2).connectionState = i;
                NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
            }
        });
    }

    public static void onLogout(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (UserConfig.getInstance(i).getClientUserId() != 0) {
                    UserConfig.getInstance(i).clearConfig();
                    MessagesController.getInstance(i).performLogout(false);
                }
            }
        });
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return 1;
        }
        return isRoaming() ? 2 : 0;
    }

    public static void onBytesSent(int i, int i2, int i3) {
        try {
            StatsController.getInstance(i3).incrementSentBytesCount(i2, 6, (long) i);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(final int i, final int i2) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (ConnectionsManager.currentTask == null && (i != 0 || Math.abs(ConnectionsManager.lastDnsRequestTime - System.currentTimeMillis()) >= 10000)) {
                    if (ConnectionsManager.isNetworkOnline()) {
                        ConnectionsManager.lastDnsRequestTime = System.currentTimeMillis();
                        AsyncTask dnsTxtLoadTask;
                        if (i == 2) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start dns txt task");
                            }
                            dnsTxtLoadTask = new DnsTxtLoadTask(i2);
                            dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = dnsTxtLoadTask;
                        } else if (i == 1) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start azure dns task");
                            }
                            dnsTxtLoadTask = new AzureLoadTask(i2);
                            dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = dnsTxtLoadTask;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start firebase task");
                            }
                            dnsTxtLoadTask = new FirebaseTask(i2);
                            dnsTxtLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                            ConnectionsManager.currentTask = dnsTxtLoadTask;
                        }
                        return;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("don't start task, current task = ");
                    stringBuilder.append(ConnectionsManager.currentTask);
                    stringBuilder.append(" next task = ");
                    stringBuilder.append(i);
                    stringBuilder.append(" time diff = ");
                    stringBuilder.append(Math.abs(ConnectionsManager.lastDnsRequestTime - System.currentTimeMillis()));
                    stringBuilder.append(" network = ");
                    stringBuilder.append(ConnectionsManager.isNetworkOnline());
                    FileLog.m0d(stringBuilder.toString());
                }
            }
        });
    }

    public static java.lang.String getHostByName(java.lang.String r7, int r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = dnsCache;
        r8 = r8.get();
        r8 = (java.util.HashMap) r8;
        r0 = r8.get(r7);
        r0 = (org.telegram.tgnet.ConnectionsManager.ResolvedDomain) r0;
        if (r0 == 0) goto L_0x0022;
    L_0x0010:
        r1 = android.os.SystemClock.uptimeMillis();
        r3 = r0.ttl;
        r5 = r1 - r3;
        r1 = 300000; // 0x493e0 float:4.2039E-40 double:1.482197E-318;
        r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1));
        if (r3 >= 0) goto L_0x0022;
    L_0x001f:
        r7 = r0.address;
        return r7;
    L_0x0022:
        r0 = 0;
        r1 = new java.net.URL;	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2.<init>();	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r3 = "https://google.com/resolve?name=";	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2.append(r3);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2.append(r7);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r3 = "&type=A";	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2.append(r3);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = r2.toString();	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1.<init>(r2);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1 = r1.openConnection();	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = "User-Agent";	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r3 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1.addRequestProperty(r2, r3);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = "Host";	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r3 = "dns.google.com";	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1.addRequestProperty(r2, r3);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1.setConnectTimeout(r2);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1.setReadTimeout(r2);	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1.connect();	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r1 = r1.getInputStream();	 Catch:{ Throwable -> 0x00de, all -> 0x00db }
        r2 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x00d7, all -> 0x00d5 }
        r2.<init>();	 Catch:{ Throwable -> 0x00d7, all -> 0x00d5 }
        r0 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r0 = new byte[r0];	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
    L_0x006b:
        r3 = r1.read(r0);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        if (r3 <= 0) goto L_0x0076;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
    L_0x0071:
        r4 = 0;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r2.write(r0, r4, r3);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        goto L_0x006b;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
    L_0x0076:
        r0 = -1;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r0 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3 = new java.lang.String;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r4 = r2.toByteArray();	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3.<init>(r4);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r0.<init>(r3);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3 = "Answer";	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r0 = r0.getJSONArray(r3);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3 = r0.length();	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        if (r3 <= 0) goto L_0x00c1;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
    L_0x0091:
        r3 = org.telegram.messenger.Utilities.random;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r4 = r0.length();	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3 = r3.nextInt(r4);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r0 = r0.getJSONObject(r3);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3 = "data";	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r0 = r0.getString(r3);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3 = new org.telegram.tgnet.ConnectionsManager$ResolvedDomain;	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r4 = android.os.SystemClock.uptimeMillis();	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r3.<init>(r0, r4);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        r8.put(r7, r3);	 Catch:{ Throwable -> 0x00d3, all -> 0x00d1 }
        if (r1 == 0) goto L_0x00bb;
    L_0x00b3:
        r1.close();	 Catch:{ Throwable -> 0x00b7 }
        goto L_0x00bb;
    L_0x00b7:
        r7 = move-exception;
        org.telegram.messenger.FileLog.m3e(r7);
    L_0x00bb:
        if (r2 == 0) goto L_0x00c0;
    L_0x00bd:
        r2.close();	 Catch:{ Exception -> 0x00c0 }
    L_0x00c0:
        return r0;
    L_0x00c1:
        if (r1 == 0) goto L_0x00cb;
    L_0x00c3:
        r1.close();	 Catch:{ Throwable -> 0x00c7 }
        goto L_0x00cb;
    L_0x00c7:
        r7 = move-exception;
        org.telegram.messenger.FileLog.m3e(r7);
    L_0x00cb:
        if (r2 == 0) goto L_0x00f0;
    L_0x00cd:
        r2.close();	 Catch:{ Exception -> 0x00f0 }
        goto L_0x00f0;
    L_0x00d1:
        r7 = move-exception;
        goto L_0x00f5;
    L_0x00d3:
        r7 = move-exception;
        goto L_0x00d9;
    L_0x00d5:
        r7 = move-exception;
        goto L_0x00f6;
    L_0x00d7:
        r7 = move-exception;
        r2 = r0;
    L_0x00d9:
        r0 = r1;
        goto L_0x00e0;
    L_0x00db:
        r7 = move-exception;
        r1 = r0;
        goto L_0x00f6;
    L_0x00de:
        r7 = move-exception;
        r2 = r0;
    L_0x00e0:
        org.telegram.messenger.FileLog.m3e(r7);	 Catch:{ all -> 0x00f3 }
        if (r0 == 0) goto L_0x00ed;
    L_0x00e5:
        r0.close();	 Catch:{ Throwable -> 0x00e9 }
        goto L_0x00ed;
    L_0x00e9:
        r7 = move-exception;
        org.telegram.messenger.FileLog.m3e(r7);
    L_0x00ed:
        if (r2 == 0) goto L_0x00f0;
    L_0x00ef:
        goto L_0x00cd;
    L_0x00f0:
        r7 = "";
        return r7;
    L_0x00f3:
        r7 = move-exception;
        r1 = r0;
    L_0x00f5:
        r0 = r2;
    L_0x00f6:
        if (r1 == 0) goto L_0x0100;
    L_0x00f8:
        r1.close();	 Catch:{ Throwable -> 0x00fc }
        goto L_0x0100;
    L_0x00fc:
        r8 = move-exception;
        org.telegram.messenger.FileLog.m3e(r8);
    L_0x0100:
        if (r0 == 0) goto L_0x0105;
    L_0x0102:
        r0.close();	 Catch:{ Exception -> 0x0105 }
    L_0x0105:
        throw r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.getHostByName(java.lang.String, int):java.lang.String");
    }

    public static void onBytesReceived(int i, int i2, int i3) {
        try {
            StatsController.getInstance(i3).incrementReceivedBytesCount(i2, 6, (long) i);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onUpdateConfig(long j, final int i) {
        try {
            j = NativeByteBuffer.wrap(j);
            j.reused = true;
            j = TL_config.TLdeserialize(j, j.readInt32(true), true);
            if (j != null) {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        MessagesController.getInstance(i).updateConfig(j);
                    }
                });
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static void onInternalPushReceived(int i) {
        KeepAliveJob.startJob();
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public static boolean isRoaming() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isRoaming();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return false;
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            State state = networkInfo.getState();
            if (networkInfo != null && (state == State.CONNECTED || state == State.CONNECTING || state == State.SUSPENDED)) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return false;
    }

    public static boolean isConnectedToWiFi() {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            if (networkInfo != null && networkInfo.getState() == State.CONNECTED) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return false;
    }

    public void setIsUpdating(final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (ConnectionsManager.this.isUpdating != z) {
                    ConnectionsManager.this.isUpdating = z;
                    if (ConnectionsManager.this.connectionState == 3) {
                        NotificationCenter.getInstance(ConnectionsManager.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
                    }
                }
            }
        });
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (VERSION.SDK_INT < 19) {
            return false;
        }
        Enumeration networkInterfaces;
        if (BuildVars.LOGS_ENABLED) {
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                        if (!networkInterface.getInterfaceAddresses().isEmpty()) {
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("valid interface: ");
                                stringBuilder.append(networkInterface);
                                FileLog.m0d(stringBuilder.toString());
                            }
                            List interfaceAddresses = networkInterface.getInterfaceAddresses();
                            for (int i = 0; i < interfaceAddresses.size(); i++) {
                                InetAddress address = ((InterfaceAddress) interfaceAddresses.get(i)).getAddress();
                                if (BuildVars.LOGS_ENABLED) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("address: ");
                                    stringBuilder2.append(address.getHostAddress());
                                    FileLog.m0d(stringBuilder2.toString());
                                }
                                if (!(address.isLinkLocalAddress() || address.isLoopbackAddress())) {
                                    if (!address.isMulticastAddress()) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("address is good");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
        }
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean z = false;
            boolean z2 = z;
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface2 = (NetworkInterface) networkInterfaces.nextElement();
                if (networkInterface2.isUp()) {
                    if (!networkInterface2.isLoopback()) {
                        List interfaceAddresses2 = networkInterface2.getInterfaceAddresses();
                        boolean z3 = z2;
                        z2 = z;
                        for (int i2 = 0; i2 < interfaceAddresses2.size(); i2++) {
                            InetAddress address2 = ((InterfaceAddress) interfaceAddresses2.get(i2)).getAddress();
                            if (!(address2.isLinkLocalAddress() || address2.isLoopbackAddress())) {
                                if (!address2.isMulticastAddress()) {
                                    if (address2 instanceof Inet6Address) {
                                        z3 = true;
                                    } else if ((address2 instanceof Inet4Address) && !address2.getHostAddress().startsWith("192.0.0.")) {
                                        z2 = true;
                                    }
                                }
                            }
                        }
                        z = z2;
                        z2 = z3;
                    }
                }
            }
            if (z || !z2) {
                return false;
            }
            return true;
        } catch (Throwable th2) {
            FileLog.m3e(th2);
        }
    }

    public static boolean isNetworkOnline() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && (activeNetworkInfo.isConnectedOrConnecting() || activeNetworkInfo.isAvailable())) {
                return true;
            }
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
            NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(1);
            if (networkInfo2 == null || !networkInfo2.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return true;
        }
    }
}
