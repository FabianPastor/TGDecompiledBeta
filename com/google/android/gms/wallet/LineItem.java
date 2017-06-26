package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class LineItem extends zza {
    public static final Creator<LineItem> CREATOR = new zzn();
    String description;
    String zzbOP;
    String zzbOQ;
    int zzbOR;
    String zzbOk;
    String zzbOl;

    public final class Builder {
        private /* synthetic */ LineItem zzbOS;

        private Builder(LineItem lineItem) {
            this.zzbOS = lineItem;
        }

        public final LineItem build() {
            return this.zzbOS;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzbOS.zzbOl = str;
            return this;
        }

        public final Builder setDescription(String str) {
            this.zzbOS.description = str;
            return this;
        }

        public final Builder setQuantity(String str) {
            this.zzbOS.zzbOP = str;
            return this;
        }

        public final Builder setRole(int i) {
            this.zzbOS.zzbOR = i;
            return this;
        }

        public final Builder setTotalPrice(String str) {
            this.zzbOS.zzbOk = str;
            return this;
        }

        public final Builder setUnitPrice(String str) {
            this.zzbOS.zzbOQ = str;
            return this;
        }
    }

    public interface Role {
        public static final int REGULAR = 0;
        public static final int SHIPPING = 2;
        public static final int TAX = 1;
    }

    LineItem() {
        this.zzbOR = 0;
    }

    LineItem(String str, String str2, String str3, String str4, int i, String str5) {
        this.description = str;
        this.zzbOP = str2;
        this.zzbOQ = str3;
        this.zzbOk = str4;
        this.zzbOR = i;
        this.zzbOl = str5;
    }

    public static Builder newBuilder() {
        LineItem lineItem = new LineItem();
        lineItem.getClass();
        return new Builder();
    }

    public final String getCurrencyCode() {
        return this.zzbOl;
    }

    public final String getDescription() {
        return this.description;
    }

    public final String getQuantity() {
        return this.zzbOP;
    }

    public final int getRole() {
        return this.zzbOR;
    }

    public final String getTotalPrice() {
        return this.zzbOk;
    }

    public final String getUnitPrice() {
        return this.zzbOQ;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.description, false);
        zzd.zza(parcel, 3, this.zzbOP, false);
        zzd.zza(parcel, 4, this.zzbOQ, false);
        zzd.zza(parcel, 5, this.zzbOk, false);
        zzd.zzc(parcel, 6, this.zzbOR);
        zzd.zza(parcel, 7, this.zzbOl, false);
        zzd.zzI(parcel, zze);
    }
}
