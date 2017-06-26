package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

@Deprecated
public final class ProxyCard extends zza {
    public static final Creator<ProxyCard> CREATOR = new zzz();
    private String zzbPK;
    private String zzbPL;
    private int zzbPM;
    private int zzbPN;

    public ProxyCard(String str, String str2, int i, int i2) {
        this.zzbPK = str;
        this.zzbPL = str2;
        this.zzbPM = i;
        this.zzbPN = i2;
    }

    public final String getCvn() {
        return this.zzbPL;
    }

    public final int getExpirationMonth() {
        return this.zzbPM;
    }

    public final int getExpirationYear() {
        return this.zzbPN;
    }

    public final String getPan() {
        return this.zzbPK;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPK, false);
        zzd.zza(parcel, 3, this.zzbPL, false);
        zzd.zzc(parcel, 4, this.zzbPM);
        zzd.zzc(parcel, 5, this.zzbPN);
        zzd.zzI(parcel, zze);
    }
}
