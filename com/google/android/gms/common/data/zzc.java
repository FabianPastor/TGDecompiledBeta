package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;

public abstract class zzc {
    protected int zzaCm;
    private int zzaCn;
    protected final DataHolder zzazI;

    public zzc(DataHolder dataHolder, int i) {
        this.zzazI = (DataHolder) zzac.zzw(dataHolder);
        zzcA(i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_common_data_zzc = (zzc) obj;
        return zzaa.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzaCm), Integer.valueOf(this.zzaCm)) && zzaa.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzaCn), Integer.valueOf(this.zzaCn)) && com_google_android_gms_common_data_zzc.zzazI == this.zzazI;
    }

    protected boolean getBoolean(String str) {
        return this.zzazI.zze(str, this.zzaCm, this.zzaCn);
    }

    protected byte[] getByteArray(String str) {
        return this.zzazI.zzg(str, this.zzaCm, this.zzaCn);
    }

    protected float getFloat(String str) {
        return this.zzazI.zzf(str, this.zzaCm, this.zzaCn);
    }

    protected int getInteger(String str) {
        return this.zzazI.zzc(str, this.zzaCm, this.zzaCn);
    }

    protected long getLong(String str) {
        return this.zzazI.zzb(str, this.zzaCm, this.zzaCn);
    }

    protected String getString(String str) {
        return this.zzazI.zzd(str, this.zzaCm, this.zzaCn);
    }

    public int hashCode() {
        return zzaa.hashCode(Integer.valueOf(this.zzaCm), Integer.valueOf(this.zzaCn), this.zzazI);
    }

    public boolean isDataValid() {
        return !this.zzazI.isClosed();
    }

    protected void zza(String str, CharArrayBuffer charArrayBuffer) {
        this.zzazI.zza(str, this.zzaCm, this.zzaCn, charArrayBuffer);
    }

    protected void zzcA(int i) {
        boolean z = i >= 0 && i < this.zzazI.getCount();
        zzac.zzar(z);
        this.zzaCm = i;
        this.zzaCn = this.zzazI.zzcC(this.zzaCm);
    }

    public boolean zzdj(String str) {
        return this.zzazI.zzdj(str);
    }

    protected Uri zzdk(String str) {
        return this.zzazI.zzh(str, this.zzaCm, this.zzaCn);
    }

    protected boolean zzdl(String str) {
        return this.zzazI.zzi(str, this.zzaCm, this.zzaCn);
    }

    protected int zzwB() {
        return this.zzaCm;
    }
}
