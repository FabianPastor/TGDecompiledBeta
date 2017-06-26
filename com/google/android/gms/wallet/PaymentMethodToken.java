package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class PaymentMethodToken extends zza {
    public static final Creator<PaymentMethodToken> CREATOR = new zzw();
    private int zzbPG;
    private String zzbPH;

    private PaymentMethodToken() {
    }

    PaymentMethodToken(int i, String str) {
        this.zzbPG = i;
        this.zzbPH = str;
    }

    public final int getPaymentMethodTokenizationType() {
        return this.zzbPG;
    }

    public final String getToken() {
        return this.zzbPH;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbPG);
        zzd.zza(parcel, 3, this.zzbPH, false);
        zzd.zzI(parcel, zze);
    }
}
