package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzbt extends zzbfm {
    public static final Creator<zzbt> CREATOR = new zzbu();
    private int zzeck;
    private ConnectionResult zzfoo;
    private boolean zzfri;
    private IBinder zzgbn;
    private boolean zzgbo;

    zzbt(int i, IBinder iBinder, ConnectionResult connectionResult, boolean z, boolean z2) {
        this.zzeck = i;
        this.zzgbn = iBinder;
        this.zzfoo = connectionResult;
        this.zzfri = z;
        this.zzgbo = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbt)) {
            return false;
        }
        zzbt com_google_android_gms_common_internal_zzbt = (zzbt) obj;
        return this.zzfoo.equals(com_google_android_gms_common_internal_zzbt.zzfoo) && zzalp().equals(com_google_android_gms_common_internal_zzbt.zzalp());
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.zzeck);
        zzbfp.zza(parcel, 2, this.zzgbn, false);
        zzbfp.zza(parcel, 3, this.zzfoo, i, false);
        zzbfp.zza(parcel, 4, this.zzfri);
        zzbfp.zza(parcel, 5, this.zzgbo);
        zzbfp.zzai(parcel, zze);
    }

    public final ConnectionResult zzahf() {
        return this.zzfoo;
    }

    public final zzan zzalp() {
        IBinder iBinder = this.zzgbn;
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
        return queryLocalInterface instanceof zzan ? (zzan) queryLocalInterface : new zzap(iBinder);
    }

    public final boolean zzalq() {
        return this.zzfri;
    }

    public final boolean zzalr() {
        return this.zzgbo;
    }
}
