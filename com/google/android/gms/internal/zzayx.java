package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class zzayx extends zza implements Comparable<zzayx> {
    public static final Creator<zzayx> CREATOR = new zzayy();
    public final String[] zzbBA;
    public final Map<String, zzayz> zzbBB = new TreeMap();
    public final int zzbBy;
    public final zzayz[] zzbBz;

    public zzayx(int i, zzayz[] com_google_android_gms_internal_zzayzArr, String[] strArr) {
        this.zzbBy = i;
        this.zzbBz = com_google_android_gms_internal_zzayzArr;
        for (zzayz com_google_android_gms_internal_zzayz : com_google_android_gms_internal_zzayzArr) {
            this.zzbBB.put(com_google_android_gms_internal_zzayz.name, com_google_android_gms_internal_zzayz);
        }
        this.zzbBA = strArr;
        if (this.zzbBA != null) {
            Arrays.sort(this.zzbBA);
        }
    }

    public /* synthetic */ int compareTo(Object obj) {
        return zza((zzayx) obj);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof zzayx)) {
            return false;
        }
        zzayx com_google_android_gms_internal_zzayx = (zzayx) obj;
        return this.zzbBy == com_google_android_gms_internal_zzayx.zzbBy && zzaa.equal(this.zzbBB, com_google_android_gms_internal_zzayx.zzbBB) && Arrays.equals(this.zzbBA, com_google_android_gms_internal_zzayx.zzbBA);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Configuration(");
        stringBuilder.append(this.zzbBy);
        stringBuilder.append(", ");
        stringBuilder.append("(");
        for (zzayz append : this.zzbBB.values()) {
            stringBuilder.append(append);
            stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        stringBuilder.append(", ");
        stringBuilder.append("(");
        if (this.zzbBA != null) {
            for (String append2 : this.zzbBA) {
                stringBuilder.append(append2);
                stringBuilder.append(", ");
            }
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(")");
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzayy.zza(this, parcel, i);
    }

    public int zza(zzayx com_google_android_gms_internal_zzayx) {
        return this.zzbBy - com_google_android_gms_internal_zzayx.zzbBy;
    }
}
