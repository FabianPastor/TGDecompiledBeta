package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzdo extends zzbfm {
    public static final Creator<zzdo> CREATOR = new zzdp();
    public final int statusCode;
    public final ParcelFileDescriptor zzlkg;

    public zzdo(int i, ParcelFileDescriptor parcelFileDescriptor) {
        this.statusCode = i;
        this.zzlkg = parcelFileDescriptor;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzlkg, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
