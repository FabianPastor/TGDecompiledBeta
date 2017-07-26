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
    String zzbOm;
    String zzbOn;
    ArrayList<LineItem> zzbOo;

    public final class Builder {
        private /* synthetic */ Cart zzbOp;

        private Builder(Cart cart) {
            this.zzbOp = cart;
        }

        public final Builder addLineItem(LineItem lineItem) {
            this.zzbOp.zzbOo.add(lineItem);
            return this;
        }

        public final Cart build() {
            return this.zzbOp;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzbOp.zzbOn = str;
            return this;
        }

        public final Builder setLineItems(List<LineItem> list) {
            this.zzbOp.zzbOo.clear();
            this.zzbOp.zzbOo.addAll(list);
            return this;
        }

        public final Builder setTotalPrice(String str) {
            this.zzbOp.zzbOm = str;
            return this;
        }
    }

    Cart() {
        this.zzbOo = new ArrayList();
    }

    Cart(String str, String str2, ArrayList<LineItem> arrayList) {
        this.zzbOm = str;
        this.zzbOn = str2;
        this.zzbOo = arrayList;
    }

    public static Builder newBuilder() {
        Cart cart = new Cart();
        cart.getClass();
        return new Builder();
    }

    public final String getCurrencyCode() {
        return this.zzbOn;
    }

    public final ArrayList<LineItem> getLineItems() {
        return this.zzbOo;
    }

    public final String getTotalPrice() {
        return this.zzbOm;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbOm, false);
        zzd.zza(parcel, 3, this.zzbOn, false);
        zzd.zzc(parcel, 4, this.zzbOo, false);
        zzd.zzI(parcel, zze);
    }
}
