package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzx extends zzbzt<Long> {
    public zzbzx(int i, String str, Long l) {
        super(0, str, l);
    }

    private final Long zzd(zzcab com_google_android_gms_internal_zzcab) {
        try {
            return Long.valueOf(com_google_android_gms_internal_zzcab.getLongFlagValue(getKey(), ((Long) zzdI()).longValue(), getSource()));
        } catch (RemoteException e) {
            return (Long) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcab com_google_android_gms_internal_zzcab) {
        return zzd(com_google_android_gms_internal_zzcab);
    }
}
