package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;

public final class Scope extends zza implements ReflectedParcelable {
    public static final Creator<Scope> CREATOR = new zze();
    private final String zzaBl;
    private int zzaku;

    Scope(int i, String str) {
        zzbo.zzh(str, "scopeUri must not be null or empty");
        this.zzaku = i;
        this.zzaBl = str;
    }

    public Scope(String str) {
        this(1, str);
    }

    public final boolean equals(Object obj) {
        return this == obj ? true : !(obj instanceof Scope) ? false : this.zzaBl.equals(((Scope) obj).zzaBl);
    }

    public final int hashCode() {
        return this.zzaBl.hashCode();
    }

    public final String toString() {
        return this.zzaBl;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, this.zzaBl, false);
        zzd.zzI(parcel, zze);
    }

    public final String zzpp() {
        return this.zzaBl;
    }
}
