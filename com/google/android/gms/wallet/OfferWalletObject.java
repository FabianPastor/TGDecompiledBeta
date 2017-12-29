package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wallet.wobs.CommonWalletObject;

public final class OfferWalletObject extends zzbfm {
    public static final Creator<OfferWalletObject> CREATOR = new zzab();
    private final int zzeck;
    CommonWalletObject zzlan;
    String zzlct;
    String zzwc;

    OfferWalletObject() {
        this.zzeck = 3;
    }

    OfferWalletObject(int i, String str, String str2, CommonWalletObject commonWalletObject) {
        this.zzeck = i;
        this.zzlct = str2;
        if (i < 3) {
            this.zzlan = CommonWalletObject.zzbkb().zznm(str).zzbkc();
        } else {
            this.zzlan = commonWalletObject;
        }
    }

    public final int getVersionCode() {
        return this.zzeck;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, getVersionCode());
        zzbfp.zza(parcel, 2, this.zzwc, false);
        zzbfp.zza(parcel, 3, this.zzlct, false);
        zzbfp.zza(parcel, 4, this.zzlan, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
