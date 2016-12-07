package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;

public class UserAttributeParcel extends AbstractSafeParcelable {
    public static final zzaj CREATOR = new zzaj();
    public final String Dr;
    public final String aoA;
    public final long asu;
    public final Long asv;
    public final Float asw;
    public final Double asx;
    public final String name;
    public final int versionCode;

    UserAttributeParcel(int i, String str, long j, Long l, Float f, String str2, String str3, Double d) {
        Double d2 = null;
        this.versionCode = i;
        this.name = str;
        this.asu = j;
        this.asv = l;
        this.asw = null;
        if (i == 1) {
            if (f != null) {
                d2 = Double.valueOf(f.doubleValue());
            }
            this.asx = d2;
        } else {
            this.asx = d;
        }
        this.Dr = str2;
        this.aoA = str3;
    }

    UserAttributeParcel(zzak com_google_android_gms_measurement_internal_zzak) {
        this(com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.asy, com_google_android_gms_measurement_internal_zzak.zzctv, com_google_android_gms_measurement_internal_zzak.zzcpe);
    }

    UserAttributeParcel(String str, long j, Object obj, String str2) {
        zzac.zzhz(str);
        this.versionCode = 2;
        this.name = str;
        this.asu = j;
        this.aoA = str2;
        if (obj == null) {
            this.asv = null;
            this.asw = null;
            this.asx = null;
            this.Dr = null;
        } else if (obj instanceof Long) {
            this.asv = (Long) obj;
            this.asw = null;
            this.asx = null;
            this.Dr = null;
        } else if (obj instanceof String) {
            this.asv = null;
            this.asw = null;
            this.asx = null;
            this.Dr = (String) obj;
        } else if (obj instanceof Double) {
            this.asv = null;
            this.asw = null;
            this.asx = (Double) obj;
            this.Dr = null;
        } else {
            throw new IllegalArgumentException("User attribute given of un-supported type");
        }
    }

    public Object getValue() {
        return this.asv != null ? this.asv : this.asx != null ? this.asx : this.Dr != null ? this.Dr : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaj.zza(this, parcel, i);
    }
}
