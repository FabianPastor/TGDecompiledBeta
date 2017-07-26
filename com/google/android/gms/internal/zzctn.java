package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzctn extends zza implements Result {
    public static final Creator<zzctn> CREATOR = new zzcto();
    private int zzaku;
    private int zzbCR;
    private Intent zzbCS;

    public zzctn() {
        this(0, null);
    }

    zzctn(int i, int i2, Intent intent) {
        this.zzaku = i;
        this.zzbCR = i2;
        this.zzbCS = intent;
    }

    private zzctn(int i, Intent intent) {
        this(2, 0, null);
    }

    public final Status getStatus() {
        return this.zzbCR == 0 ? Status.zzaBm : Status.zzaBq;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zzc(parcel, 2, this.zzbCR);
        zzd.zza(parcel, 3, this.zzbCS, i, false);
        zzd.zzI(parcel, zze);
    }
}
