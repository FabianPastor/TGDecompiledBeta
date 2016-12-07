package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.stats.zzf;
import com.google.android.gms.common.stats.zzh;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzw;
import com.google.android.gms.common.util.zzz;

public class zzxb {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String aAo = "*gcore*:";
    private final String EA;
    private final String Ey;
    private final WakeLock aAp;
    private final int aAq;
    private final String aAr;
    private boolean aAs;
    private int aAt;
    private int aAu;
    private WorkSource agC;
    private final Context mContext;

    public zzxb(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzxb(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzxb(Context context, int i, String str, String str2, String str3, String str4) {
        this.aAs = true;
        zzac.zzh(str, "Wake lock name can NOT be empty");
        this.aAq = i;
        this.aAr = str2;
        this.EA = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.Ey = str;
        } else {
            String valueOf = String.valueOf(aAo);
            String valueOf2 = String.valueOf(str);
            this.Ey = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.aAp = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzz.zzcp(this.mContext)) {
            if (zzw.zzij(str3)) {
                str3 = context.getPackageName();
            }
            this.agC = zzz.zzy(context, str3);
            zzc(this.agC);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.aAp.setWorkSource(workSource);
        } catch (IllegalArgumentException e) {
            Log.wtf(TAG, e.toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzm(String str, long j) {
        boolean zzop = zzop(str);
        String zzp = zzp(str, zzop);
        synchronized (this) {
            if (this.aAs) {
                int i = this.aAt;
                this.aAt = i + 1;
                if (i != 0) {
                }
                zzh.zzaxf().zza(this.mContext, zzf.zza(this.aAp, zzp), 7, this.Ey, zzp, this.EA, this.aAq, zzz.zzb(this.agC), j);
                this.aAu++;
            }
            if (!this.aAs) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzoo(String str) {
        boolean zzop = zzop(str);
        String zzp = zzp(str, zzop);
        synchronized (this) {
            if (this.aAs) {
                int i = this.aAt - 1;
                this.aAt = i;
                if (i != 0) {
                }
                zzh.zzaxf().zza(this.mContext, zzf.zza(this.aAp, zzp), 8, this.Ey, zzp, this.EA, this.aAq, zzz.zzb(this.agC));
                this.aAu--;
            }
            if (!this.aAs) {
            }
        }
    }

    private boolean zzop(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.aAr)) ? false : true;
    }

    private String zzp(String str, boolean z) {
        return this.aAs ? z ? str : this.aAr : this.aAr;
    }

    public void acquire(long j) {
        if (!zzs.zzaxn() && this.aAs) {
            String str = TAG;
            String str2 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ";
            String valueOf = String.valueOf(this.Ey);
            Log.wtf(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzm(null, j);
        this.aAp.acquire(j);
    }

    public boolean isHeld() {
        return this.aAp.isHeld();
    }

    public void release() {
        zzoo(null);
        this.aAp.release();
    }

    public void setReferenceCounted(boolean z) {
        this.aAp.setReferenceCounted(z);
        this.aAs = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzz.zzcp(this.mContext)) {
            if (this.agC != null) {
                this.agC.add(workSource);
            } else {
                this.agC = workSource;
            }
            zzd(this.agC);
        }
    }
}
