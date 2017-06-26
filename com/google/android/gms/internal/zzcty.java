package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.stats.zzc;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzx;

public final class zzcty {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String zzbCW = "*gcore*:";
    private final Context mContext;
    private final String zzaJp;
    private final String zzaJr;
    private final WakeLock zzbCX;
    private WorkSource zzbCY;
    private final int zzbCZ;
    private final String zzbDa;
    private boolean zzbDb;
    private int zzbDc;
    private int zzbDd;

    public zzcty(Context context, int i, String str) {
        this(context, 1, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    private zzcty(Context context, int i, String str, String str2, String str3) {
        this(context, 1, str, null, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    private zzcty(Context context, int i, String str, String str2, String str3, String str4) {
        this.zzbDb = true;
        zzbo.zzh(str, "Wake lock name can NOT be empty");
        this.zzbCZ = i;
        this.zzbDa = null;
        this.zzaJr = null;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.zzaJp = str;
        } else {
            String valueOf = String.valueOf(zzbCW);
            String valueOf2 = String.valueOf(str);
            this.zzaJp = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.zzbCX = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzx.zzaM(this.mContext)) {
            if (zzt.zzcL(str3)) {
                str3 = context.getPackageName();
            }
            this.zzbCY = zzx.zzD(context, str3);
            WorkSource workSource = this.zzbCY;
            if (workSource != null && zzx.zzaM(this.mContext)) {
                if (this.zzbCY != null) {
                    this.zzbCY.add(workSource);
                } else {
                    this.zzbCY = workSource;
                }
                try {
                    this.zzbCX.setWorkSource(this.zzbCY);
                } catch (IllegalArgumentException e) {
                    Log.wtf(TAG, e.toString());
                }
            }
        }
    }

    private final boolean zzeV(String str) {
        Object obj = null;
        return (TextUtils.isEmpty(obj) || obj.equals(this.zzbDa)) ? false : true;
    }

    private final String zzi(String str, boolean z) {
        return this.zzbDb ? z ? null : this.zzbDa : this.zzbDa;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void acquire(long j) {
        boolean zzeV = zzeV(null);
        String zzi = zzi(null, zzeV);
        synchronized (this) {
            if (this.zzbDb) {
                int i = this.zzbDc;
                this.zzbDc = i + 1;
                if (i != 0) {
                }
                zze.zzrX();
                zze.zza(this.mContext, zzc.zza(this.zzbCX, zzi), 7, this.zzaJp, zzi, null, this.zzbCZ, zzx.zzb(this.zzbCY), 1000);
                this.zzbDd++;
            }
            if (!this.zzbDb) {
            }
        }
        this.zzbCX.acquire(1000);
    }

    public final boolean isHeld() {
        return this.zzbCX.isHeld();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void release() {
        boolean zzeV = zzeV(null);
        String zzi = zzi(null, zzeV);
        synchronized (this) {
            if (this.zzbDb) {
                int i = this.zzbDc - 1;
                this.zzbDc = i;
                if (i != 0) {
                }
                zze.zzrX();
                zze.zza(this.mContext, zzc.zza(this.zzbCX, zzi), 8, this.zzaJp, zzi, null, this.zzbCZ, zzx.zzb(this.zzbCY));
                this.zzbDd--;
            }
            if (!this.zzbDb) {
            }
        }
        this.zzbCX.release();
    }

    public final void setReferenceCounted(boolean z) {
        this.zzbCX.setReferenceCounted(false);
        this.zzbDb = false;
    }
}
