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
    private static String zzbEy = "*gcore*:";
    private final Context mContext;
    private final String zzaHF;
    private final String zzaHH;
    private final int zzbEA;
    private final String zzbEB;
    private boolean zzbEC;
    private int zzbED;
    private int zzbEE;
    private final WakeLock zzbEz;
    private WorkSource zzbjl;

    public zzbay(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzbay(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzbay(Context context, int i, String str, String str2, String str3, String str4) {
        this.zzbEC = true;
        zzac.zzh(str, "Wake lock name can NOT be empty");
        this.zzbEA = i;
        this.zzbEB = str2;
        this.zzaHH = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.zzaHF = str;
        } else {
            String valueOf = String.valueOf(zzbEy);
            String valueOf2 = String.valueOf(str);
            this.zzaHF = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.zzbEz = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzz.zzbf(this.mContext)) {
            if (zzw.zzdz(str3)) {
                str3 = context.getPackageName();
            }
            this.zzbjl = zzz.zzG(context, str3);
            zzc(this.zzbjl);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.zzbEz.setWorkSource(workSource);
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
            if (this.zzbEC) {
                int i = this.zzbED - 1;
                this.zzbED = i;
                if (i != 0) {
                }
                zze.zzyX().zza(this.mContext, zzc.zza(this.zzbEz, zzo), 8, this.zzaHF, zzo, this.zzaHH, this.zzbEA, zzz.zzb(this.zzbjl));
                this.zzbEE--;
            }
            if (!this.zzbEC) {
            }
        }
    }

    private boolean zzgN(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.zzbEB)) ? false : true;
    }

    private String zzo(String str, boolean z) {
        return this.zzbEC ? z ? str : this.zzbEB : this.zzbEB;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzo(String str, long j) {
        boolean zzgN = zzgN(str);
        String zzo = zzo(str, zzgN);
        synchronized (this) {
            if (this.zzbEC) {
                int i = this.zzbED;
                this.zzbED = i + 1;
                if (i != 0) {
                }
                zze.zzyX().zza(this.mContext, zzc.zza(this.zzbEz, zzo), 7, this.zzaHF, zzo, this.zzaHH, this.zzbEA, zzz.zzb(this.zzbjl), j);
                this.zzbEE++;
            }
            if (!this.zzbEC) {
            }
        }
    }

    public void acquire(long j) {
        zzt.zzzg();
        zzo(null, j);
        this.zzbEz.acquire(j);
    }

    public boolean isHeld() {
        return this.zzbEz.isHeld();
    }

    public void release() {
        zzgM(null);
        this.zzbEz.release();
    }

    public void setReferenceCounted(boolean z) {
        this.zzbEz.setReferenceCounted(z);
        this.zzbEC = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzz.zzbf(this.mContext)) {
            if (this.zzbjl != null) {
                this.zzbjl.add(workSource);
            } else {
                this.zzbjl = workSource;
            }
            zzd(this.zzbjl);
        }
    }
}
