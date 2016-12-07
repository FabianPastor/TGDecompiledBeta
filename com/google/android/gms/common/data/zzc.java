package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzac;

public abstract class zzc {
    protected final DataHolder xi;
    protected int zK;
    private int zL;

    public zzc(DataHolder dataHolder, int i) {
        this.xi = (DataHolder) zzac.zzy(dataHolder);
        zzfz(i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_common_data_zzc = (zzc) obj;
        return zzab.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zK), Integer.valueOf(this.zK)) && zzab.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zL), Integer.valueOf(this.zL)) && com_google_android_gms_common_data_zzc.xi == this.xi;
    }

    protected boolean getBoolean(String str) {
        return this.xi.zze(str, this.zK, this.zL);
    }

    protected byte[] getByteArray(String str) {
        return this.xi.zzg(str, this.zK, this.zL);
    }

    protected float getFloat(String str) {
        return this.xi.zzf(str, this.zK, this.zL);
    }

    protected int getInteger(String str) {
        return this.xi.zzc(str, this.zK, this.zL);
    }

    protected long getLong(String str) {
        return this.xi.zzb(str, this.zK, this.zL);
    }

    protected String getString(String str) {
        return this.xi.zzd(str, this.zK, this.zL);
    }

    public int hashCode() {
        return zzab.hashCode(Integer.valueOf(this.zK), Integer.valueOf(this.zL), this.xi);
    }

    public boolean isDataValid() {
        return !this.xi.isClosed();
    }

    protected void zza(String str, CharArrayBuffer charArrayBuffer) {
        this.xi.zza(str, this.zK, this.zL, charArrayBuffer);
    }

    protected int zzatc() {
        return this.zK;
    }

    protected void zzfz(int i) {
        boolean z = i >= 0 && i < this.xi.getCount();
        zzac.zzbr(z);
        this.zK = i;
        this.zL = this.xi.zzgb(this.zK);
    }

    public boolean zzhm(String str) {
        return this.xi.zzhm(str);
    }

    protected Uri zzhn(String str) {
        return this.xi.zzh(str, this.zK, this.zL);
    }

    protected boolean zzho(String str) {
        return this.xi.zzi(str, this.zK, this.zL);
    }
}
