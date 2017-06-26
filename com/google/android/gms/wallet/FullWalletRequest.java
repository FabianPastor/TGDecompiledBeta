package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class FullWalletRequest extends zza implements ReflectedParcelable {
    public static final Creator<FullWalletRequest> CREATOR = new zzh();
    String zzbOo;
    String zzbOp;
    Cart zzbOz;

    public final class Builder {
        private /* synthetic */ FullWalletRequest zzbOA;

        private Builder(FullWalletRequest fullWalletRequest) {
            this.zzbOA = fullWalletRequest;
        }

        public final FullWalletRequest build() {
            return this.zzbOA;
        }

        public final Builder setCart(Cart cart) {
            this.zzbOA.zzbOz = cart;
            return this;
        }

        public final Builder setGoogleTransactionId(String str) {
            this.zzbOA.zzbOo = str;
            return this;
        }

        public final Builder setMerchantTransactionId(String str) {
            this.zzbOA.zzbOp = str;
            return this;
        }
    }

    FullWalletRequest() {
    }

    FullWalletRequest(String str, String str2, Cart cart) {
        this.zzbOo = str;
        this.zzbOp = str2;
        this.zzbOz = cart;
    }

    public static Builder newBuilder() {
        FullWalletRequest fullWalletRequest = new FullWalletRequest();
        fullWalletRequest.getClass();
        return new Builder();
    }

    public final Cart getCart() {
        return this.zzbOz;
    }

    public final String getGoogleTransactionId() {
        return this.zzbOo;
    }

    public final String getMerchantTransactionId() {
        return this.zzbOp;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOo, false);
        zzd.zza(parcel, 3, this.zzbOp, false);
        zzd.zza(parcel, 4, this.zzbOz, i, false);
        zzd.zzI(parcel, zze);
    }
}
