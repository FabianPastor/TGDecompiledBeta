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
    String zzbOq;
    String zzbOr;
    String zzbOt;
    private zza zzbOu;
    private zza zzbOv;
    String[] zzbOw;
    UserAddress zzbOx;
    UserAddress zzbOy;
    InstrumentInfo[] zzbOz;
    private LoyaltyWalletObject[] zzbPo;
    private OfferWalletObject[] zzbPp;

    public final class Builder {
        final /* synthetic */ MaskedWallet zzbPq;

        private Builder(MaskedWallet maskedWallet) {
            this.zzbPq = maskedWallet;
        }

        public final MaskedWallet build() {
            return this.zzbPq;
        }

        public final Builder setBuyerBillingAddress(UserAddress userAddress) {
            this.zzbPq.zzbOx = userAddress;
            return this;
        }

        public final Builder setBuyerShippingAddress(UserAddress userAddress) {
            this.zzbPq.zzbOy = userAddress;
            return this;
        }

        public final Builder setEmail(String str) {
            this.zzbPq.zzbOt = str;
            return this;
        }

        public final Builder setGoogleTransactionId(String str) {
            this.zzbPq.zzbOq = str;
            return this;
        }

        public final Builder setInstrumentInfos(InstrumentInfo[] instrumentInfoArr) {
            this.zzbPq.zzbOz = instrumentInfoArr;
            return this;
        }

        public final Builder setMerchantTransactionId(String str) {
            this.zzbPq.zzbOr = str;
            return this;
        }

        public final Builder setPaymentDescriptions(String[] strArr) {
            this.zzbPq.zzbOw = strArr;
            return this;
        }
    }

    private MaskedWallet() {
    }

    MaskedWallet(String str, String str2, String[] strArr, String str3, zza com_google_android_gms_wallet_zza, zza com_google_android_gms_wallet_zza2, LoyaltyWalletObject[] loyaltyWalletObjectArr, OfferWalletObject[] offerWalletObjectArr, UserAddress userAddress, UserAddress userAddress2, InstrumentInfo[] instrumentInfoArr) {
        this.zzbOq = str;
        this.zzbOr = str2;
        this.zzbOw = strArr;
        this.zzbOt = str3;
        this.zzbOu = com_google_android_gms_wallet_zza;
        this.zzbOv = com_google_android_gms_wallet_zza2;
        this.zzbPo = loyaltyWalletObjectArr;
        this.zzbPp = offerWalletObjectArr;
        this.zzbOx = userAddress;
        this.zzbOy = userAddress2;
        this.zzbOz = instrumentInfoArr;
    }

    public static Builder newBuilderFrom(MaskedWallet maskedWallet) {
        zzbo.zzu(maskedWallet);
        MaskedWallet maskedWallet2 = new MaskedWallet();
        maskedWallet2.getClass();
        Builder email = new Builder().setGoogleTransactionId(maskedWallet.getGoogleTransactionId()).setMerchantTransactionId(maskedWallet.getMerchantTransactionId()).setPaymentDescriptions(maskedWallet.getPaymentDescriptions()).setInstrumentInfos(maskedWallet.getInstrumentInfos()).setEmail(maskedWallet.getEmail());
        email.zzbPq.zzbPo = maskedWallet.zzbPo;
        email.zzbPq.zzbPp = maskedWallet.zzbPp;
        return email.setBuyerBillingAddress(maskedWallet.getBuyerBillingAddress()).setBuyerShippingAddress(maskedWallet.getBuyerShippingAddress());
    }

    public final UserAddress getBuyerBillingAddress() {
        return this.zzbOx;
    }

    public final UserAddress getBuyerShippingAddress() {
        return this.zzbOy;
    }

    public final String getEmail() {
        return this.zzbOt;
    }

    public final String getGoogleTransactionId() {
        return this.zzbOq;
    }

    public final InstrumentInfo[] getInstrumentInfos() {
        return this.zzbOz;
    }

    public final String getMerchantTransactionId() {
        return this.zzbOr;
    }

    public final String[] getPaymentDescriptions() {
        return this.zzbOw;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOq, false);
        zzd.zza(parcel, 3, this.zzbOr, false);
        zzd.zza(parcel, 4, this.zzbOw, false);
        zzd.zza(parcel, 5, this.zzbOt, false);
        zzd.zza(parcel, 6, this.zzbOu, i, false);
        zzd.zza(parcel, 7, this.zzbOv, i, false);
        zzd.zza(parcel, 8, this.zzbPo, i, false);
        zzd.zza(parcel, 9, this.zzbPp, i, false);
        zzd.zza(parcel, 10, this.zzbOx, i, false);
        zzd.zza(parcel, 11, this.zzbOy, i, false);
        zzd.zza(parcel, 12, this.zzbOz, i, false);
        zzd.zzI(parcel, zze);
    }
}
