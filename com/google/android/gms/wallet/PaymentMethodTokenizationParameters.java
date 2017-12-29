package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class PaymentMethodTokenizationParameters extends zzbfm {
    public static final Creator<PaymentMethodTokenizationParameters> CREATOR = new zzah();
    Bundle zzeac = new Bundle();
    int zzldi;

    public final class Builder {
        private /* synthetic */ PaymentMethodTokenizationParameters zzldk;

        private Builder(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            this.zzldk = paymentMethodTokenizationParameters;
        }

        public final Builder addParameter(String str, String str2) {
            zzbq.zzh(str, "Tokenization parameter name must not be empty");
            zzbq.zzh(str2, "Tokenization parameter value must not be empty");
            this.zzldk.zzeac.putString(str, str2);
            return this;
        }

        public final PaymentMethodTokenizationParameters build() {
            return this.zzldk;
        }

        public final Builder setPaymentMethodTokenizationType(int i) {
            this.zzldk.zzldi = i;
            return this;
        }
    }

    private PaymentMethodTokenizationParameters() {
    }

    PaymentMethodTokenizationParameters(int i, Bundle bundle) {
        this.zzldi = i;
        this.zzeac = bundle;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.zzldi);
        zzbfp.zza(parcel, 3, this.zzeac, false);
        zzbfp.zzai(parcel, zze);
    }
}
