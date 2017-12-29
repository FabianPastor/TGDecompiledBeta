package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class PaymentMethodToken extends zzbfm {
    public static final Creator<PaymentMethodToken> CREATOR = new zzaf();
    private int zzldi;
    private String zzldj;

    private PaymentMethodToken() {
    }

    PaymentMethodToken(int i, String str) {
        this.zzldi = i;
        this.zzldj = str;
    }

    public final String getToken() {
        return this.zzldj;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.zzldi);
        zzbfp.zza(parcel, 3, this.zzldj, false);
        zzbfp.zzai(parcel, zze);
    }
}
