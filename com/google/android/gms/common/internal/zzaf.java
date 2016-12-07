package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;

public class zzaf extends zza {
    public static final Creator<zzaf> CREATOR = new zzag();
    final int mVersionCode;
    IBinder zzaDx;
    private ConnectionResult zzaFh;
    private boolean zzaFi;
    private boolean zzazX;

    zzaf(int i, IBinder iBinder, ConnectionResult connectionResult, boolean z, boolean z2) {
        this.mVersionCode = i;
        this.zzaDx = iBinder;
        this.zzaFh = connectionResult;
        this.zzazX = z;
        this.zzaFi = z2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzaf)) {
            return false;
        }
        zzaf com_google_android_gms_common_internal_zzaf = (zzaf) obj;
        return this.zzaFh.equals(com_google_android_gms_common_internal_zzaf.zzaFh) && zzxz().equals(com_google_android_gms_common_internal_zzaf.zzxz());
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzag.zza(this, parcel, i);
    }

    public ConnectionResult zzxA() {
        return this.zzaFh;
    }

    public boolean zzxB() {
        return this.zzazX;
    }

    public boolean zzxC() {
        return this.zzaFi;
    }

    public zzr zzxz() {
        return zzr.zza.zzbr(this.zzaDx);
    }
}
