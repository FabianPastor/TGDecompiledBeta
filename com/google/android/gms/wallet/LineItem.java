package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

public final class LineItem extends zza {
    public static final Creator<LineItem> CREATOR = new zzl();
    String description;
    String zzbPP;
    String zzbPQ;
    String zzbQw;
    String zzbQx;
    int zzbQy;

    public final class Builder {
        final /* synthetic */ LineItem zzbQz;

        private Builder(LineItem lineItem) {
            this.zzbQz = lineItem;
        }

        public LineItem build() {
            return this.zzbQz;
        }

        public Builder setCurrencyCode(String str) {
            this.zzbQz.zzbPQ = str;
            return this;
        }

        public Builder setDescription(String str) {
            this.zzbQz.description = str;
            return this;
        }

        public Builder setQuantity(String str) {
            this.zzbQz.zzbQw = str;
            return this;
        }

        public Builder setRole(int i) {
            this.zzbQz.zzbQy = i;
            return this;
        }

        public Builder setTotalPrice(String str) {
            this.zzbQz.zzbPP = str;
            return this;
        }

        public Builder setUnitPrice(String str) {
            this.zzbQz.zzbQx = str;
            return this;
        }
    }

    public interface Role {
        public static final int REGULAR = 0;
        public static final int SHIPPING = 2;
        public static final int TAX = 1;
    }

    LineItem() {
        this.zzbQy = 0;
    }

    LineItem(String str, String str2, String str3, String str4, int i, String str5) {
        this.description = str;
        this.zzbQw = str2;
        this.zzbQx = str3;
        this.zzbPP = str4;
        this.zzbQy = i;
        this.zzbPQ = str5;
    }

    public static Builder newBuilder() {
        LineItem lineItem = new LineItem();
        lineItem.getClass();
        return new Builder();
    }

    public String getCurrencyCode() {
        return this.zzbPQ;
    }

    public String getDescription() {
        return this.description;
    }

    public String getQuantity() {
        return this.zzbQw;
    }

    public int getRole() {
        return this.zzbQy;
    }

    public String getTotalPrice() {
        return this.zzbPP;
    }

    public String getUnitPrice() {
        return this.zzbQx;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }
}
