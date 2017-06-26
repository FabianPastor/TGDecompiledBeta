package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class PaymentMethodTokenizationParameters extends zza {
    public static final Creator<PaymentMethodTokenizationParameters> CREATOR = new zzy();
    int zzbPG;
    Bundle zzbPI = new Bundle();

    public final class Builder {
        private /* synthetic */ PaymentMethodTokenizationParameters zzbPJ;

        private Builder(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            this.zzbPJ = paymentMethodTokenizationParameters;
        }

        public final Builder addParameter(String str, String str2) {
            zzbo.zzh(str, "Tokenization parameter name must not be empty");
            zzbo.zzh(str2, "Tokenization parameter value must not be empty");
            this.zzbPJ.zzbPI.putString(str, str2);
            return this;
        }

        public final PaymentMethodTokenizationParameters build() {
            return this.zzbPJ;
        }

        public final Builder setPaymentMethodTokenizationType(int i) {
            this.zzbPJ.zzbPG = i;
            return this;
        }
    }

    private PaymentMethodTokenizationParameters() {
    }

    PaymentMethodTokenizationParameters(int i, Bundle bundle) {
        this.zzbPG = i;
        this.zzbPI = bundle;
    }

    public static Builder newBuilder() {
        PaymentMethodTokenizationParameters paymentMethodTokenizationParameters = new PaymentMethodTokenizationParameters();
        paymentMethodTokenizationParameters.getClass();
        return new Builder();
    }

    public final Bundle getParameters() {
        return new Bundle(this.zzbPI);
    }

    public final int getPaymentMethodTokenizationType() {
        return this.zzbPG;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, this.zzbPG);
        zzd.zza(parcel, 3, this.zzbPI, false);
        zzd.zzI(parcel, zze);
    }
}
