package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.Node;

public final class zzfo extends zzbfm implements Node {
    public static final Creator<zzfo> CREATOR = new zzfp();
    private final String zzbuz;
    private final String zzegt;
    private final int zzlkz;
    private final boolean zzlla;

    public zzfo(String str, String str2, int i, boolean z) {
        this.zzbuz = str;
        this.zzegt = str2;
        this.zzlkz = i;
        this.zzlla = z;
    }

    public final boolean equals(Object obj) {
        return !(obj instanceof zzfo) ? false : ((zzfo) obj).zzbuz.equals(this.zzbuz);
    }

    public final String getDisplayName() {
        return this.zzegt;
    }

    public final String getId() {
        return this.zzbuz;
    }

    public final int hashCode() {
        return this.zzbuz.hashCode();
    }

    public final boolean isNearby() {
        return this.zzlla;
    }

    public final String toString() {
        String str = this.zzegt;
        String str2 = this.zzbuz;
        int i = this.zzlkz;
        return new StringBuilder((String.valueOf(str).length() + 45) + String.valueOf(str2).length()).append("Node{").append(str).append(", id=").append(str2).append(", hops=").append(i).append(", isNearby=").append(this.zzlla).append("}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getId(), false);
        zzbfp.zza(parcel, 3, getDisplayName(), false);
        zzbfp.zzc(parcel, 4, this.zzlkz);
        zzbfp.zza(parcel, 5, isNearby());
        zzbfp.zzai(parcel, zze);
    }
}
