package com.google.android.gms.internal;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public abstract class zzbgk extends zzbgh implements SafeParcelable {
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
        zzbgh com_google_android_gms_internal_zzbgh = (zzbgh) obj;
        for (zzbgi com_google_android_gms_internal_zzbgi : zzrL().values()) {
            if (zza(com_google_android_gms_internal_zzbgi)) {
                if (!com_google_android_gms_internal_zzbgh.zza(com_google_android_gms_internal_zzbgi)) {
                    return false;
                }
                if (!zzb(com_google_android_gms_internal_zzbgi).equals(com_google_android_gms_internal_zzbgh.zzb(com_google_android_gms_internal_zzbgi))) {
                    return false;
                }
            } else if (com_google_android_gms_internal_zzbgh.zza(com_google_android_gms_internal_zzbgi)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int i = 0;
        for (zzbgi com_google_android_gms_internal_zzbgi : zzrL().values()) {
            int hashCode;
            if (zza(com_google_android_gms_internal_zzbgi)) {
                hashCode = zzb(com_google_android_gms_internal_zzbgi).hashCode() + (i * 31);
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
