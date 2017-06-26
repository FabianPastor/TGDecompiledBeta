package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbr extends zza {
    public static final Creator<zzbr> CREATOR = new zzbs();
    private ConnectionResult zzaBQ;
    private boolean zzaDm;
    private IBinder zzaIq;
    private boolean zzaIr;
    private int zzaku;

    zzbr(int i, IBinder iBinder, ConnectionResult connectionResult, boolean z, boolean z2) {
        this.zzaku = i;
        this.zzaIq = iBinder;
        this.zzaBQ = connectionResult;
        this.zzaDm = z;
        this.zzaIr = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzbr)) {
            return false;
        }
        zzbr com_google_android_gms_common_internal_zzbr = (zzbr) obj;
        return this.zzaBQ.equals(com_google_android_gms_common_internal_zzbr.zzaBQ) && zzrH().equals(com_google_android_gms_common_internal_zzbr.zzrH());
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzaIq, false);
        zzd.zza(parcel, 3, this.zzaBQ, i, false);
        zzd.zza(parcel, 4, this.zzaDm);
        zzd.zza(parcel, 5, this.zzaIr);
        zzd.zzI(parcel, zze);
    }

    public final ConnectionResult zzpz() {
        return this.zzaBQ;
    }

    public final zzal zzrH() {
        IBinder iBinder = this.zzaIq;
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
        return queryLocalInterface instanceof zzal ? (zzal) queryLocalInterface : new zzan(iBinder);
    }

    public final boolean zzrI() {
        return this.zzaDm;
    }

    public final boolean zzrJ() {
        return this.zzaIr;
    }
}
