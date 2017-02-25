package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class zzbjx extends zza {
    public static final Creator<zzbjx> CREATOR = new zzbjy();
    public final int type;
    public final int versionCode;
    public final float x;
    public final float y;

    public zzbjx(int i, float f, float f2, int i2) {
        this.versionCode = i;
        this.x = f;
        this.y = f2;
        this.type = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbjy.zza(this, parcel, i);
    }
}
