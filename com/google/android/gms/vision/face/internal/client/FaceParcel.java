package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.apps.common.proguard.UsedByNative;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

@UsedByNative("wrapper.cc")
public class FaceParcel extends zza {
    public static final Creator<FaceParcel> CREATOR = new zzb();
    public final float centerX;
    public final float centerY;
    public final float height;
    public final int id;
    private int versionCode;
    public final float width;
    public final float zzbNA;
    public final float zzbNB;
    public final LandmarkParcel[] zzbNC;
    public final float zzbND;
    public final float zzbNE;
    public final float zzbNF;

    public FaceParcel(int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, LandmarkParcel[] landmarkParcelArr, float f7, float f8, float f9) {
        this.versionCode = i;
        this.id = i2;
        this.centerX = f;
        this.centerY = f2;
        this.width = f3;
        this.height = f4;
        this.zzbNA = f5;
        this.zzbNB = f6;
        this.zzbNC = landmarkParcelArr;
        this.zzbND = f7;
        this.zzbNE = f8;
        this.zzbNF = f9;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.versionCode);
        zzd.zzc(parcel, 2, this.id);
        zzd.zza(parcel, 3, this.centerX);
        zzd.zza(parcel, 4, this.centerY);
        zzd.zza(parcel, 5, this.width);
        zzd.zza(parcel, 6, this.height);
        zzd.zza(parcel, 7, this.zzbNA);
        zzd.zza(parcel, 8, this.zzbNB);
        zzd.zza(parcel, 9, this.zzbNC, i, false);
        zzd.zza(parcel, 10, this.zzbND);
        zzd.zza(parcel, 11, this.zzbNE);
        zzd.zza(parcel, 12, this.zzbNF);
        zzd.zzI(parcel, zze);
    }
}
