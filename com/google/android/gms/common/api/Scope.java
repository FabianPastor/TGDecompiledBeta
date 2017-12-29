package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class Scope extends zzbfm implements ReflectedParcelable {
    public static final Creator<Scope> CREATOR = new zzf();
    private int zzeck;
    private final String zzfnh;

    Scope(int i, String str) {
        zzbq.zzh(str, "scopeUri must not be null or empty");
        this.zzeck = i;
        this.zzfnh = str;
    }

    public Scope(String str) {
        this(1, str);
    }

    public final boolean equals(Object obj) {
        return this == obj ? true : !(obj instanceof Scope) ? false : this.zzfnh.equals(((Scope) obj).zzfnh);
    }

    public final int hashCode() {
        return this.zzfnh.hashCode();
    }

    public final String toString() {
        return this.zzfnh;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.zzeck);
        zzbfp.zza(parcel, 2, this.zzfnh, false);
        zzbfp.zzai(parcel, zze);
    }
}
