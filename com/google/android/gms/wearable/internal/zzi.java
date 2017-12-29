package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class zzi extends zzbfm {
    public static final Creator<zzi> CREATOR = new zzj();
    private final String mValue;
    private byte zzlib;
    private final byte zzlic;

    public zzi(byte b, byte b2, String str) {
        this.zzlib = b;
        this.zzlic = b2;
        this.mValue = str;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzi com_google_android_gms_wearable_internal_zzi = (zzi) obj;
        return this.zzlib != com_google_android_gms_wearable_internal_zzi.zzlib ? false : this.zzlic != com_google_android_gms_wearable_internal_zzi.zzlic ? false : this.mValue.equals(com_google_android_gms_wearable_internal_zzi.mValue);
    }

    public final int hashCode() {
        return ((((this.zzlib + 31) * 31) + this.zzlic) * 31) + this.mValue.hashCode();
    }

    public final String toString() {
        byte b = this.zzlib;
        byte b2 = this.zzlic;
        String str = this.mValue;
        return new StringBuilder(String.valueOf(str).length() + 73).append("AmsEntityUpdateParcelable{, mEntityId=").append(b).append(", mAttributeId=").append(b2).append(", mValue='").append(str).append("'}").toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlib);
        zzbfp.zza(parcel, 3, this.zzlic);
        zzbfp.zza(parcel, 4, this.mValue, false);
        zzbfp.zzai(parcel, zze);
    }
}
