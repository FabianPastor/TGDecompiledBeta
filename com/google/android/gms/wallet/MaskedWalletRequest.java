package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.identity.intents.model.CountrySpecification;
import java.util.ArrayList;
import java.util.Collection;

public final class MaskedWalletRequest extends zza implements ReflectedParcelable {
    public static final Creator<MaskedWalletRequest> CREATOR = new zzs();
    String zzVJ;
    String zzbOl;
    String zzbOp;
    Cart zzbOz;
    PaymentMethodTokenizationParameters zzbPA;
    ArrayList<Integer> zzbPB;
    boolean zzbPp;
    boolean zzbPq;
    boolean zzbPr;
    String zzbPs;
    String zzbPt;
    private boolean zzbPu;
    boolean zzbPv;
    private CountrySpecification[] zzbPw;
    boolean zzbPx;
    boolean zzbPy;
    ArrayList<CountrySpecification> zzbPz;

    public final class Builder {
        private /* synthetic */ MaskedWalletRequest zzbPC;

        private Builder(MaskedWalletRequest maskedWalletRequest) {
            this.zzbPC = maskedWalletRequest;
        }

        public final Builder addAllowedCardNetwork(int i) {
            if (this.zzbPC.zzbPB == null) {
                this.zzbPC.zzbPB = new ArrayList();
            }
            this.zzbPC.zzbPB.add(Integer.valueOf(i));
            return this;
        }

        public final Builder addAllowedCardNetworks(Collection<Integer> collection) {
            if (collection != null) {
                if (this.zzbPC.zzbPB == null) {
                    this.zzbPC.zzbPB = new ArrayList();
                }
                this.zzbPC.zzbPB.addAll(collection);
            }
            return this;
        }

        public final Builder addAllowedCountrySpecificationForShipping(CountrySpecification countrySpecification) {
            if (this.zzbPC.zzbPz == null) {
                this.zzbPC.zzbPz = new ArrayList();
            }
            this.zzbPC.zzbPz.add(countrySpecification);
            return this;
        }

        public final Builder addAllowedCountrySpecificationsForShipping(Collection<CountrySpecification> collection) {
            if (collection != null) {
                if (this.zzbPC.zzbPz == null) {
                    this.zzbPC.zzbPz = new ArrayList();
                }
                this.zzbPC.zzbPz.addAll(collection);
            }
            return this;
        }

        public final MaskedWalletRequest build() {
            return this.zzbPC;
        }

        public final Builder setAllowDebitCard(boolean z) {
            this.zzbPC.zzbPy = z;
            return this;
        }

        public final Builder setAllowPrepaidCard(boolean z) {
            this.zzbPC.zzbPx = z;
            return this;
        }

        public final Builder setCart(Cart cart) {
            this.zzbPC.zzbOz = cart;
            return this;
        }

        public final Builder setCountryCode(String str) {
            this.zzbPC.zzVJ = str;
            return this;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzbPC.zzbOl = str;
            return this;
        }

        public final Builder setEstimatedTotalPrice(String str) {
            this.zzbPC.zzbPs = str;
            return this;
        }

        @Deprecated
        public final Builder setIsBillingAgreement(boolean z) {
            this.zzbPC.zzbPv = z;
            return this;
        }

        public final Builder setMerchantName(String str) {
            this.zzbPC.zzbPt = str;
            return this;
        }

        public final Builder setMerchantTransactionId(String str) {
            this.zzbPC.zzbOp = str;
            return this;
        }

        public final Builder setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            this.zzbPC.zzbPA = paymentMethodTokenizationParameters;
            return this;
        }

        public final Builder setPhoneNumberRequired(boolean z) {
            this.zzbPC.zzbPp = z;
            return this;
        }

        public final Builder setShippingAddressRequired(boolean z) {
            this.zzbPC.zzbPq = z;
            return this;
        }

