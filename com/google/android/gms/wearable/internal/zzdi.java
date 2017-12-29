package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.List;

public final class zzdi extends zzbfm {
    public static final Creator<zzdi> CREATOR = new zzdj();
    public final int statusCode;
    public final List<zzah> zzlke;

    public zzdi(int i, List<zzah> list) {
        this.statusCode = i;
        this.zzlke = list;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 2, this.statusCode);
        zzbfp.zzc(parcel, 3, this.zzlke, false);
        zzbfp.zzai(parcel, zze);
    }
}
