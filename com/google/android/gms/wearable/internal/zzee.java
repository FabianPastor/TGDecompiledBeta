package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzee extends zzbfm {
    public static final Creator<zzee> CREATOR = new zzef();
    public final int statusCode;
    public final ParcelFileDescriptor zzjnk;

    public zzee(int i, ParcelFileDescriptor parcelFileDescriptor) {
        this.statusCode = i;
        this.zzjnk = parcelFileDescriptor;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int i2 = i | 1;
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzjnk, i2, false);
        zzbfp.zzai(parcel, zze);
    }
}
