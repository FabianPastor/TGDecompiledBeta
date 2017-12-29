package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzj implements Creator<zzi> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        byte b = (byte) 0;
        int zzd = zzbfn.zzd(parcel);
        String str = null;
        byte b2 = (byte) 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    b2 = zzbfn.zze(parcel, readInt);
                    break;
                case 3:
                    b = zzbfn.zze(parcel, readInt);
                    break;
                case 4:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new zzi(b2, b, str);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new zzi[i];
    }
}
