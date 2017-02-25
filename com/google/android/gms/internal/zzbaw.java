package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaf;

public class zzbaw extends zza {
    public static final Creator<zzbaw> CREATOR = new zzbax();
    private final ConnectionResult zzaGE;
    final int zzaiI;
    private final zzaf zzbEy;

    public zzbaw(int i) {
        this(new ConnectionResult(i, null), null);
    }

    zzbaw(int i, ConnectionResult connectionResult, zzaf com_google_android_gms_common_internal_zzaf) {
        this.zzaiI = i;
        this.zzaGE = connectionResult;
        this.zzbEy = com_google_android_gms_common_internal_zzaf;
    }

    public zzbaw(ConnectionResult connectionResult, zzaf com_google_android_gms_common_internal_zzaf) {
        this(1, connectionResult, com_google_android_gms_common_internal_zzaf);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbax.zza(this, parcel, i);
    }

    public zzaf zzPT() {
        return this.zzbEy;
    }

    public ConnectionResult zzyh() {
        return this.zzaGE;
    }
}
