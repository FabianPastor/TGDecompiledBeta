package com.google.android.gms.vision.face.internal.client;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzi implements Creator<LandmarkParcel> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int i = 0;
        float f = 0.0f;
        int zzd = zzbfn.zzd(parcel);
        float f2 = 0.0f;
        int i2 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    f2 = zzbfn.zzl(parcel, readInt);
                    break;
                case 3:
                    f = zzbfn.zzl(parcel, readInt);
                    break;
                case 4:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LandmarkParcel(i2, f2, f, i);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LandmarkParcel[i];
    }
}
