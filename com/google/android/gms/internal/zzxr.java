package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.stats.zzg;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzv;
import com.google.android.gms.common.util.zzy;

public class zzxr {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String aDy = "*gcore*:";
    private final String Gc;
    private final String Ge;
    private final int aDA;
    private final String aDB;
    private boolean aDC;
    private int aDD;
    private int aDE;
    private final WakeLock aDz;
    private WorkSource ajz;
    private final Context mContext;

    public zzxr(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzxr(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzxr(Context context, int i, String str, String str2, String str3, String str4) {
        this.aDC = true;
        zzaa.zzh(str, "Wake lock name can NOT be empty");
        this.aDA = i;
        this.aDB = str2;
        this.Ge = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.Gc = str;
        } else {
            String valueOf = String.valueOf(aDy);
            String valueOf2 = String.valueOf(str);
            this.Gc = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.aDz = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzy.zzcm(this.mContext)) {
            if (zzv.zzij(str3)) {
                str3 = context.getPackageName();
            }
            this.ajz = zzy.zzy(context, str3);
            zzc(this.ajz);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.aDz.setWorkSource(workSource);
        } catch (IllegalArgumentException e) {
            Log.wtf(TAG, e.toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzk(String str, long j) {
        boolean zzoo = zzoo(str);
        String zzp = zzp(str, zzoo);
        synchronized (this) {
            if (this.aDC) {
                int i = this.aDD;
                this.aDD = i + 1;
                if (i != 0) {
                }
                zzg.zzayg().zza(this.mContext, zze.zza(this.aDz, zzp), 7, this.Gc, zzp, this.Ge, this.aDA, zzy.zzb(this.ajz), j);
                this.aDE++;
            }
            if (!this.aDC) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzon(String str) {
        boolean zzoo = zzoo(str);
        String zzp = zzp(str, zzoo);
        synchronized (this) {
            if (this.aDC) {
                int i = this.aDD - 1;
                this.aDD = i;
                if (i != 0) {
                }
                zzg.zzayg().zza(this.mContext, zze.zza(this.aDz, zzp), 8, this.Gc, zzp, this.Ge, this.aDA, zzy.zzb(this.ajz));
                this.aDE--;
            }
            if (!this.aDC) {
            }
        }
    }

    private boolean zzoo(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.aDB)) ? false : true;
    }

    private String zzp(String str, boolean z) {
        return this.aDC ? z ? str : this.aDB : this.aDB;
    }

    public void acquire(long j) {
        if (!zzs.zzayq() && this.aDC) {
            String str = TAG;
            String str2 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ";
            String valueOf = String.valueOf(this.Gc);
            Log.wtf(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzk(null, j);
        this.aDz.acquire(j);
    }

    public boolean isHeld() {
        return this.aDz.isHeld();
    }

    public void release() {
        zzon(null);
        this.aDz.release();
    }

    public void setReferenceCounted(boolean z) {
        this.aDz.setReferenceCounted(z);
        this.aDC = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzy.zzcm(this.mContext)) {
            if (this.ajz != null) {
                this.ajz.add(workSource);
            } else {
                this.ajz = workSource;
            }
            zzd(this.ajz);
        }
    }
}
