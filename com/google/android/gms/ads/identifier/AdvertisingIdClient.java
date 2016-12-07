package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.stats.zzb;
import com.google.android.gms.common.zzc;
import com.google.android.gms.internal.zzci;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AdvertisingIdClient {
    private final Context mContext;
    com.google.android.gms.common.zza zzaku;
    zzci zzakv;
    boolean zzakw;
    Object zzakx;
    zza zzaky;
    final long zzakz;

    public static final class Info {
        private final String zzale;
        private final boolean zzalf;

        public Info(String str, boolean z) {
            this.zzale = str;
            this.zzalf = z;
        }

        public String getId() {
            return this.zzale;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.zzalf;
        }

        public String toString() {
            String str = this.zzale;
            return new StringBuilder(String.valueOf(str).length() + 7).append("{").append(str).append("}").append(this.zzalf).toString();
        }
    }

    static class zza extends Thread {
        private WeakReference<AdvertisingIdClient> zzala;
        private long zzalb;
        CountDownLatch zzalc = new CountDownLatch(1);
        boolean zzald = false;

        public zza(AdvertisingIdClient advertisingIdClient, long j) {
            this.zzala = new WeakReference(advertisingIdClient);
            this.zzalb = j;
            start();
        }

        private void disconnect() {
            AdvertisingIdClient advertisingIdClient = (AdvertisingIdClient) this.zzala.get();
            if (advertisingIdClient != null) {
                advertisingIdClient.finish();
                this.zzald = true;
            }
        }

        public void cancel() {
            this.zzalc.countDown();
        }

        public void run() {
            try {
                if (!this.zzalc.await(this.zzalb, TimeUnit.MILLISECONDS)) {
                    disconnect();
                }
            } catch (InterruptedException e) {
                disconnect();
            }
        }

        public boolean zzdo() {
            return this.zzald;
        }
    }

    public AdvertisingIdClient(Context context) {
        this(context, 30000);
    }

    public AdvertisingIdClient(Context context, long j) {
        this.zzakx = new Object();
        zzac.zzy(context);
        this.mContext = context;
        this.zzakw = false;
        this.zzakz = j;
    }

    public static Info getAdvertisingIdInfo(Context context) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient(context, -1);
        try {
            advertisingIdClient.zze(false);
            Info info = advertisingIdClient.getInfo();
            return info;
        } finally {
            advertisingIdClient.finish();
        }
    }

    public static void setShouldSkipGmsCoreVersionCheck(boolean z) {
    }

    static zzci zza(Context context, com.google.android.gms.common.zza com_google_android_gms_common_zza) throws IOException {
        try {
            return com.google.android.gms.internal.zzci.zza.zzf(com_google_android_gms_common_zza.zza(10000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            throw new IOException("Interrupted exception");
        } catch (Throwable th) {
            IOException iOException = new IOException(th);
        }
    }

    private void zzdn() {
        synchronized (this.zzakx) {
            if (this.zzaky != null) {
                this.zzaky.cancel();
                try {
                    this.zzaky.join();
                } catch (InterruptedException e) {
                }
            }
            if (this.zzakz > 0) {
                this.zzaky = new zza(this, this.zzakz);
            }
        }
    }

    static com.google.android.gms.common.zza zzg(Context context) throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        try {
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            switch (zzc.zzapd().isGooglePlayServicesAvailable(context)) {
                case 0:
                case 2:
                    ServiceConnection com_google_android_gms_common_zza = new com.google.android.gms.common.zza();
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    try {
                        if (zzb.zzawu().zza(context, intent, com_google_android_gms_common_zza, 1)) {
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
        zzac.zzhr("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.mContext == null || this.zzaku == null) {
            } else {
                try {
                    if (this.zzakw) {
                        zzb.zzawu().zza(this.mContext, this.zzaku);
                    }
                } catch (Throwable e) {
                    Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e);
                }
                this.zzakw = false;
                this.zzakv = null;
                this.zzaku = null;
            }
        }
    }

    public Info getInfo() throws IOException {
        Info info;
        zzac.zzhr("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (!this.zzakw) {
                synchronized (this.zzakx) {
                    if (this.zzaky == null || !this.zzaky.zzdo()) {
                        throw new IOException("AdvertisingIdClient is not connected.");
                    }
                }
                try {
                    zze(false);
                    if (!this.zzakw) {
                        throw new IOException("AdvertisingIdClient cannot reconnect.");
                    }
                } catch (Throwable e) {
                    Log.i("AdvertisingIdClient", "GMS remote exception ", e);
                    throw new IOException("Remote exception");
                } catch (Throwable e2) {
                    throw new IOException("AdvertisingIdClient cannot reconnect.", e2);
                }
            }
            zzac.zzy(this.zzaku);
            zzac.zzy(this.zzakv);
            info = new Info(this.zzakv.getId(), this.zzakv.zzf(true));
        }
        zzdn();
        return info;
    }

    public void start() throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zze(true);
    }

    protected void zze(boolean z) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zzac.zzhr("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.zzakw) {
                finish();
            }
            this.zzaku = zzg(this.mContext);
            this.zzakv = zza(this.mContext, this.zzaku);
            this.zzakw = true;
            if (z) {
                zzdn();
            }
        }
    }
}
