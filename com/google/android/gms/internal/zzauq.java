package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzauq extends zza {
    public static final Creator<zzauq> CREATOR = new zzaur();
    public final String name;
    public final int versionCode;
    public final String zzaGV;
    public final String zzbqZ;
    public final long zzbwg;
    public final Long zzbwh;
    public final Float zzbwi;
    public final Double zzbwj;

    zzauq(int i, String str, long j, Long l, Float f, String str2, String str3, Double d) {
        Double d2 = null;
        this.versionCode = i;
        this.name = str;
        this.zzbwg = j;
        this.zzbwh = l;
        this.zzbwi = null;
        if (i == 1) {
            if (f != null) {
                d2 = Double.valueOf(f.doubleValue());
            }
            this.zzbwj = d2;
        } else {
            this.zzbwj = d;
        }
        this.zzaGV = str2;
        this.zzbqZ = str3;
    }

    zzauq(zzaus com_google_android_gms_internal_zzaus) {
        this(com_google_android_gms_internal_zzaus.mName, com_google_android_gms_internal_zzaus.zzbwk, com_google_android_gms_internal_zzaus.mValue, com_google_android_gms_internal_zzaus.mOrigin);
    }

    zzauq(String str, long j, Object obj, String str2) {
        zzac.zzdr(str);
        this.versionCode = 2;
        this.name = str;
        this.zzbwg = j;
        this.zzbqZ = str2;
        if (obj == null) {
            this.zzbwh = null;
            this.zzbwi = null;
            this.zzbwj = null;
            this.zzaGV = null;
        } else if (obj instanceof Long) {
            this.zzbwh = (Long) obj;
            this.zzbwi = null;
            this.zzbwj = null;
            this.zzaGV = null;
        } else if (obj instanceof String) {
            this.zzbwh = null;
            this.zzbwi = null;
            this.zzbwj = null;
            this.zzaGV = (String) obj;
        } else if (obj instanceof Double) {
            this.zzbwh = null;
            this.zzbwi = null;
            this.zzbwj = (Double) obj;
            this.zzaGV = null;
        } else {
            throw new IllegalArgumentException("User attribute given of un-supported type");
        }
    }

    public Object getValue() {
        return this.zzbwh != null ? this.zzbwh : this.zzbwj != null ? this.zzbwj : this.zzaGV != null ? this.zzaGV : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaur.zza(this, parcel, i);
    }
}
