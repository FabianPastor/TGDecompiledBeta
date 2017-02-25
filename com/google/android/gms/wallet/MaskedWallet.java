package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class MaskedWallet extends zza implements ReflectedParcelable {
    public static final Creator<MaskedWallet> CREATOR = new zzn();
    String zzbPW;
    String zzbPX;
    String zzbPZ;
    LoyaltyWalletObject[] zzbQT;
    OfferWalletObject[] zzbQU;
    zza zzbQa;
    zza zzbQb;
    String[] zzbQc;
    UserAddress zzbQd;
    UserAddress zzbQe;
    InstrumentInfo[] zzbQf;

    public final class Builder {
        final /* synthetic */ MaskedWallet zzbQV;

        private Builder(MaskedWallet maskedWallet) {
            this.zzbQV = maskedWallet;
        }

        public MaskedWallet build() {
            return this.zzbQV;
        }

        public Builder setBuyerBillingAddress(UserAddress userAddress) {
            this.zzbQV.zzbQd = userAddress;
            return this;
        }

        public Builder setBuyerShippingAddress(UserAddress userAddress) {
            this.zzbQV.zzbQe = userAddress;
            return this;
        }

        public Builder setEmail(String str) {
            this.zzbQV.zzbPZ = str;
            return this;
        }

        public Builder setGoogleTransactionId(String str) {
            this.zzbQV.zzbPW = str;
            return this;
        }

        public Builder setInstrumentInfos(InstrumentInfo[] instrumentInfoArr) {
            this.zzbQV.zzbQf = instrumentInfoArr;
            return this;
        }

        public Builder setMerchantTransactionId(String str) {
            this.zzbQV.zzbPX = str;
            return this;
        }

        public Builder setPaymentDescriptions(String[] strArr) {
            this.zzbQV.zzbQc = strArr;
            return this;
        }

        @Deprecated
        public Builder zza(LoyaltyWalletObject[] loyaltyWalletObjectArr) {
            this.zzbQV.zzbQT = loyaltyWalletObjectArr;
            return this;
        }

        @Deprecated
        public Builder zza(OfferWalletObject[] offerWalletObjectArr) {
            this.zzbQV.zzbQU = offerWalletObjectArr;
            return this;
        }
    }

    private MaskedWallet() {
    }

    MaskedWallet(String str, String str2, String[] strArr, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, LoyaltyWalletObject[] loyaltyWalletObjectArr, OfferWalletObject[] offerWalletObjectArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr) {
        this.zzbPW = str;
        this.zzbPX = str2;
        this.zzbQc = strArr;
        this.zzbPZ = str3;
        this.zzbQa = com_google_android_gms_wallet_zza;
        this.zzbQb = com_google_android_gms_wallet_zza2;
        this.zzbQT = loyaltyWalletObjectArr;
        this.zzbQU = offerWalletObjectArr;
        this.zzbQd = userAddress;
        this.zzbQe = userAddress2;
        this.zzbQf = instrumentInfoArr;
    }

    public static Builder newBuilderFrom(MaskedWallet maskedWallet) {
        zzac.zzw(maskedWallet);
        return zzTW().setGoogleTransactionId(maskedWallet.getGoogleTransactionId()).setMerchantTransactionId(maskedWallet.getMerchantTransactionId()).setPaymentDescriptions(maskedWallet.getPaymentDescriptions()).setInstrumentInfos(maskedWallet.getInstrumentInfos()).setEmail(maskedWallet.getEmail()).zza(maskedWallet.zzTX()).zza(maskedWallet.zzTY()).setBuyerBillingAddress(maskedWallet.getBuyerBillingAddress()).setBuyerShippingAddress(maskedWallet.getBuyerShippingAddress());
    }

    public static Builder zzTW() {
        MaskedWallet maskedWallet = new MaskedWallet();
        maskedWallet.getClass();
        return new Builder();
    }

    public UserAddress getBuyerBillingAddress() {
        return this.zzbQd;
    }

    public UserAddress getBuyerShippingAddress() {
        return this.zzbQe;
    }

    public String getEmail() {
        return this.zzbPZ;
    }

    public String getGoogleTransactionId() {
        return this.zzbPW;
    }

    public InstrumentInfo[] getInstrumentInfos() {
        return this.zzbQf;
    }

    public String getMerchantTransactionId() {
        return this.zzbPX;
    }

    public String[] getPaymentDescriptions() {
        return this.zzbQc;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzn.zza(this, parcel, i);
    }

    @Deprecated
    public LoyaltyWalletObject[] zzTX() {
        return this.zzbQT;
    }

    @Deprecated
    public OfferWalletObject[] zzTY() {
        return this.zzbQU;
    }
}
