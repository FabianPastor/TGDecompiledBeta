package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbr;

public final class zzctx extends zza {
    public static final Creator<zzctx> CREATOR = new zzcty();
    private final ConnectionResult zzaBQ;
    private int zzaku;
    private final zzbr zzbCV;

    public zzctx(int i) {
        this(new ConnectionResult(8, null), null);
    }

    zzctx(int i, ConnectionResult connectionResult, zzbr com_google_android_gms_common_internal_zzbr) {
        this.zzaku = i;
        this.zzaBQ = connectionResult;
        this.zzbCV = com_google_android_gms_common_internal_zzbr;
    }

    private zzctx(ConnectionResult connectionResult, zzbr com_google_android_gms_common_internal_zzbr) {
        this(1, connectionResult, null);
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzaBQ, i, false);
        zzd.zza(parcel, 3, this.zzbCV, i, false);
        zzd.zzI(parcel, zze);
    }

    public final zzbr zzAx() {
        return this.zzbCV;
    }

    public final ConnectionResult zzpz() {
        return this.zzaBQ;
    }
}
