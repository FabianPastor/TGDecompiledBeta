package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class WalletFragmentInitParams extends zza implements ReflectedParcelable {
    public static final Creator<WalletFragmentInitParams> CREATOR = new zza();
    private String zzaiu;
    private MaskedWalletRequest zzbRX;
    private MaskedWallet zzbRY;
    private int zzbSl;

    public final class Builder {
        final /* synthetic */ WalletFragmentInitParams zzbSm;

        private Builder(WalletFragmentInitParams walletFragmentInitParams) {
            this.zzbSm = walletFragmentInitParams;
        }

        public WalletFragmentInitParams build() {
            boolean z = true;
            boolean z2 = (this.zzbSm.zzbRY != null && this.zzbSm.zzbRX == null) || (this.zzbSm.zzbRY == null && this.zzbSm.zzbRX != null);
            zzac.zza(z2, (Object) "Exactly one of MaskedWallet or MaskedWalletRequest is required");
            if (this.zzbSm.zzbSl < 0) {
                z = false;
            }
            zzac.zza(z, (Object) "masked wallet request code is required and must be non-negative");
            return this.zzbSm;
        }

        public Builder setAccountName(String str) {
            this.zzbSm.zzaiu = str;
            return this;
        }

        public Builder setMaskedWallet(MaskedWallet maskedWallet) {
            this.zzbSm.zzbRY = maskedWallet;
            return this;
        }

        public Builder setMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            this.zzbSm.zzbRX = maskedWalletRequest;
            return this;
        }

        public Builder setMaskedWalletRequestCode(int i) {
            this.zzbSm.zzbSl = i;
            return this;
        }
    }

    private WalletFragmentInitParams() {
        this.zzbSl = -1;
    }

    WalletFragmentInitParams(String str, MaskedWalletRequest maskedWalletRequest, int i, MaskedWallet maskedWallet) {
        this.zzaiu = str;
        this.zzbRX = maskedWalletRequest;
        this.zzbSl = i;
        this.zzbRY = maskedWallet;
    }

    public static Builder newBuilder() {
        WalletFragmentInitParams walletFragmentInitParams = new WalletFragmentInitParams();
        walletFragmentInitParams.getClass();
        return new Builder();
    }

    public String getAccountName() {
        return this.zzaiu;
    }

    public MaskedWallet getMaskedWallet() {
        return this.zzbRY;
    }

    public MaskedWalletRequest getMaskedWalletRequest() {
        return this.zzbRX;
    }

    public int getMaskedWalletRequestCode() {
        return this.zzbSl;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
