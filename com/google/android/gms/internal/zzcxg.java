package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzcxg extends zzbfm implements Result {
    public static final Creator<zzcxg> CREATOR = new zzcxh();
    private int zzeck;
    private int zzkbx;
    private Intent zzkby;

    public zzcxg() {
        this(0, null);
    }

    zzcxg(int i, int i2, Intent intent) {
        this.zzeck = i;
        this.zzkbx = i2;
        this.zzkby = intent;
    }

    private zzcxg(int i, Intent intent) {
        this(2, 0, null);
    }

    public final Status getStatus() {
        return this.zzkbx == 0 ? Status.zzfni : Status.zzfnm;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.zzeck);
        zzbfp.zzc(parcel, 2, this.zzkbx);
        zzbfp.zza(parcel, 3, this.zzkby, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
