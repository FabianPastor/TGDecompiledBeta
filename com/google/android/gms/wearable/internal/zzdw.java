package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.ConnectionConfiguration;

@Deprecated
public final class zzdw extends zzbfm {
    public static final Creator<zzdw> CREATOR = new zzdx();
    private int statusCode;
    private ConnectionConfiguration zzlkk;

    public zzdw(int i, ConnectionConfiguration connectionConfiguration) {
        this.statusCode = i;
        this.zzlkk = connectionConfiguration;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlkk, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