        @Deprecated
        public final Builder setUseMinimalBillingAddress(boolean z) {
            this.zzbPC.zzbPr = z;
            return this;
        }
    }

    MaskedWalletRequest() {
        this.zzbPx = true;
        this.zzbPy = true;
    }

    MaskedWalletRequest(String str, boolean z, boolean z2, boolean z3, String str2, String str3, String str4, Cart cart, boolean z4, boolean z5, CountrySpecification[] countrySpecificationArr, boolean z6, boolean z7, ArrayList<CountrySpecification> arrayList, PaymentMethodTokenizationParameters paymentMethodTokenizationParameters, ArrayList<Integer> arrayList2, String str5) {
        this.zzbOp = str;
        this.zzbPp = z;
        this.zzbPq = z2;
        this.zzbPr = z3;
        this.zzbPs = str2;
        this.zzbOl = str3;
        this.zzbPt = str4;
        this.zzbOz = cart;
        this.zzbPu = z4;
        this.zzbPv = z5;
        this.zzbPw = countrySpecificationArr;
        this.zzbPx = z6;
        this.zzbPy = z7;
        this.zzbPz = arrayList;
        this.zzbPA = paymentMethodTokenizationParameters;
        this.zzbPB = arrayList2;
        this.zzVJ = str5;
    }

    public static Builder newBuilder() {
        MaskedWalletRequest maskedWalletRequest = new MaskedWalletRequest();
        maskedWalletRequest.getClass();
        return new Builder();
    }

    public final boolean allowDebitCard() {
        return this.zzbPy;
    }

    public final boolean allowPrepaidCard() {
        return this.zzbPx;
    }

    public final ArrayList<Integer> getAllowedCardNetworks() {
        return this.zzbPB;
    }

    public final ArrayList<CountrySpecification> getAllowedCountrySpecificationsForShipping() {
        return this.zzbPz;
    }

    public final CountrySpecification[] getAllowedShippingCountrySpecifications() {
        return this.zzbPw;
    }

    public final Cart getCart() {
        return this.zzbOz;
    }

    public final String getCountryCode() {
        return this.zzVJ;
    }

    public final String getCurrencyCode() {
        return this.zzbOl;
    }

    public final String getEstimatedTotalPrice() {
        return this.zzbPs;
    }

    public final String getMerchantName() {
        return this.zzbPt;
    }

    public final String getMerchantTransactionId() {
        return this.zzbOp;
    }

    public final PaymentMethodTokenizationParameters getPaymentMethodTokenizationParameters() {
        return this.zzbPA;
    }

    @Deprecated
    public final boolean isBillingAgreement() {
        return this.zzbPv;
    }

    public final boolean isPhoneNumberRequired() {
        return this.zzbPp;
    }

    public final boolean isShippingAddressRequired() {
        return this.zzbPq;
    }

    @Deprecated
    public final boolean useMinimalBillingAddress() {
        return this.zzbPr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOp, false);
        zzd.zza(parcel, 3, this.zzbPp);
        zzd.zza(parcel, 4, this.zzbPq);
        zzd.zza(parcel, 5, this.zzbPr);
        zzd.zza(parcel, 6, this.zzbPs, false);
        zzd.zza(parcel, 7, this.zzbOl, false);
        zzd.zza(parcel, 8, this.zzbPt, false);
        zzd.zza(parcel, 9, this.zzbOz, i, false);
        zzd.zza(parcel, 10, this.zzbPu);
        zzd.zza(parcel, 11, this.zzbPv);
        zzd.zza(parcel, 12, this.zzbPw, i, false);
        zzd.zza(parcel, 13, this.zzbPx);
        zzd.zza(parcel, 14, this.zzbPy);
        zzd.zzc(parcel, 15, this.zzbPz, false);
        zzd.zza(parcel, 16, this.zzbPA, i, false);
        zzd.zza(parcel, 17, this.zzbPB, false);
        zzd.zza(parcel, 18, this.zzVJ, false);
        zzd.zzI(parcel, zze);
    }
}
