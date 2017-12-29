package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class WalletObjectMessage extends zzbfm {
    public static final Creator<WalletObjectMessage> CREATOR = new zzn();
    String body;
    String zzlgd;
    TimeInterval zzlgg;
    UriData zzlgh;
    UriData zzlgi;

    WalletObjectMessage() {
    }

    WalletObjectMessage(String str, String str2, TimeInterval timeInterval, UriData uriData, UriData uriData2) {
        this.zzlgd = str;
        this.body = str2;
        this.zzlgg = timeInterval;
        this.zzlgh = uriData;
        this.zzlgi = uriData2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlgd, false);
        zzbfp.zza(parcel, 3, this.body, false);
        zzbfp.zza(parcel, 4, this.zzlgg, i, false);
        zzbfp.zza(parcel, 5, this.zzlgh, i, false);
        zzbfp.zza(parcel, 6, this.zzlgi, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
