package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;

public final class zzb implements Creator<FaceParcel> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = com.google.android.gms.common.internal.safeparcel.zzb.zzd(parcel);
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
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (SupportMenu.USER_MASK & readInt) {
                case 1:
                    i = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, readInt);
                    break;
                case 2:
                    i2 = com.google.android.gms.common.internal.safeparcel.zzb.zzg(parcel, readInt);
                    break;
                case 3:
                    f = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 4:
                    f2 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 5:
                    f3 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 6:
                    f4 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 7:
                    f5 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 8:
                    f6 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 9:
                    landmarkParcelArr = (LandmarkParcel[]) com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, readInt, LandmarkParcel.CREATOR);
                    break;
                case 10:
                    f7 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 11:
                    f8 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                case 12:
                    f9 = com.google.android.gms.common.internal.safeparcel.zzb.zzl(parcel, readInt);
                    break;
                default:
                    com.google.android.gms.common.internal.safeparcel.zzb.zzb(parcel, readInt);
                    break;
            }
        }
        com.google.android.gms.common.internal.safeparcel.zzb.zzF(parcel, zzd);
        return new FaceParcel(i, i2, f, f2, f3, f4, f5, f6, landmarkParcelArr, f7, f8, f9);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new FaceParcel[i];
    }
}
