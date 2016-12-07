package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhg extends zza {
    public static final Creator<zzbhg> CREATOR = new zzbhh();
    public final int height;
    public final int left;
    public final int top;
    public final int versionCode;
    public final int width;
    public final float zzbNA;

    public zzbhg(int i, int i2, int i3, int i4, int i5, float f) {
        this.versionCode = i;
        this.left = i2;
        this.top = i3;
        this.width = i4;
        this.height = i5;
        this.zzbNA = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhh.zza(this, parcel, i);
    }
}
