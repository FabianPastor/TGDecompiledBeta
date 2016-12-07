package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.stats.zzg;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzv;
import com.google.android.gms.common.util.zzy;

public class zzayd {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String zzbCt = "*gcore*:";
    private final Context mContext;
    private final String zzaGw;
    private final String zzaGy;
    private final WakeLock zzbCu;
    private final int zzbCv;
    private final String zzbCw;
    private boolean zzbCx;
    private int zzbCy;
    private int zzbCz;
    private WorkSource zzbiJ;

    public zzayd(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzayd(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzayd(Context context, int i, String str, String str2, String str3, String str4) {
        this.zzbCx = true;
        zzac.zzh(str, "Wake lock name can NOT be empty");
        this.zzbCv = i;
        this.zzbCw = str2;
        this.zzaGy = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.zzaGw = str;
        } else {
            String valueOf = String.valueOf(zzbCt);
            String valueOf2 = String.valueOf(str);
            this.zzaGw = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.zzbCu = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzy.zzaO(this.mContext)) {
            if (zzv.zzdD(str3)) {
                str3 = context.getPackageName();
            }
            this.zzbiJ = zzy.zzy(context, str3);
            zzc(this.zzbiJ);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.zzbCu.setWorkSource(workSource);
        } catch (IllegalArgumentException e) {
            Log.wtf(TAG, e.toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzgP(String str) {
        boolean zzgQ = zzgQ(str);
        String zzo = zzo(str, zzgQ);
        synchronized (this) {
            if (this.zzbCx) {
                int i = this.zzbCy - 1;
                this.zzbCy = i;
                if (i != 0) {
                }
                zzg.zzyr().zza(this.mContext, zze.zza(this.zzbCu, zzo), 8, this.zzaGw, zzo, this.zzaGy, this.zzbCv, zzy.zzb(this.zzbiJ));
                this.zzbCz--;
            }
            if (!this.zzbCx) {
            }
        }
    }

    private boolean zzgQ(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.zzbCw)) ? false : true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzm(String str, long j) {
        boolean zzgQ = zzgQ(str);
        String zzo = zzo(str, zzgQ);
        synchronized (this) {
            if (this.zzbCx) {
                int i = this.zzbCy;
                this.zzbCy = i + 1;
                if (i != 0) {
                }
                zzg.zzyr().zza(this.mContext, zze.zza(this.zzbCu, zzo), 7, this.zzaGw, zzo, this.zzaGy, this.zzbCv, zzy.zzb(this.zzbiJ), j);
                this.zzbCz++;
            }
            if (!this.zzbCx) {
            }
        }
    }

    private String zzo(String str, boolean z) {
        return this.zzbCx ? z ? str : this.zzbCw : this.zzbCw;
    }

    public void acquire(long j) {
        if (!zzs.zzyA() && this.zzbCx) {
            String str = TAG;
            String str2 = "Do not acquire with timeout on reference counted WakeLocks before ICS. wakelock: ";
            String valueOf = String.valueOf(this.zzaGw);
            Log.wtf(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzm(null, j);
        this.zzbCu.acquire(j);
    }

    public boolean isHeld() {
        return this.zzbCu.isHeld();
    }

    public void release() {
        zzgP(null);
        this.zzbCu.release();
    }

    public void setReferenceCounted(boolean z) {
        this.zzbCu.setReferenceCounted(z);
        this.zzbCx = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzy.zzaO(this.mContext)) {
            if (this.zzbiJ != null) {
                this.zzbiJ.add(workSource);
            } else {
                this.zzbiJ = workSource;
            }
            zzd(this.zzbiJ);
        }
    }
}
