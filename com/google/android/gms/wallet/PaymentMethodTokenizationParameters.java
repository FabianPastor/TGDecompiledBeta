package com.google.android.gms.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class PaymentMethodTokenizationParameters extends zza {
    public static final Creator<PaymentMethodTokenizationParameters> CREATOR = new zzs();
    int zzbRn;
    Bundle zzbRo = new Bundle();

    public final class Builder {
        final /* synthetic */ PaymentMethodTokenizationParameters zzbRp;

        private Builder(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            this.zzbRp = paymentMethodTokenizationParameters;
        }

        public Builder addParameter(String str, String str2) {
            zzac.zzh(str, "Tokenization parameter name must not be empty");
            zzac.zzh(str2, "Tokenization parameter value must not be empty");
            this.zzbRp.zzbRo.putString(str, str2);
            return this;
        }

        public PaymentMethodTokenizationParameters build() {
            return this.zzbRp;
        }

        public Builder setPaymentMethodTokenizationType(int i) {
            this.zzbRp.zzbRn = i;
            return this;
        }
    }

    private PaymentMethodTokenizationParameters() {
    }

    PaymentMethodTokenizationParameters(int i, Bundle bundle) {
        this.zzbRn = i;
        this.zzbRo = bundle;
    }

    public static Builder newBuilder() {
        PaymentMethodTokenizationParameters paymentMethodTokenizationParameters = new PaymentMethodTokenizationParameters();
        paymentMethodTokenizationParameters.getClass();
        return new Builder();
    }

    public Bundle getParameters() {
        return new Bundle(this.zzbRo);
    }

    public int getPaymentMethodTokenizationType() {
        return this.zzbRn;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzs.zza(this, parcel, i);
    }
}
