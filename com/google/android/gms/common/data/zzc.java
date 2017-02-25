package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;

public abstract class zzc {
    protected final DataHolder zzaBi;
    protected int zzaDL;
    private int zzaDM;

    public zzc(DataHolder dataHolder, int i) {
        this.zzaBi = (DataHolder) zzac.zzw(dataHolder);
        zzcG(i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_common_data_zzc = (zzc) obj;
        return zzaa.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzaDL), Integer.valueOf(this.zzaDL)) && zzaa.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzaDM), Integer.valueOf(this.zzaDM)) && com_google_android_gms_common_data_zzc.zzaBi == this.zzaBi;
    }

    protected boolean getBoolean(String str) {
        return this.zzaBi.zze(str, this.zzaDL, this.zzaDM);
    }

    protected byte[] getByteArray(String str) {
        return this.zzaBi.zzg(str, this.zzaDL, this.zzaDM);
    }

    protected float getFloat(String str) {
        return this.zzaBi.zzf(str, this.zzaDL, this.zzaDM);
    }

    protected int getInteger(String str) {
        return this.zzaBi.zzc(str, this.zzaDL, this.zzaDM);
    }

    protected long getLong(String str) {
        return this.zzaBi.zzb(str, this.zzaDL, this.zzaDM);
    }

    protected String getString(String str) {
        return this.zzaBi.zzd(str, this.zzaDL, this.zzaDM);
    }

    public int hashCode() {
        return zzaa.hashCode(Integer.valueOf(this.zzaDL), Integer.valueOf(this.zzaDM), this.zzaBi);
    }

    public boolean isDataValid() {
        return !this.zzaBi.isClosed();
    }

    protected void zza(String str, CharArrayBuffer charArrayBuffer) {
        this.zzaBi.zza(str, this.zzaDL, this.zzaDM, charArrayBuffer);
    }

    protected void zzcG(int i) {
        boolean z = i >= 0 && i < this.zzaBi.getCount();
        zzac.zzaw(z);
        this.zzaDL = i;
        this.zzaDM = this.zzaBi.zzcI(this.zzaDL);
    }

    public boolean zzdf(String str) {
        return this.zzaBi.zzdf(str);
    }

    protected Uri zzdg(String str) {
        return this.zzaBi.zzh(str, this.zzaDL, this.zzaDM);
    }

    protected boolean zzdh(String str) {
        return this.zzaBi.zzi(str, this.zzaDL, this.zzaDM);
    }

    protected int zzxi() {
        return this.zzaDL;
    }
}
