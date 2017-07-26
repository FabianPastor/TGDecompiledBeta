package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzc extends zza {
    public static final Creator<zzc> CREATOR = new zzd();
    public int mode;
    public int zzbNG;
    public int zzbNH;
    public boolean zzbNI;
    public boolean zzbNJ;
    public float zzbNK;

    public zzc(int i, int i2, int i3, boolean z, boolean z2, float f) {
        this.mode = i;
        this.zzbNG = i2;
        this.zzbNH = i3;
        this.zzbNI = z;
        this.zzbNJ = z2;
        this.zzbNK = f;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.mode);
        zzd.zzc(parcel, 3, this.zzbNG);
        zzd.zzc(parcel, 4, this.zzbNH);
        zzd.zza(parcel, 5, this.zzbNI);
        zzd.zza(parcel, 6, this.zzbNJ);
        zzd.zza(parcel, 7, this.zzbNK);
        zzd.zzI(parcel, zze);
    }
}
