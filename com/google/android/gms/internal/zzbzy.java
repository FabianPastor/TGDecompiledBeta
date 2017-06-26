package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzy extends zzbzt<String> {
    public zzbzy(int i, String str, String str2) {
        super(0, str, str2);
    }

    private final String zze(zzcab com_google_android_gms_internal_zzcab) {
        try {
            return com_google_android_gms_internal_zzcab.getStringFlagValue(getKey(), (String) zzdI(), getSource());
        } catch (RemoteException e) {
            return (String) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcab com_google_android_gms_internal_zzcab) {
        return zze(com_google_android_gms_internal_zzcab);
    }
}
