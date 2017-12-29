package com.google.android.gms.internal;

import android.content.SharedPreferences.Editor;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbq;

public final class zzcib {
    private final long zzdyr;
    private /* synthetic */ zzchx zzjdm;
    private String zzjdo;
    private final String zzjdp;
    private final String zzjdq;

    private zzcib(zzchx com_google_android_gms_internal_zzchx, String str, long j) {
        this.zzjdm = com_google_android_gms_internal_zzchx;
        zzbq.zzgm(str);
        zzbq.checkArgument(j > 0);
        this.zzjdo = String.valueOf(str).concat(":start");
        this.zzjdp = String.valueOf(str).concat(":count");
        this.zzjdq = String.valueOf(str).concat(":value");
        this.zzdyr = j;
    }

    private final void zzaac() {
        this.zzjdm.zzve();
        long currentTimeMillis = this.zzjdm.zzws().currentTimeMillis();
        Editor edit = this.zzjdm.zzazl().edit();
        edit.remove(this.zzjdp);
        edit.remove(this.zzjdq);
        edit.putLong(this.zzjdo, currentTimeMillis);
        edit.apply();
    }

    private final long zzaae() {
        return this.zzjdm.zzazl().getLong(this.zzjdo, 0);
    }

    public final Pair<String, Long> zzaad() {
        this.zzjdm.zzve();
        this.zzjdm.zzve();
        long zzaae = zzaae();
        if (zzaae == 0) {
            zzaac();
            zzaae = 0;
        } else {
            zzaae = Math.abs(zzaae - this.zzjdm.zzws().currentTimeMillis());
        }
        if (zzaae < this.zzdyr) {
            return null;
        }
        if (zzaae > (this.zzdyr << 1)) {
            zzaac();
            return null;
        }
        String string = this.zzjdm.zzazl().getString(this.zzjdq, null);
        long j = this.zzjdm.zzazl().getLong(this.zzjdp, 0);
        zzaac();
        return (string == null || j <= 0) ? zzchx.zzjcp : new Pair(string, Long.valueOf(j));
    }

    public final void zzf(String str, long j) {
        this.zzjdm.zzve();
        if (zzaae() == 0) {
            zzaac();
        }
        if (str == null) {
            str = TtmlNode.ANONYMOUS_REGION_ID;
        }
        long j2 = this.zzjdm.zzazl().getLong(this.zzjdp, 0);
        if (j2 <= 0) {
            Editor edit = this.zzjdm.zzazl().edit();
            edit.putString(this.zzjdq, str);
            edit.putLong(this.zzjdp, 1);
            edit.apply();
            return;
        }
        Object obj = (this.zzjdm.zzawu().zzbaz().nextLong() & Long.MAX_VALUE) < Long.MAX_VALUE / (j2 + 1) ? 1 : null;
        Editor edit2 = this.zzjdm.zzazl().edit();
        if (obj != null) {
            edit2.putString(this.zzjdq, str);
        }
        edit2.putLong(this.zzjdp, j2 + 1);
        edit2.apply();
    }
}
