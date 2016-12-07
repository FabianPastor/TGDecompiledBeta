package com.google.android.gms.internal;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzbhm extends zza {
    public static final Creator<zzbhm> CREATOR = new zzbhn();
    final int versionCode;
    public final Rect zzbNL;

    public zzbhm() {
        this.versionCode = 1;
        this.zzbNL = new Rect();
    }

    public zzbhm(int i, Rect rect) {
        this.versionCode = i;
        this.zzbNL = rect;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbhn.zza(this, parcel, i);
    }
}
