package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfn;

public final class zzi implements Creator<LoyaltyPoints> {
    public final /* synthetic */ Object createFromParcel(Parcel parcel) {
        int zzd = zzbfn.zzd(parcel);
        TimeInterval timeInterval = null;
        LoyaltyPointsBalance loyaltyPointsBalance = null;
        String str = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            switch (65535 & readInt) {
                case 2:
                    str = zzbfn.zzq(parcel, readInt);
                    break;
                case 3:
                    loyaltyPointsBalance = (LoyaltyPointsBalance) zzbfn.zza(parcel, readInt, LoyaltyPointsBalance.CREATOR);
                    break;
                case 5:
                    timeInterval = (TimeInterval) zzbfn.zza(parcel, readInt, TimeInterval.CREATOR);
                    break;
                default:
                    zzbfn.zzb(parcel, readInt);
                    break;
            }
        }
        zzbfn.zzaf(parcel, zzd);
        return new LoyaltyPoints(str, loyaltyPointsBalance, timeInterval);
    }

    public final /* synthetic */ Object[] newArray(int i) {
        return new LoyaltyPoints[i];
    }
}
