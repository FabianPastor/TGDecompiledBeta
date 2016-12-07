package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class BoundingBoxParcel extends AbstractSafeParcelable {
    public static final Creator<BoundingBoxParcel> CREATOR = new zza();
    public final float aOP;
    public final int height;
    public final int left;
    public final int top;
    public final int versionCode;
    public final int width;

    public BoundingBoxParcel(int i, int i2, int i3, int i4, int i5, float f) {
        this.versionCode = i;
        this.left = i2;
        this.top = i3;
        this.width = i4;
        this.height = i5;
        this.aOP = f;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
