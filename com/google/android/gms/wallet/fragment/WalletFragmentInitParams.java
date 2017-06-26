package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class WalletFragmentInitParams extends zza implements ReflectedParcelable {
    public static final Creator<WalletFragmentInitParams> CREATOR = new zzd();
    private String zzakh;
    private MaskedWalletRequest zzbQd;
    private MaskedWallet zzbQe;
    private int zzbQr;

    public final class Builder {
        private /* synthetic */ WalletFragmentInitParams zzbQs;

        private Builder(WalletFragmentInitParams walletFragmentInitParams) {
            this.zzbQs = walletFragmentInitParams;
        }

        public final WalletFragmentInitParams build() {
            boolean z = true;
            boolean z2 = (this.zzbQs.zzbQe != null && this.zzbQs.zzbQd == null) || (this.zzbQs.zzbQe == null && this.zzbQs.zzbQd != null);
            zzbo.zza(z2, (Object) "Exactly one of MaskedWallet or MaskedWalletRequest is required");
            if (this.zzbQs.zzbQr < 0) {
                z = false;
            }
            zzbo.zza(z, (Object) "masked wallet request code is required and must be non-negative");
            return this.zzbQs;
        }

        public final Builder setAccountName(String str) {
            this.zzbQs.zzakh = str;
            return this;
        }

        public final Builder setMaskedWallet(MaskedWallet maskedWallet) {
            this.zzbQs.zzbQe = maskedWallet;
            return this;
        }

        public final Builder setMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            this.zzbQs.zzbQd = maskedWalletRequest;
            return this;
        }

        public final Builder setMaskedWalletRequestCode(int i) {
            this.zzbQs.zzbQr = i;
            return this;
        }
    }

    private WalletFragmentInitParams() {
        this.zzbQr = -1;
    }

    WalletFragmentInitParams(String str, MaskedWalletRequest maskedWalletRequest, int i, MaskedWallet maskedWallet) {
        this.zzakh = str;
        this.zzbQd = maskedWalletRequest;
        this.zzbQr = i;
        this.zzbQe = maskedWallet;
    }

    public static Builder newBuilder() {
        WalletFragmentInitParams walletFragmentInitParams = new WalletFragmentInitParams();
        walletFragmentInitParams.getClass();
        return new Builder();
    }

    public final String getAccountName() {
        return this.zzakh;
    }

    public final MaskedWallet getMaskedWallet() {
        return this.zzbQe;
    }

    public final MaskedWalletRequest getMaskedWalletRequest() {
        return this.zzbQd;
    }

    public final int getMaskedWalletRequestCode() {
        return this.zzbQr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getAccountName(), false);
        zzd.zza(parcel, 3, getMaskedWalletRequest(), i, false);
        zzd.zzc(parcel, 4, getMaskedWalletRequestCode());
        zzd.zza(parcel, 5, getMaskedWallet(), i, false);
        zzd.zzI(parcel, zze);
    }
}
