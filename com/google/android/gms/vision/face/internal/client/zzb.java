package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzb implements Creator<FaceParcel> {
    static void zza(FaceParcel faceParcel, Parcel parcel, int i) {
        int zzcr = com.google.android.gms.common.internal.safeparcel.zzb.zzcr(parcel);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 1, faceParcel.versionCode);
        com.google.android.gms.common.internal.safeparcel.zzb.zzc(parcel, 2, faceParcel.id);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 3, faceParcel.centerX);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 4, faceParcel.centerY);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 5, faceParcel.width);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 6, faceParcel.height);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 7, faceParcel.aLg);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 8, faceParcel.aLh);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 9, faceParcel.aLi, i, false);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 10, faceParcel.aLj);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 11, faceParcel.aLk);
        com.google.android.gms.common.internal.safeparcel.zzb.zza(parcel, 12, faceParcel.aLl);
        com.google.android.gms.common.internal.safeparcel.zzb.zzaj(parcel, zzcr);
    }

    public /* synthetic */ Object createFromParcel(Parcel parcel) {
        return zztc(parcel);
    }

    public /* synthetic */ Object[] newArray(int i) {
        return zzabs(i);
    }

    public FaceParcel[] zzabs(int i) {
        return new FaceParcel[i];
    }

    public FaceParcel zztc(Parcel parcel) {
        int zzcq = zza.zzcq(parcel);
        int i = 0;
        int i2 = 0;
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        float f5 = 0.0f;
        float f6 = 0.0f;
        LandmarkParcel[] landmarkParcelArr = null;
        float f7 = 0.0f;
        float f8 = 0.0f;
        float f9 = 0.0f;
        while (parcel.dataPosition() < zzcq) {
            int zzcp = zza.zzcp(parcel);
            switch (zza.zzgv(zzcp)) {
                case 1:
                    i = zza.zzg(parcel, zzcp);
                    break;
                case 2:
                    i2 = zza.zzg(parcel, zzcp);
                    break;
                case 3:
                    f = zza.zzl(parcel, zzcp);
                    break;
                case 4:
                    f2 = zza.zzl(parcel, zzcp);
                    break;
                case 5:
                    f3 = zza.zzl(parcel, zzcp);
                    break;
                case 6:
                    f4 = zza.zzl(parcel, zzcp);
                    break;
                case 7:
                    f5 = zza.zzl(parcel, zzcp);
                    break;
                case 8:
                    f6 = zza.zzl(parcel, zzcp);
                    break;
                case 9:
                    landmarkParcelArr = (LandmarkParcel[]) zza.zzb(parcel, zzcp, LandmarkParcel.CREATOR);
                    break;
                case 10:
                    f7 = zza.zzl(parcel, zzcp);
                    break;
                case 11:
                    f8 = zza.zzl(parcel, zzcp);
                    break;
                case 12:
                    f9 = zza.zzl(parcel, zzcp);
                    break;
                default:
                    zza.zzb(parcel, zzcp);
                    break;
            }
        }
        if (parcel.dataPosition() == zzcq) {
            return new FaceParcel(i, i2, f, f2, f3, f4, f5, f6, landmarkParcelArr, f7, f8, f9);
        }
        throw new zza.zza("Overread allowed size end=" + zzcq, parcel);
    }
}
