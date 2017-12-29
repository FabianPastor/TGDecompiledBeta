package com.google.android.gms.wallet.wobs;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class LabelValue extends zzbfm {
    public static final Creator<LabelValue> CREATOR = new zzc();
    private String label;
    private String value;

    LabelValue() {
    }

    public LabelValue(String str, String str2) {
        this.label = str;
        this.value = str2;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.label, false);
        zzbfp.zza(parcel, 3, this.value, false);
        zzbfp.zzai(parcel, zze);
    }
}
