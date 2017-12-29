package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.ArrayList;

public final class Cart extends zzbfm implements ReflectedParcelable {
    public static final Creator<Cart> CREATOR = new zzg();
    String zzkzr;
    String zzkzs;
    ArrayList<LineItem> zzkzt;

    public final class Builder {
        private /* synthetic */ Cart zzkzu;

        private Builder(Cart cart) {
            this.zzkzu = cart;
        }

        public final Builder addLineItem(LineItem lineItem) {
            this.zzkzu.zzkzt.add(lineItem);
            return this;
        }

        public final Cart build() {
            return this.zzkzu;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzkzu.zzkzs = str;
            return this;
        }

        public final Builder setTotalPrice(String str) {
            this.zzkzu.zzkzr = str;
            return this;
        }
    }

    Cart() {
        this.zzkzt = new ArrayList();
    }

    Cart(String str, String str2, ArrayList<LineItem> arrayList) {
        this.zzkzr = str;
        this.zzkzs = str2;
        this.zzkzt = arrayList;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzkzr, false);
        zzbfp.zza(parcel, 3, this.zzkzs, false);
        zzbfp.zzc(parcel, 4, this.zzkzt, false);
        zzbfp.zzai(parcel, zze);
    }
}
