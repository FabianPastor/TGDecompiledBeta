package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.ArrayList;

public final class IsReadyToPayRequest extends zzbfm {
    public static final Creator<IsReadyToPayRequest> CREATOR = new zzr();
    ArrayList<Integer> zzkzm;
    private String zzlaz;
    private String zzlba;
    ArrayList<Integer> zzlbb;
    boolean zzlbc;

    public final class Builder {
        private /* synthetic */ IsReadyToPayRequest zzlbd;

        private Builder(IsReadyToPayRequest isReadyToPayRequest) {
            this.zzlbd = isReadyToPayRequest;
        }

        public final IsReadyToPayRequest build() {
            return this.zzlbd;
        }
    }

    IsReadyToPayRequest() {
    }

    IsReadyToPayRequest(ArrayList<Integer> arrayList, String str, String str2, ArrayList<Integer> arrayList2, boolean z) {
        this.zzkzm = arrayList;
        this.zzlaz = str;
        this.zzlba = str2;
        this.zzlbb = arrayList2;
        this.zzlbc = z;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzkzm, false);
        zzbfp.zza(parcel, 4, this.zzlaz, false);
        zzbfp.zza(parcel, 5, this.zzlba, false);
        zzbfp.zza(parcel, 6, this.zzlbb, false);
        zzbfp.zza(parcel, 7, this.zzlbc);
        zzbfp.zzai(parcel, zze);
    }
}
