package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class LoyaltyPointsBalance extends zzbfm {
    public static final Creator<LoyaltyPointsBalance> CREATOR = new zzh();
    String zzlar;
    int zzlfx;
    String zzlfy;
    double zzlfz;
    long zzlga;
    int zzlgb;

    LoyaltyPointsBalance() {
        this.zzlgb = -1;
        this.zzlfx = -1;
        this.zzlfz = -1.0d;
    }

    LoyaltyPointsBalance(int i, String str, double d, String str2, long j, int i2) {
        this.zzlfx = i;
        this.zzlfy = str;
        this.zzlfz = d;
        this.zzlar = str2;
        this.zzlga = j;
        this.zzlgb = i2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.zzlfx);
        zzbfp.zza(parcel, 3, this.zzlfy, false);
        zzbfp.zza(parcel, 4, this.zzlfz);
        zzbfp.zza(parcel, 5, this.zzlar, false);
        zzbfp.zza(parcel, 6, this.zzlga);
        zzbfp.zzc(parcel, 7, this.zzlgb);
        zzbfp.zzai(parcel, zze);
    }
}
