package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzz;

public abstract class zzc {
    protected int BU;
    private int BV;
    protected final DataHolder zy;

    public zzc(DataHolder dataHolder, int i) {
        this.zy = (DataHolder) zzaa.zzy(dataHolder);
        zzfy(i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_common_data_zzc = (zzc) obj;
        return zzz.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.BU), Integer.valueOf(this.BU)) && zzz.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.BV), Integer.valueOf(this.BV)) && com_google_android_gms_common_data_zzc.zy == this.zy;
    }

    protected boolean getBoolean(String str) {
        return this.zy.zze(str, this.BU, this.BV);
    }

    protected byte[] getByteArray(String str) {
        return this.zy.zzg(str, this.BU, this.BV);
    }

    protected float getFloat(String str) {
        return this.zy.zzf(str, this.BU, this.BV);
    }

    protected int getInteger(String str) {
        return this.zy.zzc(str, this.BU, this.BV);
    }

    protected long getLong(String str) {
        return this.zy.zzb(str, this.BU, this.BV);
    }

    protected String getString(String str) {
        return this.zy.zzd(str, this.BU, this.BV);
    }

    public int hashCode() {
        return zzz.hashCode(Integer.valueOf(this.BU), Integer.valueOf(this.BV), this.zy);
    }

    public boolean isDataValid() {
        return !this.zy.isClosed();
    }

    protected void zza(String str, CharArrayBuffer charArrayBuffer) {
        this.zy.zza(str, this.BU, this.BV, charArrayBuffer);
    }

    protected int zzaul() {
        return this.BU;
    }

    protected void zzfy(int i) {
        boolean z = i >= 0 && i < this.zy.getCount();
        zzaa.zzbs(z);
        this.BU = i;
        this.BV = this.zy.zzga(this.BU);
    }

    public boolean zzho(String str) {
        return this.zy.zzho(str);
    }

    protected Uri zzhp(String str) {
        return this.zy.zzh(str, this.BU, this.BV);
    }

    protected boolean zzhq(String str) {
        return this.zy.zzi(str, this.BU, this.BV);
    }
}
