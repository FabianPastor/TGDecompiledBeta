package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzx extends zzbzu<Integer> {
    public zzbzx(int i, String str, Integer num) {
        super(0, str, num);
    }

    private final Integer zzc(zzcac com_google_android_gms_internal_zzcac) {
        try {
            return Integer.valueOf(com_google_android_gms_internal_zzcac.getIntFlagValue(getKey(), ((Integer) zzdI()).intValue(), getSource()));
        } catch (RemoteException e) {
            return (Integer) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcac com_google_android_gms_internal_zzcac) {
        return zzc(com_google_android_gms_internal_zzcac);
    }
}
