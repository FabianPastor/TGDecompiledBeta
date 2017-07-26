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
    private MaskedWalletRequest zzbQf;
    private MaskedWallet zzbQg;
    private int zzbQt;

    public final class Builder {
        private /* synthetic */ WalletFragmentInitParams zzbQu;

        private Builder(WalletFragmentInitParams walletFragmentInitParams) {
            this.zzbQu = walletFragmentInitParams;
        }

        public final WalletFragmentInitParams build() {
            boolean z = true;
            boolean z2 = (this.zzbQu.zzbQg != null && this.zzbQu.zzbQf == null) || (this.zzbQu.zzbQg == null && this.zzbQu.zzbQf != null);
            zzbo.zza(z2, (Object) "Exactly one of MaskedWallet or MaskedWalletRequest is required");
            if (this.zzbQu.zzbQt < 0) {
                z = false;
            }
            zzbo.zza(z, (Object) "masked wallet request code is required and must be non-negative");
            return this.zzbQu;
        }

        public final Builder setAccountName(String str) {
            this.zzbQu.zzakh = str;
            return this;
        }

        public final Builder setMaskedWallet(MaskedWallet maskedWallet) {
            this.zzbQu.zzbQg = maskedWallet;
            return this;
        }

        public final Builder setMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            this.zzbQu.zzbQf = maskedWalletRequest;
            return this;
        }

        public final Builder setMaskedWalletRequestCode(int i) {
            this.zzbQu.zzbQt = i;
            return this;
        }
    }

    private WalletFragmentInitParams() {
        this.zzbQt = -1;
    }

    WalletFragmentInitParams(String str, MaskedWalletRequest maskedWalletRequest, int i, MaskedWallet maskedWallet) {
        this.zzakh = str;
        this.zzbQf = maskedWalletRequest;
        this.zzbQt = i;
        this.zzbQg = maskedWallet;
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
        return this.zzbQg;
    }

    public final MaskedWalletRequest getMaskedWalletRequest() {
        return this.zzbQf;
    }

    public final int getMaskedWalletRequestCode() {
        return this.zzbQt;
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
