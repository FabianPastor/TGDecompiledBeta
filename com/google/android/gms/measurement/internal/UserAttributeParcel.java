package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;

public class UserAttributeParcel extends AbstractSafeParcelable {
    public static final Creator<UserAttributeParcel> CREATOR = new zzaj();
    public final String Fe;
    public final String arK;
    public final long avT;
    public final Long avU;
    public final Float avV;
    public final Double avW;
    public final String name;
    public final int versionCode;

    UserAttributeParcel(int i, String str, long j, Long l, Float f, String str2, String str3, Double d) {
        Double d2 = null;
        this.versionCode = i;
        this.name = str;
        this.avT = j;
        this.avU = l;
        this.avV = null;
        if (i == 1) {
            if (f != null) {
                d2 = Double.valueOf(f.doubleValue());
            }
            this.avW = d2;
        } else {
            this.avW = d;
        }
        this.Fe = str2;
        this.arK = str3;
    }

    UserAttributeParcel(zzak com_google_android_gms_measurement_internal_zzak) {
        this(com_google_android_gms_measurement_internal_zzak.mName, com_google_android_gms_measurement_internal_zzak.avX, com_google_android_gms_measurement_internal_zzak.zzcyd, com_google_android_gms_measurement_internal_zzak.zzctj);
    }

    UserAttributeParcel(String str, long j, Object obj, String str2) {
        zzaa.zzib(str);
        this.versionCode = 2;
        this.name = str;
        this.avT = j;
        this.arK = str2;
        if (obj == null) {
            this.avU = null;
            this.avV = null;
            this.avW = null;
            this.Fe = null;
        } else if (obj instanceof Long) {
            this.avU = (Long) obj;
            this.avV = null;
            this.avW = null;
            this.Fe = null;
        } else if (obj instanceof String) {
            this.avU = null;
            this.avV = null;
            this.avW = null;
            this.Fe = (String) obj;
        } else if (obj instanceof Double) {
            this.avU = null;
            this.avV = null;
            this.avW = (Double) obj;
            this.Fe = null;
        } else {
            throw new IllegalArgumentException("User attribute given of un-supported type");
        }
    }

    public Object getValue() {
        return this.avU != null ? this.avU : this.avW != null ? this.avW : this.Fe != null ? this.Fe : null;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzaj.zza(this, parcel, i);
    }
}
