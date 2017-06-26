package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fs extends zza {
    public static final Creator<fs> CREATOR = new ft();
    public final String zzbNQ;
    public final fd zzbNW;
    private fd zzbNX;
    public final String zzbNZ;
    private float zzbOa;
    private fn[] zzbOg;
    private boolean zzbOh;

    public fs(fn[] fnVarArr, fd fdVar, fd fdVar2, String str, float f, String str2, boolean z) {
        this.zzbOg = fnVarArr;
        this.zzbNW = fdVar;
        this.zzbNX = fdVar2;
        this.zzbNZ = str;
        this.zzbOa = f;
        this.zzbNQ = str2;
        this.zzbOh = z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOg, i, false);
        zzd.zza(parcel, 3, this.zzbNW, i, false);
        zzd.zza(parcel, 4, this.zzbNX, i, false);
        zzd.zza(parcel, 5, this.zzbNZ, false);
        zzd.zza(parcel, 6, this.zzbOa);
        zzd.zza(parcel, 7, this.zzbNQ, false);
        zzd.zza(parcel, 8, this.zzbOh);
        zzd.zzI(parcel, zze);
    }
}
