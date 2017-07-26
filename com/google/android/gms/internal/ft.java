package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class ft extends zza {
    public static final Creator<ft> CREATOR = new fu();
    public final String zzbNS;
    public final fe zzbNY;
    private fe zzbNZ;
    public final String zzbOb;
    private float zzbOc;
    private fo[] zzbOi;
    private boolean zzbOj;

    public ft(fo[] foVarArr, fe feVar, fe feVar2, String str, float f, String str2, boolean z) {
        this.zzbOi = foVarArr;
        this.zzbNY = feVar;
        this.zzbNZ = feVar2;
        this.zzbOb = str;
        this.zzbOc = f;
        this.zzbNS = str2;
        this.zzbOj = z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOi, i, false);
        zzd.zza(parcel, 3, this.zzbNY, i, false);
        zzd.zza(parcel, 4, this.zzbNZ, i, false);
        zzd.zza(parcel, 5, this.zzbOb, false);
        zzd.zza(parcel, 6, this.zzbOc);
        zzd.zza(parcel, 7, this.zzbNS, false);
        zzd.zza(parcel, 8, this.zzbOj);
        zzd.zzI(parcel, zze);
    }
}
