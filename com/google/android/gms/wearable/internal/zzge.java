package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.List;

public final class zzge extends zzbfm {
    public static final Creator<zzge> CREATOR = new zzgf();
    private int statusCode;
    private long zzllb;
    private List<zzfs> zzlld;

    public zzge(int i, long j, List<zzfs> list) {
        this.statusCode = i;
        this.zzllb = j;
        this.zzlld = list;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zza(parcel, 3, this.zzllb);
        zzbfp.zzc(parcel, 4, this.zzlld, false);
        zzbfp.zzai(parcel, zze);
    }
}
