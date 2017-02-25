package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.zzc;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GoogleCloudMessaging {
    public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
    public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
    public static final String INSTANCE_ID_SCOPE = "GCM";
    @Deprecated
    public static final String MESSAGE_TYPE_DELETED = "deleted_messages";
    @Deprecated
    public static final String MESSAGE_TYPE_MESSAGE = "gcm";
    @Deprecated
    public static final String MESSAGE_TYPE_SEND_ERROR = "send_error";
    @Deprecated
    public static final String MESSAGE_TYPE_SEND_EVENT = "send_event";
    public static int zzbgB = 5000000;
    public static int zzbgC = 6500000;
    public static int zzbgD = 7000000;
    static GoogleCloudMessaging zzbgE;
    private static final AtomicInteger zzbgH = new AtomicInteger(1);
    private PendingIntent zzbgF;
    private Map<String, Handler> zzbgG = Collections.synchronizedMap(new HashMap());
    private final BlockingQueue<Intent> zzbgI = new LinkedBlockingQueue();
    final Messenger zzbgJ = new Messenger(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ GoogleCloudMessaging zzbgK;

        public void handleMessage(Message message) {
            if (message == null || !(message.obj instanceof Intent)) {
                Log.w(GoogleCloudMessaging.INSTANCE_ID_SCOPE, "Dropping invalid message");
            }
            Intent intent = (Intent) message.obj;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())) {
                this.zzbgK.zzbgI.add(intent);
            } else if (!this.zzbgK.zzq(intent)) {
                intent.setPackage(this.zzbgK.zzqn.getPackageName());
                this.zzbgK.zzqn.sendBroadcast(intent);
            }
        }
    });
    private Context zzqn;

    public static synchronized GoogleCloudMessaging getInstance(Context context) {
        GoogleCloudMessaging googleCloudMessaging;
        synchronized (GoogleCloudMessaging.class) {
            if (zzbgE == null) {
                zzbgE = new GoogleCloudMessaging();
                zzbgE.zzqn = context.getApplicationContext();
            }
            googleCloudMessaging = zzbgE;
        }
        return googleCloudMessaging;
    }

    private String zzGR() {
        String valueOf = String.valueOf("google.rpc");
        String valueOf2 = String.valueOf(String.valueOf(zzbgH.getAndIncrement()));
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }

    static String zza(Intent intent, String str) throws IOException {
        if (intent == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String stringExtra = intent.getStringExtra(str);
        if (stringExtra != null) {
            return stringExtra;
        }
        stringExtra = intent.getStringExtra("error");
        if (stringExtra != null) {
            throw new IOException(stringExtra);
        }
        throw new IOException("SERVICE_NOT_AVAILABLE");
    }

    private void zza(String str, String str2, long j, int i, Bundle bundle) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("Missing 'to'");
        }
        String zzbv = zzbv(this.zzqn);
        if (zzbv == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        zzr(intent);
        intent.setPackage(zzbv);
        intent.putExtra("google.to", str);
        intent.putExtra("google.message_id", str2);
        intent.putExtra("google.ttl", Long.toString(j));
        intent.putExtra("google.delay", Integer.toString(i));
        intent.putExtra("google.from", zzeE(str));
        if (zzbv.contains(".gsf")) {
            Bundle bundle2 = new Bundle();
            for (String zzbv2 : bundle.keySet()) {
                Object obj = bundle.get(zzbv2);
                if (obj instanceof String) {
                    String str3 = "gcm.";
                    zzbv2 = String.valueOf(zzbv2);
                    bundle2.putString(zzbv2.length() != 0 ? str3.concat(zzbv2) : new String(str3), (String) obj);
                }
            }
            bundle2.putString("google.to", str);
            bundle2.putString("google.message_id", str2);
            InstanceID.getInstance(this.zzqn).zzc(INSTANCE_ID_SCOPE, "upstream", bundle2);
            return;
        }
        this.zzqn.sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    public static String zzbv(Context context) {
        return zzc.zzbA(context);
    }

    public static int zzbw(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String zzbv = zzbv(context);
        if (zzbv != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(zzbv, 0);
                if (packageInfo != null) {
                    return packageInfo.versionCode;
                }
            } catch (NameNotFoundException e) {
            }
        }
        return -1;
    }

    private String zzeE(String str) {
        int indexOf = str.indexOf(64);
        if (indexOf > 0) {
            str = str.substring(0, indexOf);
        }
        return InstanceID.getInstance(this.zzqn).zzHi().zzh("", str, INSTANCE_ID_SCOPE);
    }

    private boolean zzq(Intent intent) {
        Object stringExtra = intent.getStringExtra("In-Reply-To");
        if (stringExtra == null && intent.hasExtra("error")) {
            stringExtra = intent.getStringExtra("google.message_id");
        }
        if (stringExtra != null) {
            Handler handler = (Handler) this.zzbgG.remove(stringExtra);
            if (handler != null) {
                Message obtain = Message.obtain();
                obtain.obj = intent;
                return handler.sendMessage(obtain);
            }
        }
        return false;
    }

    public void close() {
        zzbgE = null;
        zza.zzbgq = null;
        zzGS();
    }

    public String getMessageType(Intent intent) {
        if (!"com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            return null;
        }
        String stringExtra = intent.getStringExtra("message_type");
        return stringExtra == null ? MESSAGE_TYPE_MESSAGE : stringExtra;
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    @Deprecated
    public synchronized String register(String... strArr) throws IOException {
        String zzbv;
        zzbv = zzbv(this.zzqn);
        if (zzbv == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String zzf = zzf(strArr);
        Bundle bundle = new Bundle();
        if (zzbv.contains(".gsf")) {
            bundle.putString("legacy.sender", zzf);
            zzbv = InstanceID.getInstance(this.zzqn).getToken(zzf, INSTANCE_ID_SCOPE, bundle);
        } else {
            bundle.putString("sender", zzf);
            zzbv = zza(zzJ(bundle), "registration_id");
        }
        return zzbv;
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void send(String str, String str2, long j, Bundle bundle) throws IOException {
        zza(str, str2, j, -1, bundle);
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void send(String str, String str2, Bundle bundle) throws IOException {
        send(str, str2, -1, bundle);
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    @Deprecated
    public synchronized void unregister() throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        InstanceID.getInstance(this.zzqn).deleteInstanceID();
    }

    synchronized void zzGS() {
        if (this.zzbgF != null) {
            this.zzbgF.cancel();
            this.zzbgF = null;
        }
    }

    @Deprecated
    Intent zzJ(Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        } else if (zzbw(this.zzqn) < 0) {
            throw new IOException("Google Play Services missing");
        } else {
            if (bundle == null) {
                bundle = new Bundle();
            }
            Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzbv(this.zzqn));
            zzr(intent);
            intent.putExtra("google.message_id", zzGR());
            intent.putExtras(bundle);
            intent.putExtra("google.messenger", this.zzbgJ);
            this.zzqn.startService(intent);
            try {
                return (Intent) this.zzbgI.poll(30000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    String zzf(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            throw new IllegalArgumentException("No senderIds");
        }
        StringBuilder stringBuilder = new StringBuilder(strArr[0]);
        for (int i = 1; i < strArr.length; i++) {
            stringBuilder.append(',').append(strArr[i]);
        }
        return stringBuilder.toString();
    }

    synchronized void zzr(Intent intent) {
        if (this.zzbgF == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzbgF = PendingIntent.getBroadcast(this.zzqn, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzbgF);
    }
}
