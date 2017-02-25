package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class FullWalletRequest extends zza implements ReflectedParcelable {
    public static final Creator<FullWalletRequest> CREATOR = new zzh();
    String zzbPW;
    String zzbPX;
    Cart zzbQh;

    public final class Builder {
        final /* synthetic */ FullWalletRequest zzbQi;

        private Builder(FullWalletRequest fullWalletRequest) {
            this.zzbQi = fullWalletRequest;
        }

        public FullWalletRequest build() {
            return this.zzbQi;
        }

        public Builder setCart(Cart cart) {
            this.zzbQi.zzbQh = cart;
            return this;
        }

        public Builder setGoogleTransactionId(String str) {
            this.zzbQi.zzbPW = str;
            return this;
        }

        public Builder setMerchantTransactionId(String str) {
            this.zzbQi.zzbPX = str;
            return this;
        }
    }

    FullWalletRequest() {
    }

    FullWalletRequest(String str, String str2, Cart cart) {
        this.zzbPW = str;
        this.zzbPX = str2;
        this.zzbQh = cart;
    }

    public static Builder newBuilder() {
        FullWalletRequest fullWalletRequest = new FullWalletRequest();
        fullWalletRequest.getClass();
        return new Builder();
    }

    public Cart getCart() {
        return this.zzbQh;
    }

    public String getGoogleTransactionId() {
        return this.zzbPW;
    }

    public String getMerchantTransactionId() {
        return this.zzbPX;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }
}
