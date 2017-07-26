package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbo;

public final class zzcga {
    private final long zzaiB;
    private /* synthetic */ zzcfw zzbrF;
    private String zzbrH;
    private final String zzbrI;
    private final String zzbrJ;

    private zzcga(zzcfw com_google_android_gms_internal_zzcfw, String str, long j) {
        this.zzbrF = com_google_android_gms_internal_zzcfw;
        zzbo.zzcF(str);
        zzbo.zzaf(j > 0);
        this.zzbrH = String.valueOf(str).concat(":start");
        this.zzbrI = String.valueOf(str).concat(":count");
        this.zzbrJ = String.valueOf(str).concat(":value");
        this.zzaiB = j;
    }

    @WorkerThread
    private final void zzma() {
        this.zzbrF.zzjC();
        long currentTimeMillis = this.zzbrF.zzkq().currentTimeMillis();
        Editor edit = this.zzbrF.zzaix.edit();
        edit.remove(this.zzbrI);
        edit.remove(this.zzbrJ);
        edit.putLong(this.zzbrH, currentTimeMillis);
        edit.apply();
    }

    @WorkerThread
    private final long zzmc() {
        return this.zzbrF.zzyF().getLong(this.zzbrH, 0);
    }

    @WorkerThread
    public final void zzf(String str, long j) {
        this.zzbrF.zzjC();
        if (zzmc() == 0) {
            zzma();
        }
        if (str == null) {
            str = "";
        }
        long j2 = this.zzbrF.zzaix.getLong(this.zzbrI, 0);
        if (j2 <= 0) {
            Editor edit = this.zzbrF.zzaix.edit();
            edit.putString(this.zzbrJ, str);
            edit.putLong(this.zzbrI, 1);
            edit.apply();
            return;
        }
        Object obj = (this.zzbrF.zzwB().zzzt().nextLong() & Long.MAX_VALUE) < Long.MAX_VALUE / (j2 + 1) ? 1 : null;
        Editor edit2 = this.zzbrF.zzaix.edit();
        if (obj != null) {
            edit2.putString(this.zzbrJ, str);
        }
        edit2.putLong(this.zzbrI, j2 + 1);
        edit2.apply();
    }

    @WorkerThread
    public final Pair<String, Long> zzmb() {
        this.zzbrF.zzjC();
        this.zzbrF.zzjC();
        long zzmc = zzmc();
        if (zzmc == 0) {
            zzma();
            zzmc = 0;
        } else {
            zzmc = Math.abs(zzmc - this.zzbrF.zzkq().currentTimeMillis());
        }
        if (zzmc < this.zzaiB) {
            return null;
        }
        if (zzmc > (this.zzaiB << 1)) {
            zzma();
            return null;
        }
        String string = this.zzbrF.zzyF().getString(this.zzbrJ, null);
        long j = this.zzbrF.zzyF().getLong(this.zzbrI, 0);
        zzma();
        return (string == null || j <= 0) ? zzcfw.zzbri : new Pair(string, Long.valueOf(j));
    }
}
