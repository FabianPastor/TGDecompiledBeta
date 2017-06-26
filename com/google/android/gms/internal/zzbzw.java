package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzw extends zzbzt<Integer> {
    public zzbzw(int i, String str, Integer num) {
        super(0, str, num);
    }

    private final Integer zzc(zzcab com_google_android_gms_internal_zzcab) {
        try {
            return Integer.valueOf(com_google_android_gms_internal_zzcab.getIntFlagValue(getKey(), ((Integer) zzdI()).intValue(), getSource()));
        } catch (RemoteException e) {
            return (Integer) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcab com_google_android_gms_internal_zzcab) {
        return zzc(com_google_android_gms_internal_zzcab);
    }
}
