package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.widget.RemoteViews;

public final class zzdku extends zzbfm {
    public static final Creator<zzdku> CREATOR = new zzdkv();
    private String[] zzleh;
    private int[] zzlei;
    private RemoteViews zzlej;
    private byte[] zzlek;

    private zzdku() {
    }

    public zzdku(String[] strArr, int[] iArr, RemoteViews remoteViews, byte[] bArr) {
        this.zzleh = strArr;
        this.zzlei = iArr;
        this.zzlej = remoteViews;
        this.zzlek = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 1, this.zzleh, false);
        zzbfp.zza(parcel, 2, this.zzlei, false);
        zzbfp.zza(parcel, 3, this.zzlej, i, false);
        zzbfp.zza(parcel, 4, this.zzlek, false);
        zzbfp.zzai(parcel, zze);
    }
}
