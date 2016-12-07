package com.google.android.gms.common.api;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;

public final class Scope extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Creator<Scope> CREATOR = new zzg();
    final int mVersionCode;
    private final String vX;

    Scope(int i, String str) {
        zzac.zzh(str, "scopeUri must not be null or empty");
        this.mVersionCode = i;
        this.vX = str;
    }

    public Scope(String str) {
        this(1, str);
    }

    public boolean equals(Object obj) {
        return this == obj ? true : !(obj instanceof Scope) ? false : this.vX.equals(((Scope) obj).vX);
    }

    public int hashCode() {
        return this.vX.hashCode();
    }

    public String toString() {
        return this.vX;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    public String zzaqg() {
        return this.vX;
    }
}
