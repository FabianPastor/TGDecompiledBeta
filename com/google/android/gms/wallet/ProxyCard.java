package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

@Deprecated
public final class ProxyCard extends zzbfm {
    public static final Creator<ProxyCard> CREATOR = new zzak();
    private String zzldn;
    private String zzldo;
    private int zzldp;
    private int zzldq;

    public ProxyCard(String str, String str2, int i, int i2) {
        this.zzldn = str;
        this.zzldo = str2;
        this.zzldp = i;
        this.zzldq = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzldn, false);
        zzbfp.zza(parcel, 3, this.zzldo, false);
        zzbfp.zzc(parcel, 4, this.zzldp);
        zzbfp.zzc(parcel, 5, this.zzldq);
        zzbfp.zzai(parcel, zze);
    }
}
