package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceParcel extends AbstractSafeParcelable {
    public static final Creator<FaceParcel> CREATOR = new zzb();
    public final float aOr;
    public final float aOs;
    public final LandmarkParcel[] aOt;
    public final float aOu;
    public final float aOv;
    public final float aOw;
    public final float centerX;
    public final float centerY;
    public final float height;
    public final int id;
    public final int versionCode;
    public final float width;

    public FaceParcel(int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, LandmarkParcel[] landmarkParcelArr, float f7, float f8, float f9) {
        this.versionCode = i;
        this.id = i2;
        this.centerX = f;
        this.centerY = f2;
        this.width = f3;
        this.height = f4;
        this.aOr = f5;
        this.aOs = f6;
        this.aOt = landmarkParcelArr;
        this.aOu = f7;
        this.aOv = f8;
        this.aOw = f9;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
