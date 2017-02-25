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
    private static String zzbEz = "*gcore*:";
    private final Context mContext;
    private final String zzaHF;
    private final String zzaHH;
    private final WakeLock zzbEA;
    private final int zzbEB;
    private final String zzbEC;
    private boolean zzbED;
    private int zzbEE;
    private int zzbEF;
    private WorkSource zzbjq;

    public zzbay(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzbay(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, str2, str3, null);
    }

    @SuppressLint({"UnwrappedWakeLock"})
    public zzbay(Context context, int i, String str, String str2, String str3, String str4) {
        this.zzbED = true;
        zzac.zzh(str, "Wake lock name can NOT be empty");
        this.zzbEB = i;
        this.zzbEC = str2;
        this.zzaHH = str4;
        this.mContext = context.getApplicationContext();
        if ("com.google.android.gms".equals(context.getPackageName())) {
            this.zzaHF = str;
        } else {
            String valueOf = String.valueOf(zzbEz);
            String valueOf2 = String.valueOf(str);
            this.zzaHF = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        this.zzbEA = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        if (zzz.zzbf(this.mContext)) {
            if (zzw.zzdz(str3)) {
                str3 = context.getPackageName();
            }
            this.zzbjq = zzz.zzF(context, str3);
            zzc(this.zzbjq);
        }
    }

    private void zzd(WorkSource workSource) {
        try {
            this.zzbEA.setWorkSource(workSource);
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
            if (this.zzbED) {
                int i = this.zzbEE - 1;
                this.zzbEE = i;
                if (i != 0) {
                }
                zze.zzyX().zza(this.mContext, zzc.zza(this.zzbEA, zzo), 8, this.zzaHF, zzo, this.zzaHH, this.zzbEB, zzz.zzb(this.zzbjq));
                this.zzbEF--;
            }
            if (!this.zzbED) {
            }
        }
    }

    private boolean zzgN(String str) {
        return (TextUtils.isEmpty(str) || str.equals(this.zzbEC)) ? false : true;
    }

    private String zzo(String str, boolean z) {
        return this.zzbED ? z ? str : this.zzbEC : this.zzbEC;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzo(String str, long j) {
        boolean zzgN = zzgN(str);
        String zzo = zzo(str, zzgN);
        synchronized (this) {
            if (this.zzbED) {
                int i = this.zzbEE;
                this.zzbEE = i + 1;
                if (i != 0) {
                }
                zze.zzyX().zza(this.mContext, zzc.zza(this.zzbEA, zzo), 7, this.zzaHF, zzo, this.zzaHH, this.zzbEB, zzz.zzb(this.zzbjq), j);
                this.zzbEF++;
            }
            if (!this.zzbED) {
            }
        }
    }

    public void acquire(long j) {
        zzt.zzzg();
        zzo(null, j);
        this.zzbEA.acquire(j);
    }

    public boolean isHeld() {
        return this.zzbEA.isHeld();
    }

    public void release() {
        zzgM(null);
        this.zzbEA.release();
    }

    public void setReferenceCounted(boolean z) {
        this.zzbEA.setReferenceCounted(z);
        this.zzbED = z;
    }

    public void zzc(WorkSource workSource) {
        if (workSource != null && zzz.zzbf(this.mContext)) {
            if (this.zzbjq != null) {
                this.zzbjq.add(workSource);
            } else {
                this.zzbjq = workSource;
            }
            zzd(this.zzbjq);
        }
    }
}
