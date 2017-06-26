package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzc extends zza {
    public static final Creator<zzc> CREATOR = new zzd();
    public int mode;
    public int zzbNE;
    public int zzbNF;
    public boolean zzbNG;
    public boolean zzbNH;
    public float zzbNI;

    public zzc(int i, int i2, int i3, boolean z, boolean z2, float f) {
        this.mode = i;
        this.zzbNE = i2;
        this.zzbNF = i3;
        this.zzbNG = z;
        this.zzbNH = z2;
        this.zzbNI = f;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.mode);
        zzd.zzc(parcel, 3, this.zzbNE);
        zzd.zzc(parcel, 4, this.zzbNF);
        zzd.zza(parcel, 5, this.zzbNG);
        zzd.zza(parcel, 6, this.zzbNH);
        zzd.zza(parcel, 7, this.zzbNI);
        zzd.zzI(parcel, zze);
    }
}
