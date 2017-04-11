package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zze;
import com.google.android.gms.common.zzg;
import com.google.android.gms.internal.zzcq;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AdvertisingIdClient {
    private final Context mContext;
    com.google.android.gms.common.zza zzsa;
    zzcq zzsb;
    boolean zzsc;
    Object zzsd;
    zza zzse;
    final long zzsf;

    public static final class Info {
        private final String zzsl;
        private final boolean zzsm;

        public Info(String str, boolean z) {
            this.zzsl = str;
            this.zzsm = z;
        }

        public String getId() {
            return this.zzsl;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.zzsm;
        }

        public String toString() {
            String str = this.zzsl;
            return new StringBuilder(String.valueOf(str).length() + 7).append("{").append(str).append("}").append(this.zzsm).toString();
        }
    }

    static class zza extends Thread {
        private WeakReference<AdvertisingIdClient> zzsh;
        private long zzsi;
        CountDownLatch zzsj = new CountDownLatch(1);
        boolean zzsk = false;

        public zza(AdvertisingIdClient advertisingIdClient, long j) {
            this.zzsh = new WeakReference(advertisingIdClient);
            this.zzsi = j;
            start();
        }

        private void disconnect() {
            AdvertisingIdClient advertisingIdClient = (AdvertisingIdClient) this.zzsh.get();
            if (advertisingIdClient != null) {
                advertisingIdClient.finish();
                this.zzsk = true;
            }
        }

        public void cancel() {
            this.zzsj.countDown();
        }

        public void run() {
            try {
                if (!this.zzsj.await(this.zzsi, TimeUnit.MILLISECONDS)) {
                    disconnect();
                }
            } catch (InterruptedException e) {
                disconnect();
            }
        }

        public boolean zzbx() {
            return this.zzsk;
        }
    }

    public AdvertisingIdClient(Context context) {
        this(context, 30000, false);
    }

    public AdvertisingIdClient(Context context, long j, boolean z) {
        this.zzsd = new Object();
        zzac.zzw(context);
        if (z) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                context = applicationContext;
            }
            this.mContext = context;
        } else {
            this.mContext = context;
        }
        this.zzsc = false;
        this.zzsf = j;
    }

    public static Info getAdvertisingIdInfo(Context context) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        float f = 0.0f;
        boolean z = false;
        try {
            Context remoteContext = zzg.getRemoteContext(context);
            if (remoteContext != null) {
                SharedPreferences sharedPreferences = remoteContext.getSharedPreferences("google_ads_flags", 1);
                z = sharedPreferences.getBoolean("gads:ad_id_app_context:enabled", false);
                f = sharedPreferences.getFloat("gads:ad_id_app_context:ping_ratio", 0.0f);
            }
        } catch (Throwable e) {
            Log.w("AdvertisingIdClient", "Error while reading from SharedPreferences ", e);
        }
        AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient(context, -1, z);
        Info info;
        try {
            advertisingIdClient.zze(false);
            info = advertisingIdClient.getInfo();
            advertisingIdClient.zza(info, z, f, null);
            return info;
        } catch (Throwable th) {
            info = th;
            advertisingIdClient.zza(null, z, f, info);
            return null;
        } finally {
            advertisingIdClient.finish();
        }
    }

    public static void setShouldSkipGmsCoreVersionCheck(boolean z) {
    }

    static zzcq zza(Context context, com.google.android.gms.common.zza com_google_android_gms_common_zza) throws IOException {
        try {
            return com.google.android.gms.internal.zzcq.zza.zzf(com_google_android_gms_common_zza.zza(10000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            throw new IOException("Interrupted exception");
        } catch (Throwable th) {
            IOException iOException = new IOException(th);
        }
    }

    private void zza(Info info, boolean z, float f, Throwable th) {
        if (Math.random() <= ((double) f)) {
            final String uri = zza(info, z, th).toString();
            new Thread(this) {
                public void run() {
                    new zza().zzu(uri);
                }
            }.start();
        }
    }

    private void zzbw() {
        synchronized (this.zzsd) {
            if (this.zzse != null) {
                this.zzse.cancel();
                try {
                    this.zzse.join();
                } catch (InterruptedException e) {
                }
            }
            if (this.zzsf > 0) {
                this.zzse = new zza(this, this.zzsf);
            }
        }
    }

    static com.google.android.gms.common.zza zzf(Context context) throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        try {
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            switch (zze.zzuY().isGooglePlayServicesAvailable(context)) {
                case 0:
                case 2:
                    ServiceConnection com_google_android_gms_common_zza = new com.google.android.gms.common.zza();
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    try {
                        if (com.google.android.gms.common.stats.zza.zzyJ().zza(context, intent, com_google_android_gms_common_zza, 1)) {
                            return com_google_android_gms_common_zza;
                        }
                        throw new IOException("Connection failure");
                    } catch (Throwable th) {
                        IOException iOException = new IOException(th);
                    }
                default:
                    throw new IOException("Google Play services not available");
            }
        } catch (NameNotFoundException e) {
            throw new GooglePlayServicesNotAvailableException(9);
        }
    }

    protected void finalize() throws Throwable {
        finish();
        super.finalize();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finish() {
        zzac.zzdk("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.mContext == null || this.zzsa == null) {
            } else {
                try {
                    if (this.zzsc) {
                        com.google.android.gms.common.stats.zza.zzyJ().zza(this.mContext, this.zzsa);
                    }
                } catch (Throwable e) {
                    Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e);
                } catch (Throwable e2) {
                    Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e2);
                }
                this.zzsc = false;
                this.zzsb = null;
                this.zzsa = null;
            }
        }
    }

    public Info getInfo() throws IOException {
        Info info;
        zzac.zzdk("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (!this.zzsc) {
                synchronized (this.zzsd) {
                    if (this.zzse == null || !this.zzse.zzbx()) {
                        throw new IOException("AdvertisingIdClient is not connected.");
                    }
                }
                try {
                    zze(false);
                    if (!this.zzsc) {
                        throw new IOException("AdvertisingIdClient cannot reconnect.");
                    }
                } catch (Throwable e) {
                    Log.i("AdvertisingIdClient", "GMS remote exception ", e);
                    throw new IOException("Remote exception");
                } catch (Throwable e2) {
                    throw new IOException("AdvertisingIdClient cannot reconnect.", e2);
                }
            }
            zzac.zzw(this.zzsa);
            zzac.zzw(this.zzsb);
            info = new Info(this.zzsb.getId(), this.zzsb.zzf(true));
        }
        zzbw();
        return info;
    }

    public void start() throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zze(true);
    }

    Uri zza(Info info, boolean z, Throwable th) {
        Bundle bundle = new Bundle();
        bundle.putString("app_context", z ? "1" : "0");
        if (info != null) {
            bundle.putString("limit_ad_tracking", info.isLimitAdTrackingEnabled() ? "1" : "0");
        }
        if (!(info == null || info.getId() == null)) {
            bundle.putString("ad_id_size", Integer.toString(info.getId().length()));
        }
        if (th != null) {
            bundle.putString("error", th.getClass().getName());
        }
        Builder buildUpon = Uri.parse("https://pagead2.googlesyndication.com/pagead/gen_204?id=gmob-apps").buildUpon();
        for (String str : bundle.keySet()) {
            buildUpon.appendQueryParameter(str, bundle.getString(str));
        }
        return buildUpon.build();
    }

    protected void zze(boolean z) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zzac.zzdk("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.zzsc) {
                finish();
            }
            this.zzsa = zzf(this.mContext);
            this.zzsb = zza(this.mContext, this.zzsa);
            this.zzsc = true;
            if (z) {
                zzbw();
            }
        }
    }
}
