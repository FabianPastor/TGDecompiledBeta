package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class LineItem extends zzbfm {
    public static final Creator<LineItem> CREATOR = new zzt();
    String description;
    String zzkzr;
    String zzkzs;
    String zzlbe;
    String zzlbf;
    int zzlbg;

    public final class Builder {
        private /* synthetic */ LineItem zzlbh;

        private Builder(LineItem lineItem) {
            this.zzlbh = lineItem;
        }

        public final LineItem build() {
            return this.zzlbh;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzlbh.zzkzs = str;
            return this;
        }

        public final Builder setDescription(String str) {
            this.zzlbh.description = str;
            return this;
        }

        public final Builder setQuantity(String str) {
            this.zzlbh.zzlbe = str;
            return this;
        }

        public final Builder setTotalPrice(String str) {
            this.zzlbh.zzkzr = str;
            return this;
        }

        public final Builder setUnitPrice(String str) {
            this.zzlbh.zzlbf = str;
            return this;
        }
    }

    LineItem() {
        this.zzlbg = 0;
    }

    LineItem(String str, String str2, String str3, String str4, int i, String str5) {
        this.description = str;
        this.zzlbe = str2;
        this.zzlbf = str3;
        this.zzkzr = str4;
        this.zzlbg = i;
        this.zzkzs = str5;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.description, false);
        zzbfp.zza(parcel, 3, this.zzlbe, false);
        zzbfp.zza(parcel, 4, this.zzlbf, false);
        zzbfp.zza(parcel, 5, this.zzkzr, false);
        zzbfp.zzc(parcel, 6, this.zzlbg);
        zzbfp.zza(parcel, 7, this.zzkzs, false);
        zzbfp.zzai(parcel, zze);
    }
}
