package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbkj extends zza {
    public static final Creator<zzbkj> CREATOR = new zzbkk();
    final int versionCode;
    public final Rect zzbPJ;

    public zzbkj() {
        this.versionCode = 1;
        this.zzbPJ = new Rect();
    }

    public zzbkj(int i, Rect rect) {
        this.versionCode = i;
        this.zzbPJ = rect;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbkk.zza(this, parcel, i);
    }
}
