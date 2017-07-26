package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class fm extends zza {
    public static final Creator<fm> CREATOR = new fn();
    public final Rect zzbOh;

    public fm() {
        this.zzbOh = new Rect();
    }

    public fm(Rect rect) {
        this.zzbOh = rect;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOh, i, false);
        zzd.zzI(parcel, zze);
    }
}
