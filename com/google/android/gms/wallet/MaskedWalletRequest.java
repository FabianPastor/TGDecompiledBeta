package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.identity.intents.model.CountrySpecification;
import java.util.ArrayList;
import java.util.Collection;

public final class MaskedWalletRequest extends zza implements ReflectedParcelable {
    public static final Creator<MaskedWalletRequest> CREATOR = new zzo();
    String zzUI;
    String zzbPQ;
    String zzbPX;
    boolean zzbQW;
    boolean zzbQX;
    boolean zzbQY;
    String zzbQZ;
    Cart zzbQh;
    String zzbRa;
    boolean zzbRb;
    boolean zzbRc;
    CountrySpecification[] zzbRd;
    boolean zzbRe;
    boolean zzbRf;
    ArrayList<CountrySpecification> zzbRg;
    PaymentMethodTokenizationParameters zzbRh;
    ArrayList<Integer> zzbRi;

    public final class Builder {
        final /* synthetic */ MaskedWalletRequest zzbRj;

        private Builder(MaskedWalletRequest maskedWalletRequest) {
            this.zzbRj = maskedWalletRequest;
        }

        public Builder addAllowedCardNetwork(int i) {
            if (this.zzbRj.zzbRi == null) {
                this.zzbRj.zzbRi = new ArrayList();
            }
            this.zzbRj.zzbRi.add(Integer.valueOf(i));
            return this;
        }

        public Builder addAllowedCardNetworks(Collection<Integer> collection) {
            if (collection != null) {
                if (this.zzbRj.zzbRi == null) {
                    this.zzbRj.zzbRi = new ArrayList();
                }
                this.zzbRj.zzbRi.addAll(collection);
            }
            return this;
        }

        public Builder addAllowedCountrySpecificationForShipping(CountrySpecification countrySpecification) {
            if (this.zzbRj.zzbRg == null) {
                this.zzbRj.zzbRg = new ArrayList();
            }
            this.zzbRj.zzbRg.add(countrySpecification);
            return this;
        }

        public Builder addAllowedCountrySpecificationsForShipping(Collection<CountrySpecification> collection) {
            if (collection != null) {
                if (this.zzbRj.zzbRg == null) {
                    this.zzbRj.zzbRg = new ArrayList();
                }
                this.zzbRj.zzbRg.addAll(collection);
            }
            return this;
        }

        public MaskedWalletRequest build() {
            return this.zzbRj;
        }

        public Builder setAllowDebitCard(boolean z) {
            this.zzbRj.zzbRf = z;
            return this;
        }

        public Builder setAllowPrepaidCard(boolean z) {
            this.zzbRj.zzbRe = z;
            return this;
        }

        public Builder setCart(Cart cart) {
            this.zzbRj.zzbQh = cart;
            return this;
        }

        public Builder setCountryCode(String str) {
            this.zzbRj.zzUI = str;
            return this;
        }

        public Builder setCurrencyCode(String str) {
            this.zzbRj.zzbPQ = str;
            return this;
        }

        public Builder setEstimatedTotalPrice(String str) {
            this.zzbRj.zzbQZ = str;
            return this;
        }

        @Deprecated
        public Builder setIsBillingAgreement(boolean z) {
            this.zzbRj.zzbRc = z;
            return this;
        }

        public Builder setMerchantName(String str) {
            this.zzbRj.zzbRa = str;
            return this;
        }

        public Builder setMerchantTransactionId(String str) {
            this.zzbRj.zzbPX = str;
            return this;
        }

        public Builder setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            this.zzbRj.zzbRh = paymentMethodTokenizationParameters;
            return this;
        }

        public Builder setPhoneNumberRequired(boolean z) {
            this.zzbRj.zzbQW = z;
            return this;
        }

        public Builder setShippingAddressRequired(boolean z) {
            this.zzbRj.zzbQX = z;
            return this;
        }

        @Deprecated
        public Builder setUseMinimalBillingAddress(boolean z) {
            this.zzbRj.zzbQY = z;
            return this;
        }
    }

    MaskedWalletRequest() {
        this.zzbRe = true;
        this.zzbRf = true;
    }

    MaskedWalletRequest(String str, boolean z, boolean z2, boolean z3, String str2, String str3, String str4, Cart cart, boolean z4, boolean z5, CountrySpecification[] countrySpecificationArr, boolean z6, boolean z7, ArrayList<CountrySpecification> arrayList, PaymentMethodTokenizationParameters paymentMethodTokenizationParameters, ArrayList<Integer> arrayList2, String str5) {
        this.zzbPX = str;
        this.zzbQW = z;
        this.zzbQX = z2;
        this.zzbQY = z3;
        this.zzbQZ = str2;
        this.zzbPQ = str3;
        this.zzbRa = str4;
        this.zzbQh = cart;
        this.zzbRb = z4;
        this.zzbRc = z5;
        this.zzbRd = countrySpecificationArr;
        this.zzbRe = z6;
        this.zzbRf = z7;
        this.zzbRg = arrayList;
        this.zzbRh = paymentMethodTokenizationParameters;
        this.zzbRi = arrayList2;
        this.zzUI = str5;
    }

    public static Builder newBuilder() {
        MaskedWalletRequest maskedWalletRequest = new MaskedWalletRequest();
        maskedWalletRequest.getClass();
        return new Builder();
    }

    public boolean allowDebitCard() {
        return this.zzbRf;
    }

    public boolean allowPrepaidCard() {
        return this.zzbRe;
    }

    public ArrayList<Integer> getAllowedCardNetworks() {
        return this.zzbRi;
    }

    public ArrayList<CountrySpecification> getAllowedCountrySpecificationsForShipping() {
        return this.zzbRg;
    }

    public CountrySpecification[] getAllowedShippingCountrySpecifications() {
        return this.zzbRd;
    }

    public Cart getCart() {
        return this.zzbQh;
    }

    public String getCountryCode() {
        return this.zzUI;
    }

    public String getCurrencyCode() {
        return this.zzbPQ;
    }

    public String getEstimatedTotalPrice() {
        return this.zzbQZ;
    }

    public String getMerchantName() {
        return this.zzbRa;
    }

    public String getMerchantTransactionId() {
        return this.zzbPX;
    }

    public PaymentMethodTokenizationParameters getPaymentMethodTokenizationParameters() {
        return this.zzbRh;
    }

    @Deprecated
    public boolean isBillingAgreement() {
        return this.zzbRc;
    }

    public boolean isPhoneNumberRequired() {
        return this.zzbQW;
    }

    public boolean isShippingAddressRequired() {
        return this.zzbQX;
    }

    @Deprecated
    public boolean useMinimalBillingAddress() {
        return this.zzbQY;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzo.zza(this, parcel, i);
    }
}
