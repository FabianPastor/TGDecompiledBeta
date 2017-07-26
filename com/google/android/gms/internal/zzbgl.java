package com.google.android.gms.internal;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public abstract class zzbgl extends zzbgi implements SafeParcelable {
    public final int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!getClass().isInstance(obj)) {
            return false;
        }
        zzbgi com_google_android_gms_internal_zzbgi = (zzbgi) obj;
        for (zzbgj com_google_android_gms_internal_zzbgj : zzrL().values()) {
            if (zza(com_google_android_gms_internal_zzbgj)) {
                if (!com_google_android_gms_internal_zzbgi.zza(com_google_android_gms_internal_zzbgj)) {
                    return false;
                }
                if (!zzb(com_google_android_gms_internal_zzbgj).equals(com_google_android_gms_internal_zzbgi.zzb(com_google_android_gms_internal_zzbgj))) {
                    return false;
                }
            } else if (com_google_android_gms_internal_zzbgi.zza(com_google_android_gms_internal_zzbgj)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        for (zzbgj com_google_android_gms_internal_zzbgj : zzrL().values()) {
            int hashCode;
            if (zza(com_google_android_gms_internal_zzbgj)) {
                hashCode = zzb(com_google_android_gms_internal_zzbgj).hashCode() + (i * 31);
            } else {
                hashCode = i;
            }
            i = hashCode;
        }
        return i;
    }

    public Object zzcH(String str) {
        return null;
    }

    public boolean zzcI(String str) {
        return false;
    }
}
