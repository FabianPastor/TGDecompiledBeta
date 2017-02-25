package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class PaymentMethodToken extends zza {
    public static final Creator<PaymentMethodToken> CREATOR = new zzr();
    int zzbRn;
    String zzbxX;

    private PaymentMethodToken() {
    }

    PaymentMethodToken(int i, String str) {
        this.zzbRn = i;
        this.zzbxX = str;
    }

    public int getPaymentMethodTokenizationType() {
        return this.zzbRn;
    }

    public String getToken() {
        return this.zzbxX;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzr.zza(this, parcel, i);
    }
}
