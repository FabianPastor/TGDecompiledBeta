package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzv extends zzbzt<Boolean> {
    public zzbzv(int i, String str, Boolean bool) {
        super(0, str, bool);
    }

    private final Boolean zzb(zzcab com_google_android_gms_internal_zzcab) {
        try {
            return Boolean.valueOf(com_google_android_gms_internal_zzcab.getBooleanFlagValue(getKey(), ((Boolean) zzdI()).booleanValue(), getSource()));
        } catch (RemoteException e) {
            return (Boolean) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcab com_google_android_gms_internal_zzcab) {
        return zzb(com_google_android_gms_internal_zzcab);
    }
}
