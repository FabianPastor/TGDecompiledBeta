package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class FullWalletRequest extends zzbfm implements ReflectedParcelable {
    public static final Creator<FullWalletRequest> CREATOR = new zzm();
    String zzlaa;
    String zzlab;
    Cart zzlal;

    public final class Builder {
        private /* synthetic */ FullWalletRequest zzlam;

        private Builder(FullWalletRequest fullWalletRequest) {
            this.zzlam = fullWalletRequest;
        }

        public final FullWalletRequest build() {
            return this.zzlam;
        }

        public final Builder setCart(Cart cart) {
            this.zzlam.zzlal = cart;
            return this;
        }

        public final Builder setGoogleTransactionId(String str) {
            this.zzlam.zzlaa = str;
            return this;
        }
    }

    FullWalletRequest() {
    }

    FullWalletRequest(String str, String str2, Cart cart) {
        this.zzlaa = str;
        this.zzlab = str2;
        this.zzlal = cart;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlaa, false);
        zzbfp.zza(parcel, 3, this.zzlab, false);
        zzbfp.zza(parcel, 4, this.zzlal, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
