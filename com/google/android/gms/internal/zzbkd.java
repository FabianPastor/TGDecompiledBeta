package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkd extends zza {
    public static final Creator<zzbkd> CREATOR = new zzbke();
    public final int height;
    public final int left;
    public final int top;
    public final int versionCode;
    public final int width;
    public final float zzbPy;

    public zzbkd(int i, int i2, int i3, int i4, int i5, float f) {
        this.versionCode = i;
        this.left = i2;
        this.top = i3;
        this.width = i4;
        this.height = i5;
        this.zzbPy = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbke.zza(this, parcel, i);
    }
}
