package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public final class Scope extends zza implements ReflectedParcelable {
    public static final Creator<Scope> CREATOR = new zzg();
    final int mVersionCode;
    private final String zzayg;

    Scope(int i, String str) {
        zzac.zzh(str, "scopeUri must not be null or empty");
        this.mVersionCode = i;
        this.zzayg = str;
    }

    public Scope(String str) {
        this(1, str);
    }

    public boolean equals(Object obj) {
        return this == obj ? true : !(obj instanceof Scope) ? false : this.zzayg.equals(((Scope) obj).zzayg);
    }

    public int hashCode() {
        return this.zzayg.hashCode();
    }

    public String toString() {
        return this.zzayg;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    public String zzuS() {
        return this.zzayg;
    }
}
