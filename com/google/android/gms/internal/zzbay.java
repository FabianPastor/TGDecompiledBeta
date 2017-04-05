package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.stats.zzc;
import com.google.android.gms.common.stats.zze;
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.common.util.zzw;
import com.google.android.gms.common.util.zzz;

public class zzbay {
    private static boolean DEBUG = false;
    private static String TAG = "WakeLock";
    private static String zzbEv = "*gcore*:";
    private final Context mContext;
    private final String zzaHF;
    private final String zzaHH;
    private int zzbEA;
    private int zzbEB;
    private final WakeLock zzbEw;
    private final int zzbEx;
    private final String zzbEy;
    private boolean zzbEz;
    private WorkSource zzbjm;

    public zzbay(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzbay(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzbay(Context context, int i, String str, String str2, String str3, String str4) {
        this.zzbEz = true;
        zzac.zzh(str, "Wake lock name can NOT be empty");
        this.zzbEx = i;
        this.zzbEy = str2;
        this.zzaHH = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.zzaHF = str;
        } else {
            String valueOf = String.valueOf(zzbEv);
            String valueOf2 = String.valueOf(str);
            this.zzaHF = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.zzbEw = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzz.zzbf(this.mContext)) {
            if (zzw.zzdz(str3)) {
                str3 = context.getPackageName();
            }
            this.zzbjm = zzz.zzF(context, str3);
            zzc(this.zzbjm);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.zzbEw.setWorkSource(workSource);
        } catch (IllegalArgumentException e) {
            Log.wtf(TAG, e.toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzgM(String str) {
        boolean zzgN = zzgN(str);
        String zzo = zzo(str, zzgN);
        synchronized (this) {
            if (this.zzbEz) {
                int i = this.zzbEA - 1;
                this.zzbEA = i;
                if (i != 0) {
                }
                zze.zzyX().zza(this.mContext, zzc.zza(this.zzbEw, zzo), 8, this.zzaHF, zzo, this.zzaHH, this.zzbEx, zzz.zzb(this.zzbjm));
                this.zzbEB--;
            }
            if (!this.zzbEz) {
            }
        }
    }

    private boolean zzgN(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.zzbEy)) ? false : true;
    }

    private String zzo(String str, boolean z) {
        return this.zzbEz ? z ? str : this.zzbEy : this.zzbEy;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzo(String str, long j) {
        boolean zzgN = zzgN(str);
        String zzo = zzo(str, zzgN);
        synchronized (this) {
            if (this.zzbEz) {
                int i = this.zzbEA;
                this.zzbEA = i + 1;
                if (i != 0) {
                }
                zze.zzyX().zza(this.mContext, zzc.zza(this.zzbEw, zzo), 7, this.zzaHF, zzo, this.zzaHH, this.zzbEx, zzz.zzb(this.zzbjm), j);
                this.zzbEB++;
            }
            if (!this.zzbEz) {
            }
        }
    }

    public void acquire(long j) {
        zzt.zzzg();
        zzo(null, j);
        this.zzbEw.acquire(j);
    }

    public boolean isHeld() {
        return this.zzbEw.isHeld();
    }

    public void release() {
        zzgM(null);
        this.zzbEw.release();
    }

    public void setReferenceCounted(boolean z) {
        this.zzbEw.setReferenceCounted(z);
        this.zzbEz = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzz.zzbf(this.mContext)) {
            if (this.zzbjm != null) {
                this.zzbjm.add(workSource);
            } else {
                this.zzbjm = workSource;
            }
            zzd(this.zzbjm);
        }
    }
}
