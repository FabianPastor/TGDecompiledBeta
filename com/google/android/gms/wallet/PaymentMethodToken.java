package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class PaymentMethodToken extends zza {
    public static final Creator<PaymentMethodToken> CREATOR = new zzw();
    private int zzbPI;
    private String zzbPJ;

    private PaymentMethodToken() {
    }

    PaymentMethodToken(int i, String str) {
        this.zzbPI = i;
        this.zzbPJ = str;
    }

    public final int getPaymentMethodTokenizationType() {
        return this.zzbPI;
    }

    public final String getToken() {
        return this.zzbPJ;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbPI);
        zzd.zza(parcel, 3, this.zzbPJ, false);
        zzd.zzI(parcel, zze);
    }
}
