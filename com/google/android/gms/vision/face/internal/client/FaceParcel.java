package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

public class FaceParcel extends AbstractSafeParcelable {
    public static final zzb CREATOR = new zzb();
    public final float aLg;
    public final float aLh;
    public final LandmarkParcel[] aLi;
    public final float aLj;
    public final float aLk;
    public final float aLl;
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
        this.aLg = f5;
        this.aLh = f6;
        this.aLi = landmarkParcelArr;
        this.aLj = f7;
        this.aLk = f8;
        this.aLl = f9;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzb.zza(this, parcel, i);
    }
}
