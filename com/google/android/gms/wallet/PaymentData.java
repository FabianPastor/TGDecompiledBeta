package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class PaymentData extends zzbfm {
    public static final Creator<PaymentData> CREATOR = new zzac();
    private String zzegs;
    private CardInfo zzlcv;
    private UserAddress zzlcw;
    private PaymentMethodToken zzlcx;
    private String zzlcy;

    private PaymentData() {
    }

    PaymentData(String str, CardInfo cardInfo, UserAddress userAddress, PaymentMethodToken paymentMethodToken, String str2) {
        this.zzegs = str;
        this.zzlcv = cardInfo;
        this.zzlcw = userAddress;
        this.zzlcx = paymentMethodToken;
        this.zzlcy = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 1, this.zzegs, false);
        zzbfp.zza(parcel, 2, this.zzlcv, i, false);
        zzbfp.zza(parcel, 3, this.zzlcw, i, false);
        zzbfp.zza(parcel, 4, this.zzlcx, i, false);
        zzbfp.zza(parcel, 5, this.zzlcy, false);
        zzbfp.zzai(parcel, zze);
    }
}
