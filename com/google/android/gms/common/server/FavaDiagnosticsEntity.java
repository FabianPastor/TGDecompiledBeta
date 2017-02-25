package com.google.android.gms.common.server;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;

public class FavaDiagnosticsEntity extends zza implements ReflectedParcelable {
    public static final Creator<FavaDiagnosticsEntity> CREATOR = new zza();
    public final String zzaGP;
    public final int zzaGQ;
    final int zzaiI;

    public FavaDiagnosticsEntity(int i, String str, int i2) {
        this.zzaiI = i;
        this.zzaGP = str;
        this.zzaGQ = i2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
