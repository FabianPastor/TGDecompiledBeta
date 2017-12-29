package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class LoyaltyPoints extends zzbfm {
    public static final Creator<LoyaltyPoints> CREATOR = new zzi();
    String label;
    TimeInterval zzlbr;
    LoyaltyPointsBalance zzlfv;

    LoyaltyPoints() {
    }

    LoyaltyPoints(String str, LoyaltyPointsBalance loyaltyPointsBalance, TimeInterval timeInterval) {
        this.label = str;
        this.zzlfv = loyaltyPointsBalance;
        this.zzlbr = timeInterval;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.label, false);
        zzbfp.zza(parcel, 3, this.zzlfv, i, false);
        zzbfp.zza(parcel, 5, this.zzlbr, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
