package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzb implements Creator<FaceParcel> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
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
            switch (65535 & readInt) {
                case 1:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    f = zzbfn.zzl(parcel, readInt);
                    break;
                case 4:
                    f2 = zzbfn.zzl(parcel, readInt);
                    break;
                case 5:
                    f3 = zzbfn.zzl(parcel, readInt);
                    break;
                case 6:
                    f4 = zzbfn.zzl(parcel, readInt);
                    break;
                case 7:
                    f5 = zzbfn.zzl(parcel, readInt);
                    break;
                case 8:
                    f6 = zzbfn.zzl(parcel, readInt);
                    break;
                case 9:
                    landmarkParcelArr = (LandmarkParcel[]) zzbfn.zzb(parcel, readInt, LandmarkParcel.CREATOR);
                    break;
                case 10:
                    f7 = zzbfn.zzl(parcel, readInt);
                    break;
                case 11:
                    f8 = zzbfn.zzl(parcel, readInt);
                    break;
                case 12:
                    f9 = zzbfn.zzl(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new FaceParcel(i, i2, f, f2, f3, f4, f5, f6, landmarkParcelArr, f7, f8, f9);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new FaceParcel[i];
    }
}
