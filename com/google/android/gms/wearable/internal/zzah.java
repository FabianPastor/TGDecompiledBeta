package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class zzah extends zzbfm implements CapabilityInfo {
    public static final Creator<zzah> CREATOR = new zzai();
    private final Object mLock = new Object();
    private final String mName;
    private Set<Node> zzlio;
    private final List<zzfo> zzliu;

    public zzah(String str, List<zzfo> list) {
        this.mName = str;
        this.zzliu = list;
        this.zzlio = null;
        zzbq.checkNotNull(this.mName);
        zzbq.checkNotNull(this.zzliu);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzah com_google_android_gms_wearable_internal_zzah = (zzah) obj;
        if (this.mName == null ? com_google_android_gms_wearable_internal_zzah.mName != null : !this.mName.equals(com_google_android_gms_wearable_internal_zzah.mName)) {
            return false;
        }
        if (this.zzliu != null) {
            if (this.zzliu.equals(com_google_android_gms_wearable_internal_zzah.zzliu)) {
                return true;
            }
        } else if (com_google_android_gms_wearable_internal_zzah.zzliu == null) {
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
            if (this.zzlio == null) {
                this.zzlio = new HashSet(this.zzliu);
            }
            set = this.zzlio;
        }
        return set;
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = ((this.mName != null ? this.mName.hashCode() : 0) + 31) * 31;
        if (this.zzliu != null) {
            i = this.zzliu.hashCode();
        }
        return hashCode + i;
    }

    public final String toString() {
        String str = this.mName;
        String valueOf = String.valueOf(this.zzliu);
        return new StringBuilder((String.valueOf(str).length() + 18) + String.valueOf(valueOf).length()).append("CapabilityInfo{").append(str).append(", ").append(valueOf).append("}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getName(), false);
        zzbfp.zzc(parcel, 3, this.zzliu, false);
        zzbfp.zzai(parcel, zze);
    }
}
