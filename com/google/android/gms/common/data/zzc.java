package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbq;
import java.util.Arrays;

public class zzc {
    protected final DataHolder zzfqt;
    protected int zzfvx;
    private int zzfvy;

    public zzc(DataHolder dataHolder, int i) {
        this.zzfqt = (DataHolder) zzbq.checkNotNull(dataHolder);
        zzbx(i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_common_data_zzc = (zzc) obj;
        return zzbg.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzfvx), Integer.valueOf(this.zzfvx)) && zzbg.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzfvy), Integer.valueOf(this.zzfvy)) && com_google_android_gms_common_data_zzc.zzfqt == this.zzfqt;
    }

    protected final byte[] getByteArray(String str) {
        return this.zzfqt.zzg(str, this.zzfvx, this.zzfvy);
    }

    protected final int getInteger(String str) {
        return this.zzfqt.zzc(str, this.zzfvx, this.zzfvy);
    }

    protected final String getString(String str) {
        return this.zzfqt.zzd(str, this.zzfvx, this.zzfvy);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.zzfvx), Integer.valueOf(this.zzfvy), this.zzfqt});
    }

    protected final void zzbx(int i) {
        boolean z = i >= 0 && i < this.zzfqt.zzfwg;
        zzbq.checkState(z);
        this.zzfvx = i;
        this.zzfvy = this.zzfqt.zzbz(this.zzfvx);
    }
}
