package com.google.android.gms.wallet.fragment;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;

public final class WalletFragmentInitParams extends zzbfm implements ReflectedParcelable {
    public static final Creator<WalletFragmentInitParams> CREATOR = new zzd();
    private String zzebv;
    private MaskedWalletRequest zzler;
    private MaskedWallet zzles;
    private int zzlff;

    public final class Builder {
        private /* synthetic */ WalletFragmentInitParams zzlfg;

        private Builder(WalletFragmentInitParams walletFragmentInitParams) {
            this.zzlfg = walletFragmentInitParams;
        }

        public final WalletFragmentInitParams build() {
            boolean z = true;
            boolean z2 = (this.zzlfg.zzles != null && this.zzlfg.zzler == null) || (this.zzlfg.zzles == null && this.zzlfg.zzler != null);
            zzbq.zza(z2, "Exactly one of MaskedWallet or MaskedWalletRequest is required");
            if (this.zzlfg.zzlff < 0) {
                z = false;
            }
            zzbq.zza(z, "masked wallet request code is required and must be non-negative");
            return this.zzlfg;
        }

        public final Builder setMaskedWalletRequest(MaskedWalletRequest maskedWalletRequest) {
            this.zzlfg.zzler = maskedWalletRequest;
            return this;
        }

        public final Builder setMaskedWalletRequestCode(int i) {
            this.zzlfg.zzlff = i;
            return this;
        }
    }

    private WalletFragmentInitParams() {
        this.zzlff = -1;
    }

    WalletFragmentInitParams(String str, MaskedWalletRequest maskedWalletRequest, int i, MaskedWallet maskedWallet) {
        this.zzebv = str;
        this.zzler = maskedWalletRequest;
        this.zzlff = i;
        this.zzles = maskedWallet;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final String getAccountName() {
        return this.zzebv;
    }

    public final MaskedWallet getMaskedWallet() {
        return this.zzles;
    }

    public final MaskedWalletRequest getMaskedWalletRequest() {
        return this.zzler;
    }

    public final int getMaskedWalletRequestCode() {
        return this.zzlff;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getAccountName(), false);
        zzbfp.zza(parcel, 3, getMaskedWalletRequest(), i, false);
        zzbfp.zzc(parcel, 4, getMaskedWalletRequestCode());
        zzbfp.zza(parcel, 5, getMaskedWallet(), i, false);
        zzbfp.zzai(parcel, zze);
    }
}
