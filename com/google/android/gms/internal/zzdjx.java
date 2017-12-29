package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzdjx implements Creator<zzdjw> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int i = 0;
        int zzd = zzbfn.zzd(parcel);
        long j = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i4 = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    i3 = zzbfn.zzg(parcel, readInt);
                    break;
                case 4:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 5:
                    j = zzbfn.zzi(parcel, readInt);
                    break;
                case 6:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzdjw(i4, i3, i2, j, i);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzdjw[i];
    }
}
