package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fl extends zza {
    public static final Creator<fl> CREATOR = new fm();
    public final Rect zzbOf;

    public fl() {
        this.zzbOf = new Rect();
    }

    public fl(Rect rect) {
        this.zzbOf = rect;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOf, i, false);
        zzd.zzI(parcel, zze);
    }
}
