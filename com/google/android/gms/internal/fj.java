package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fj extends zza {
    public static final Creator<fj> CREATOR = new fk();
    public final String zzbNQ;
    public final fs[] zzbNV;
    public final fd zzbNW;
    private fd zzbNX;
    private fd zzbNY;
    public final String zzbNZ;
    private float zzbOa;
    private int zzbOb;
    public final boolean zzbOc;
    public final int zzbOd;
    public final int zzbOe;

    public fj(fs[] fsVarArr, fd fdVar, fd fdVar2, fd fdVar3, String str, float f, String str2, int i, boolean z, int i2, int i3) {
        this.zzbNV = fsVarArr;
        this.zzbNW = fdVar;
        this.zzbNX = fdVar2;
        this.zzbNY = fdVar3;
        this.zzbNZ = str;
        this.zzbOa = f;
        this.zzbNQ = str2;
        this.zzbOb = i;
        this.zzbOc = z;
        this.zzbOd = i2;
        this.zzbOe = i3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbNV, i, false);
        zzd.zza(parcel, 3, this.zzbNW, i, false);
        zzd.zza(parcel, 4, this.zzbNX, i, false);
        zzd.zza(parcel, 5, this.zzbNY, i, false);
        zzd.zza(parcel, 6, this.zzbNZ, false);
        zzd.zza(parcel, 7, this.zzbOa);
        zzd.zza(parcel, 8, this.zzbNQ, false);
        zzd.zzc(parcel, 9, this.zzbOb);
        zzd.zza(parcel, 10, this.zzbOc);
        zzd.zzc(parcel, 11, this.zzbOd);
        zzd.zzc(parcel, 12, this.zzbOe);
        zzd.zzI(parcel, zze);
    }
}
