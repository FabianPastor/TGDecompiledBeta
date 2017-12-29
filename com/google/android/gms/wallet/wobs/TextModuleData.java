package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class TextModuleData extends zzbfm {
    public static final Creator<TextModuleData> CREATOR = new zzj();
    private String body;
    private String zzlgd;

    TextModuleData() {
    }

    public TextModuleData(String str, String str2) {
        this.zzlgd = str;
        this.body = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlgd, false);
        zzbfp.zza(parcel, 3, this.body, false);
        zzbfp.zzai(parcel, zze);
    }
}
