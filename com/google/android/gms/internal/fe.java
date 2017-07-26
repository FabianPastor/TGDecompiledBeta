package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fe extends zza {
    public static final Creator<fe> CREATOR = new ff();
    public final int height;
    public final int left;
    public final int top;
    public final int width;
    public final float zzbNW;

    public fe(int i, int i2, int i3, int i4, float f) {
        this.left = i;
        this.top = i2;
        this.width = i3;
        this.height = i4;
        this.zzbNW = f;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.left);
        zzd.zzc(parcel, 3, this.top);
        zzd.zzc(parcel, 4, this.width);
        zzd.zzc(parcel, 5, this.height);
        zzd.zza(parcel, 6, this.zzbNW);
        zzd.zzI(parcel, zze);
    }
}
