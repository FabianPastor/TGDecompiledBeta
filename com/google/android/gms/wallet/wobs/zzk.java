package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzk implements Creator<TimeInterval> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        long j = 0;
        int zzd = zzbfn.zzd(parcel);
        long j2 = 0;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    j2 = zzbfn.zzi(parcel, readInt);
                    break;
                case 3:
                    j = zzbfn.zzi(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new TimeInterval(j2, j);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new TimeInterval[i];
    }
}
