package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;
import java.util.List;

public final class Cart extends zza implements ReflectedParcelable {
    public static final Creator<Cart> CREATOR = new zzc();
    String zzbPP;
    String zzbPQ;
    ArrayList<LineItem> zzbPR;

    public final class Builder {
        final /* synthetic */ Cart zzbPS;

        private Builder(Cart cart) {
            this.zzbPS = cart;
        }

        public Builder addLineItem(LineItem lineItem) {
            this.zzbPS.zzbPR.add(lineItem);
            return this;
        }

        public Cart build() {
            return this.zzbPS;
        }

        public Builder setCurrencyCode(String str) {
            this.zzbPS.zzbPQ = str;
            return this;
        }

        public Builder setLineItems(List<LineItem> list) {
            this.zzbPS.zzbPR.clear();
            this.zzbPS.zzbPR.addAll(list);
            return this;
        }

        public Builder setTotalPrice(String str) {
            this.zzbPS.zzbPP = str;
            return this;
        }
    }

    Cart() {
        this.zzbPR = new ArrayList();
    }

    Cart(String str, String str2, ArrayList<LineItem> arrayList) {
        this.zzbPP = str;
        this.zzbPQ = str2;
        this.zzbPR = arrayList;
    }

    public static Builder newBuilder() {
        Cart cart = new Cart();
        cart.getClass();
        return new Builder();
    }

    public String getCurrencyCode() {
        return this.zzbPQ;
    }

    public ArrayList<LineItem> getLineItems() {
        return this.zzbPR;
    }

    public String getTotalPrice() {
        return this.zzbPP;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }
}
