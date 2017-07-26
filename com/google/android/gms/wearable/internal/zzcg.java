package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.List;

public final class zzcg extends zza {
    public static final Creator<zzcg> CREATOR = new zzch();
    public final int statusCode;
    public final List<zzaa> zzbSG;

    public zzcg(int i, List<zzaa> list) {
        this.statusCode = i;
        this.zzbSG = list;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.statusCode);
        zzd.zzc(parcel, 3, this.zzbSG, false);
        zzd.zzI(parcel, zze);
    }
}
