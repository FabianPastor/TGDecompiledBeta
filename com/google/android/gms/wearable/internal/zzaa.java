package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class zzaa extends zza implements CapabilityInfo {
    public static final Creator<zzaa> CREATOR = new zzab();
    private final Object mLock = new Object();
    private final String mName;
    private Set<Node> zzbRY;
    private final List<zzeg> zzbSb;

    public zzaa(String str, List<zzeg> list) {
        this.mName = str;
        this.zzbSb = list;
        this.zzbRY = null;
        zzbo.zzu(this.mName);
        zzbo.zzu(this.zzbSb);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzaa com_google_android_gms_wearable_internal_zzaa = (zzaa) obj;
        if (this.mName == null ? com_google_android_gms_wearable_internal_zzaa.mName != null : !this.mName.equals(com_google_android_gms_wearable_internal_zzaa.mName)) {
            return false;
        }
        if (this.zzbSb != null) {
            if (this.zzbSb.equals(com_google_android_gms_wearable_internal_zzaa.zzbSb)) {
                return true;
            }
        } else if (com_google_android_gms_wearable_internal_zzaa.zzbSb == null) {
            return true;
        }
        return false;
    }

    public final String getName() {
        return this.mName;
    }

    public final Set<Node> getNodes() {
        Set<Node> set;
        synchronized (this.mLock) {
            if (this.zzbRY == null) {
                this.zzbRY = new HashSet(this.zzbSb);
            }
            set = this.zzbRY;
        }
        return set;
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.mName != null ? this.mName.hashCode() : 0) + 31) * 31;
        if (this.zzbSb != null) {
            i = this.zzbSb.hashCode();
        }
        return hashCode + i;
    }

    public final String toString() {
        String str = this.mName;
        String valueOf = String.valueOf(this.zzbSb);
        return new StringBuilder((String.valueOf(str).length() + 18) + String.valueOf(valueOf).length()).append("CapabilityInfo{").append(str).append(", ").append(valueOf).append("}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, getName(), false);
        zzd.zzc(parcel, 3, this.zzbSb, false);
        zzd.zzI(parcel, zze);
    }
}