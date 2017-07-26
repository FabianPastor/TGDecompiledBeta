package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fk extends zza {
    public static final Creator<fk> CREATOR = new fl();
    public final String zzbNS;
    public final ft[] zzbNX;
    public final fe zzbNY;
    private fe zzbNZ;
    private fe zzbOa;
    public final String zzbOb;
    private float zzbOc;
    private int zzbOd;
    public final boolean zzbOe;
    public final int zzbOf;
    public final int zzbOg;

    public fk(ft[] ftVarArr, fe feVar, fe feVar2, fe feVar3, String str, float f, String str2, int i, boolean z, int i2, int i3) {
        this.zzbNX = ftVarArr;
        this.zzbNY = feVar;
        this.zzbNZ = feVar2;
        this.zzbOa = feVar3;
        this.zzbOb = str;
        this.zzbOc = f;
        this.zzbNS = str2;
        this.zzbOd = i;
        this.zzbOe = z;
        this.zzbOf = i2;
        this.zzbOg = i3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbNX, i, false);
        zzd.zza(parcel, 3, this.zzbNY, i, false);
        zzd.zza(parcel, 4, this.zzbNZ, i, false);
        zzd.zza(parcel, 5, this.zzbOa, i, false);
        zzd.zza(parcel, 6, this.zzbOb, false);
        zzd.zza(parcel, 7, this.zzbOc);
        zzd.zza(parcel, 8, this.zzbNS, false);
        zzd.zzc(parcel, 9, this.zzbOd);
        zzd.zza(parcel, 10, this.zzbOe);
        zzd.zzc(parcel, 11, this.zzbOf);
        zzd.zzc(parcel, 12, this.zzbOg);
        zzd.zzI(parcel, zze);
    }
}
