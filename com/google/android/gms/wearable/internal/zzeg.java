package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.wearable.Node;

public final class zzeg extends zza implements Node {
    public static final Creator<zzeg> CREATOR = new zzeh();
    private final String zzIk;
    private final String zzalP;
    private final int zzbSY;
    private final boolean zzbSZ;

    public zzeg(String str, String str2, int i, boolean z) {
        this.zzIk = str;
        this.zzalP = str2;
        this.zzbSY = i;
        this.zzbSZ = z;
    }

    public final boolean equals(Object obj) {
        return !(obj instanceof zzeg) ? false : ((zzeg) obj).zzIk.equals(this.zzIk);
    }

    public final String getDisplayName() {
        return this.zzalP;
    }

    public final String getId() {
        return this.zzIk;
    }

    public final int hashCode() {
        return this.zzIk.hashCode();
    }

    public final boolean isNearby() {
        return this.zzbSZ;
    }

    public final String toString() {
        String str = this.zzalP;
        String str2 = this.zzIk;
        int i = this.zzbSY;
        return new StringBuilder((String.valueOf(str).length() + 45) + String.valueOf(str2).length()).append("Node{").append(str).append(", id=").append(str2).append(", hops=").append(i).append(", isNearby=").append(this.zzbSZ).append("}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getId(), false);
        zzd.zza(parcel, 3, getDisplayName(), false);
        zzd.zzc(parcel, 4, this.zzbSY);
        zzd.zza(parcel, 5, isNearby());
        zzd.zzI(parcel, zze);
    }
}
