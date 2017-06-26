package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.List;

public final class Cart extends zza implements ReflectedParcelable {
    public static final Creator<Cart> CREATOR = new zzd();
    String zzbOk;
    String zzbOl;
    ArrayList<LineItem> zzbOm;

    public final class Builder {
        private /* synthetic */ Cart zzbOn;

        private Builder(Cart cart) {
            this.zzbOn = cart;
        }

        public final Builder addLineItem(LineItem lineItem) {
            this.zzbOn.zzbOm.add(lineItem);
            return this;
        }

        public final Cart build() {
            return this.zzbOn;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzbOn.zzbOl = str;
            return this;
        }

        public final Builder setLineItems(List<LineItem> list) {
            this.zzbOn.zzbOm.clear();
            this.zzbOn.zzbOm.addAll(list);
            return this;
        }

        public final Builder setTotalPrice(String str) {
            this.zzbOn.zzbOk = str;
            return this;
        }
    }

    Cart() {
        this.zzbOm = new ArrayList();
    }

    Cart(String str, String str2, ArrayList<LineItem> arrayList) {
        this.zzbOk = str;
        this.zzbOl = str2;
        this.zzbOm = arrayList;
    }

    public static Builder newBuilder() {
        Cart cart = new Cart();
        cart.getClass();
        return new Builder();
    }

    public final String getCurrencyCode() {
        return this.zzbOl;
    }

    public final ArrayList<LineItem> getLineItems() {
        return this.zzbOm;
    }

    public final String getTotalPrice() {
        return this.zzbOk;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOk, false);
        zzd.zza(parcel, 3, this.zzbOl, false);
        zzd.zzc(parcel, 4, this.zzbOm, false);
        zzd.zzI(parcel, zze);
    }
}
