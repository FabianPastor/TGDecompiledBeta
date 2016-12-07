package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzaub extends zza {
    public static final Creator<zzaub> CREATOR = new zzauc();
    public final String name;
    public final int versionCode;
    public final String zzaFy;
    public final String zzbqQ;
    public final long zzbuZ;
    public final Long zzbva;
    public final Float zzbvb;
    public final Double zzbvc;

    zzaub(int i, String str, long j, Long l, Float f, String str2, String str3, Double d) {
        Double d2 = null;
        this.versionCode = i;
        this.name = str;
        this.zzbuZ = j;
        this.zzbva = l;
        this.zzbvb = null;
        if (i == 1) {
            if (f != null) {
                d2 = Double.valueOf(f.doubleValue());
            }
            this.zzbvc = d2;
        } else {
            this.zzbvc = d;
        }
        this.zzaFy = str2;
        this.zzbqQ = str3;
    }

    zzaub(zzaud com_google_android_gms_internal_zzaud) {
        this(com_google_android_gms_internal_zzaud.mName, com_google_android_gms_internal_zzaud.zzbvd, com_google_android_gms_internal_zzaud.zzYe, com_google_android_gms_internal_zzaud.zzVQ);
    }

    zzaub(String str, long j, Object obj, String str2) {
        zzac.zzdv(str);
        this.versionCode = 2;
        this.name = str;
        this.zzbuZ = j;
        this.zzbqQ = str2;
        if (obj == null) {
            this.zzbva = null;
            this.zzbvb = null;
            this.zzbvc = null;
            this.zzaFy = null;
        } else if (obj instanceof Long) {
            this.zzbva = (Long) obj;
            this.zzbvb = null;
            this.zzbvc = null;
            this.zzaFy = null;
        } else if (obj instanceof String) {
            this.zzbva = null;
            this.zzbvb = null;
            this.zzbvc = null;
            this.zzaFy = (String) obj;
        } else if (obj instanceof Double) {
            this.zzbva = null;
            this.zzbvb = null;
            this.zzbvc = (Double) obj;
            this.zzaFy = null;
        } else {
            throw new IllegalArgumentException("User attribute given of un-supported type");
        }
    }

    public Object getValue() {
        return this.zzbva != null ? this.zzbva : this.zzbvc != null ? this.zzbvc : this.zzaFy != null ? this.zzaFy : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzauc.zza(this, parcel, i);
    }
}
