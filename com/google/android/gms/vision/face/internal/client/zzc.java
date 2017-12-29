package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzc extends zzbfm {
    public static final Creator<zzc> CREATOR = new zzd();
    public int mode;
    public int zzkxn;
    public int zzkxo;
    public boolean zzkxp;
    public boolean zzkxq;
    public float zzkxr;

    public zzc(int i, int i2, int i3, boolean z, boolean z2, float f) {
        this.mode = i;
        this.zzkxn = i2;
        this.zzkxo = i3;
        this.zzkxp = z;
        this.zzkxq = z2;
        this.zzkxr = f;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.mode);
        zzbfp.zzc(parcel, 3, this.zzkxn);
        zzbfp.zzc(parcel, 4, this.zzkxo);
        zzbfp.zza(parcel, 5, this.zzkxp);
        zzbfp.zza(parcel, 6, this.zzkxq);
        zzbfp.zza(parcel, 7, this.zzkxr);
        zzbfp.zzai(parcel, zze);
    }
}
