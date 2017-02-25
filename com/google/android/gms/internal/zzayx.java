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
    public final int zzbBC;
    public final zzayz[] zzbBD;
    public final String[] zzbBE;
    public final Map<String, zzayz> zzbBF = new TreeMap();

    public zzayx(int i, zzayz[] com_google_android_gms_internal_zzayzArr, String[] strArr) {
        this.zzbBC = i;
        this.zzbBD = com_google_android_gms_internal_zzayzArr;
        for (zzayz com_google_android_gms_internal_zzayz : com_google_android_gms_internal_zzayzArr) {
            this.zzbBF.put(com_google_android_gms_internal_zzayz.name, com_google_android_gms_internal_zzayz);
        }
        this.zzbBE = strArr;
        if (this.zzbBE != null) {
            Arrays.sort(this.zzbBE);
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
        return this.zzbBC == com_google_android_gms_internal_zzayx.zzbBC && zzaa.equal(this.zzbBF, com_google_android_gms_internal_zzayx.zzbBF) && Arrays.equals(this.zzbBE, com_google_android_gms_internal_zzayx.zzbBE);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Configuration(");
        stringBuilder.append(this.zzbBC);
        stringBuilder.append(", ");
        stringBuilder.append("(");
        for (zzayz append : this.zzbBF.values()) {
            stringBuilder.append(append);
            stringBuilder.append(", ");
        }
        stringBuilder.append(")");
        stringBuilder.append(", ");
        stringBuilder.append("(");
        if (this.zzbBE != null) {
            for (String append2 : this.zzbBE) {
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
        return this.zzbBC - com_google_android_gms_internal_zzayx.zzbBC;
    }
}
