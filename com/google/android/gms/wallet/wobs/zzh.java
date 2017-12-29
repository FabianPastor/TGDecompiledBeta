package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzh implements Creator<LoyaltyPointsBalance> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        String str = null;
        int zzd = zzbfn.zzd(parcel);
        int i = 0;
        double d = 0.0d;
        long j = 0;
        int i2 = -1;
        String str2 = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    i = zzbfn.zzg(parcel, readInt);
                    break;
                case 3:
                    str2 = zzbfn.zzq(parcel, readInt);
                    break;
                case 4:
                    d = zzbfn.zzn(parcel, readInt);
                    break;
                case 5:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 6:
                    j = zzbfn.zzi(parcel, readInt);
                    break;
                case 7:
                    i2 = zzbfn.zzg(parcel, readInt);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LoyaltyPointsBalance(i, str2, d, str, j, i2);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LoyaltyPointsBalance[i];
    }
}
