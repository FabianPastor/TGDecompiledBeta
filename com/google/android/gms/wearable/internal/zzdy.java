package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.ConnectionConfiguration;

public final class zzdy extends zzbfm {
    public static final Creator<zzdy> CREATOR = new zzdz();
    private int statusCode;
    private ConnectionConfiguration[] zzlkl;

    public zzdy(int i, ConnectionConfiguration[] connectionConfigurationArr) {
        this.statusCode = i;
        this.zzlkl = connectionConfigurationArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlkl, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
