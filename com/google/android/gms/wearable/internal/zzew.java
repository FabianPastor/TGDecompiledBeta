package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.List;

public final class zzew extends zza {
    public static final Creator<zzew> CREATOR = new zzex();
    private int statusCode;
    private long zzbTc;
    private List<zzek> zzbTe;

    public zzew(int i, long j, List<zzek> list) {
        this.statusCode = i;
        this.zzbTc = j;
        this.zzbTe = list;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zza(parcel, 3, this.zzbTc);
        zzd.zzc(parcel, 4, this.zzbTe, false);
        zzd.zzI(parcel, zze);
    }
}
