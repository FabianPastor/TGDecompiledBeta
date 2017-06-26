package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.identity.intents.model.UserAddress;

public final class MaskedWallet extends zza implements ReflectedParcelable {
    public static final Creator<MaskedWallet> CREATOR = new zzq();
    String zzbOo;
    String zzbOp;
    String zzbOr;
    private zza zzbOs;
    private zza zzbOt;
    String[] zzbOu;
    UserAddress zzbOv;
    UserAddress zzbOw;
    InstrumentInfo[] zzbOx;
    private LoyaltyWalletObject[] zzbPm;
    private OfferWalletObject[] zzbPn;

    public final class Builder {
        final /* synthetic */ MaskedWallet zzbPo;

        private Builder(MaskedWallet maskedWallet) {
            this.zzbPo = maskedWallet;
        }

        public final MaskedWallet build() {
            return this.zzbPo;
        }

        public final Builder setBuyerBillingAddress(UserAddress userAddress) {
            this.zzbPo.zzbOv = userAddress;
            return this;
        }

        public final Builder setBuyerShippingAddress(UserAddress userAddress) {
            this.zzbPo.zzbOw = userAddress;
            return this;
        }

        public final Builder setEmail(String str) {
            this.zzbPo.zzbOr = str;
            return this;
        }

        public final Builder setGoogleTransactionId(String str) {
            this.zzbPo.zzbOo = str;
            return this;
        }

        public final Builder setInstrumentInfos(InstrumentInfo[] instrumentInfoArr) {
            this.zzbPo.zzbOx = instrumentInfoArr;
            return this;
        }

        public final Builder setMerchantTransactionId(String str) {
            this.zzbPo.zzbOp = str;
            return this;
        }

        public final Builder setPaymentDescriptions(String[] strArr) {
            this.zzbPo.zzbOu = strArr;
            return this;
        }
    }

    private MaskedWallet() {
    }

    MaskedWallet(String str, String str2, String[] strArr, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, LoyaltyWalletObject[] loyaltyWalletObjectArr, OfferWalletObject[] offerWalletObjectArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr) {
        this.zzbOo = str;
        this.zzbOp = str2;
        this.zzbOu = strArr;
        this.zzbOr = str3;
        this.zzbOs = com_google_android_gms_wallet_zza;
        this.zzbOt = com_google_android_gms_wallet_zza2;
        this.zzbPm = loyaltyWalletObjectArr;
        this.zzbPn = offerWalletObjectArr;
        this.zzbOv = userAddress;
        this.zzbOw = userAddress2;
        this.zzbOx = instrumentInfoArr;
    }

    public static Builder newBuilderFrom(MaskedWallet maskedWallet) {
        zzbo.zzu(maskedWallet);
        MaskedWallet maskedWallet2 = new MaskedWallet();
        maskedWallet2.getClass();
        Builder email = new Builder().setGoogleTransactionId(maskedWallet.getGoogleTransactionId()).setMerchantTransactionId(maskedWallet.getMerchantTransactionId()).setPaymentDescriptions(maskedWallet.getPaymentDescriptions()).setInstrumentInfos(maskedWallet.getInstrumentInfos()).setEmail(maskedWallet.getEmail());
        email.zzbPo.zzbPm = maskedWallet.zzbPm;
        email.zzbPo.zzbPn = maskedWallet.zzbPn;
        return email.setBuyerBillingAddress(maskedWallet.getBuyerBillingAddress()).setBuyerShippingAddress(maskedWallet.getBuyerShippingAddress());
    }

    public final UserAddress getBuyerBillingAddress() {
        return this.zzbOv;
    }

    public final UserAddress getBuyerShippingAddress() {
        return this.zzbOw;
    }

    public final String getEmail() {
        return this.zzbOr;
    }

    public final String getGoogleTransactionId() {
        return this.zzbOo;
    }

    public final InstrumentInfo[] getInstrumentInfos() {
        return this.zzbOx;
    }

    public final String getMerchantTransactionId() {
        return this.zzbOp;
    }

    public final String[] getPaymentDescriptions() {
        return this.zzbOu;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOo, false);
        zzd.zza(parcel, 3, this.zzbOp, false);
        zzd.zza(parcel, 4, this.zzbOu, false);
        zzd.zza(parcel, 5, this.zzbOr, false);
        zzd.zza(parcel, 6, this.zzbOs, i, false);
        zzd.zza(parcel, 7, this.zzbOt, i, false);
        zzd.zza(parcel, 8, this.zzbPm, i, false);
        zzd.zza(parcel, 9, this.zzbPn, i, false);
        zzd.zza(parcel, 10, this.zzbOv, i, false);
        zzd.zza(parcel, 11, this.zzbOw, i, false);
        zzd.zza(parcel, 12, this.zzbOx, i, false);
        zzd.zzI(parcel, zze);
    }
}
