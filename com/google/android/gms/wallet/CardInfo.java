package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class CardInfo extends zzbfm {
    public static final Creator<CardInfo> CREATOR = new zzc();
    private String zzkzh;
    private String zzkzi;
    private String zzkzj;
    private int zzkzk;
    private UserAddress zzkzl;

    private CardInfo() {
    }

    CardInfo(String str, String str2, String str3, int i, UserAddress userAddress) {
        this.zzkzh = str;
        this.zzkzi = str2;
        this.zzkzj = str3;
        this.zzkzk = i;
        this.zzkzl = userAddress;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 1, this.zzkzh, false);
        zzbfp.zza(parcel, 2, this.zzkzi, false);
        zzbfp.zza(parcel, 3, this.zzkzj, false);
        zzbfp.zzc(parcel, 4, this.zzkzk);
        zzbfp.zza(parcel, 5, this.zzkzl, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
