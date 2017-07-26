package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

@Deprecated
public final class ProxyCard extends zza {
    public static final Creator<ProxyCard> CREATOR = new zzz();
    private String zzbPM;
    private String zzbPN;
    private int zzbPO;
    private int zzbPP;

    public ProxyCard(String str, String str2, int i, int i2) {
        this.zzbPM = str;
        this.zzbPN = str2;
        this.zzbPO = i;
        this.zzbPP = i2;
    }

    public final String getCvn() {
        return this.zzbPN;
    }

    public final int getExpirationMonth() {
        return this.zzbPO;
    }

    public final int getExpirationYear() {
        return this.zzbPP;
    }

    public final String getPan() {
        return this.zzbPM;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbPM, false);
        zzd.zza(parcel, 3, this.zzbPN, false);
        zzd.zzc(parcel, 4, this.zzbPO);
        zzd.zzc(parcel, 5, this.zzbPP);
        zzd.zzI(parcel, zze);
    }
}
