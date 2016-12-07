package com.google.android.gms.vision.text.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class LineBoxParcel extends AbstractSafeParcelable {
    public static final Creator<LineBoxParcel> CREATOR = new zzd();
    public final String aOK;
    public final WordBoxParcel[] aOQ;
    public final BoundingBoxParcel aOR;
    public final BoundingBoxParcel aOS;
    public final BoundingBoxParcel aOT;
    public final String aOU;
    public final float aOV;
    public final int aOW;
    public final boolean aOX;
    public final int aOY;
    public final int aOZ;
    public final int versionCode;

    public LineBoxParcel(int i, WordBoxParcel[] wordBoxParcelArr, BoundingBoxParcel boundingBoxParcel, BoundingBoxParcel boundingBoxParcel2, BoundingBoxParcel boundingBoxParcel3, String str, float f, String str2, int i2, boolean z, int i3, int i4) {
        this.versionCode = i;
        this.aOQ = wordBoxParcelArr;
        this.aOR = boundingBoxParcel;
        this.aOS = boundingBoxParcel2;
        this.aOT = boundingBoxParcel3;
        this.aOU = str;
        this.aOV = f;
        this.aOK = str2;
        this.aOW = i2;
        this.aOX = z;
        this.aOY = i3;
        this.aOZ = i4;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }
}
