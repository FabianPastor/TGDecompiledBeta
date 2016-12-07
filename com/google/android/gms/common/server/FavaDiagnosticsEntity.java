package com.google.android.gms.common.server;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public class FavaDiagnosticsEntity extends zza implements ReflectedParcelable {
    public static final Creator<FavaDiagnosticsEntity> CREATOR = new zza();
    final int mVersionCode;
    public final String zzaFs;
    public final int zzaFt;

    public FavaDiagnosticsEntity(int i, String str, int i2) {
        this.mVersionCode = i;
        this.zzaFs = str;
        this.zzaFt = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
