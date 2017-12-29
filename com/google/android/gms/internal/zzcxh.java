package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class zzcxh implements Creator<zzcxg> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int i = 0;
        int zzd = zzbfn.zzd(parcel);
        Intent intent = null;
        int i2 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 1:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                case 2:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    intent = (Intent) zzbfn.zza(parcel, readInt, Intent.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzcxg(i2, i, intent);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzcxg[i];
    }
}
