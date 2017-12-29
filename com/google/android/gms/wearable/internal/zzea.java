package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.List;

public final class zzea extends zzbfm {
    public static final Creator<zzea> CREATOR = new zzeb();
    public final int statusCode;
    public final List<zzfo> zzlkm;

    public zzea(int i, List<zzfo> list) {
        this.statusCode = i;
        this.zzlkm = list;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zzc(parcel, 3, this.zzlkm, false);
        zzbfp.zzai(parcel, zze);
    }
}
