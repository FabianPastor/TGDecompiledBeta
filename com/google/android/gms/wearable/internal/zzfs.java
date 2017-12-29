package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzfs extends zzbfm {
    public static final Creator<zzfs> CREATOR = new zzft();
    private String label;
    private String packageName;
    private long zzllb;

    public zzfs(String str, String str2, long j) {
        this.packageName = str;
        this.label = str2;
        this.zzllb = j;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.packageName, false);
        zzbfp.zza(parcel, 3, this.label, false);
        zzbfp.zza(parcel, 4, this.zzllb);
        zzbfp.zzai(parcel, zze);
    }
}
