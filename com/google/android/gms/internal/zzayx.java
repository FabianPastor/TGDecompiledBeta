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
    public final int zzbBB;
    public final zzayz[] zzbBC;
    public final String[] zzbBD;
    public final Map<String, zzayz> zzbBE = new TreeMap();

    public zzayx(int i, zzayz[] com_google_android_gms_internal_zzayzArr, String[] strArr) {
        this.zzbBB = i;
        this.zzbBC = com_google_android_gms_internal_zzayzArr;
        for (zzayz com_google_android_gms_internal_zzayz : com_google_android_gms_internal_zzayzArr) {
            this.zzbBE.put(com_google_android_gms_internal_zzayz.name, com_google_android_gms_internal_zzayz);
        }
        this.zzbBD = strArr;
        if (this.zzbBD != null) {
            Arrays.sort(this.zzbBD);
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
        return this.zzbBB == com_google_android_gms_internal_zzayx.zzbBB && zzaa.equal(this.zzbBE, com_google_android_gms_internal_zzayx.zzbBE) && Arrays.equals(this.zzbBD, com_google_android_gms_internal_zzayx.zzbBD);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Configuration(");
        stringBuilder.append(this.zzbBB);
        stringBuilder.append(", ");
        stringBuilder.append("(");
        for (zzayz append : this.zzbBE.values()) {
            stringBuilder.append(append);
            stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        stringBuilder.append(", ");
        stringBuilder.append("(");
        if (this.zzbBD != null) {
            for (String append2 : this.zzbBD) {
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
        return this.zzbBB - com_google_android_gms_internal_zzayx.zzbBB;
    }
}
