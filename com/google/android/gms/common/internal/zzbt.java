package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;

public final class zzbt extends zza {
    public static final Creator<zzbt> CREATOR = new zzbu();
    private final int zzaIs;
    private final int zzaIt;
    @Deprecated
    private final Scope[] zzaIu;
    private int zzaku;

    zzbt(int i, int i2, int i3, Scope[] scopeArr) {
        this.zzaku = i;
        this.zzaIs = i2;
        this.zzaIt = i3;
        this.zzaIu = scopeArr;
    }

    public zzbt(int i, int i2, Scope[] scopeArr) {
        this(1, i, i2, null);
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zzc(parcel, 2, this.zzaIs);
        zzd.zzc(parcel, 3, this.zzaIt);
        zzd.zza(parcel, 4, this.zzaIu, i, false);
        zzd.zzI(parcel, zze);
    }
}
