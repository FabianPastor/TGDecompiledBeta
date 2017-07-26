package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzw extends zzbzu<Boolean> {
    public zzbzw(int i, String str, Boolean bool) {
        super(0, str, bool);
    }

    private final Boolean zzb(zzcac com_google_android_gms_internal_zzcac) {
        try {
            return Boolean.valueOf(com_google_android_gms_internal_zzcac.getBooleanFlagValue(getKey(), ((Boolean) zzdI()).booleanValue(), getSource()));
        } catch (RemoteException e) {
            return (Boolean) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcac com_google_android_gms_internal_zzcac) {
        return zzb(com_google_android_gms_internal_zzcac);
    }
}
