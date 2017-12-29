package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.apps.common.proguard.UsedByNative;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

@UsedByNative("wrapper.cc")
public class FaceParcel extends zzbfm {
    public static final Creator<FaceParcel> CREATOR = new zzb();
    public final float centerX;
    public final float centerY;
    public final float height;
    public final int id;
    private int versionCode;
    public final float width;
    public final float zzkxh;
    public final float zzkxi;
    public final LandmarkParcel[] zzkxj;
    public final float zzkxk;
    public final float zzkxl;
    public final float zzkxm;

    public FaceParcel(int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, LandmarkParcel[] landmarkParcelArr, float f7, float f8, float f9) {
        this.versionCode = i;
        this.id = i2;
        this.centerX = f;
        this.centerY = f2;
        this.width = f3;
        this.height = f4;
        this.zzkxh = f5;
        this.zzkxi = f6;
        this.zzkxj = landmarkParcelArr;
        this.zzkxk = f7;
        this.zzkxl = f8;
        this.zzkxm = f9;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.versionCode);
        zzbfp.zzc(parcel, 2, this.id);
        zzbfp.zza(parcel, 3, this.centerX);
        zzbfp.zza(parcel, 4, this.centerY);
        zzbfp.zza(parcel, 5, this.width);
        zzbfp.zza(parcel, 6, this.height);
        zzbfp.zza(parcel, 7, this.zzkxh);
        zzbfp.zza(parcel, 8, this.zzkxi);
        zzbfp.zza(parcel, 9, this.zzkxj, i, false);
        zzbfp.zza(parcel, 10, this.zzkxk);
        zzbfp.zza(parcel, 11, this.zzkxl);
        zzbfp.zza(parcel, 12, this.zzkxm);
        zzbfp.zzai(parcel, zze);
    }
}
