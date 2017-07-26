package com.google.android.gms.internal;

import android.os.RemoteException;

public final class zzbzz extends zzbzu<String> {
    public zzbzz(int i, String str, String str2) {
        super(0, str, str2);
    }

    private final String zze(zzcac com_google_android_gms_internal_zzcac) {
        try {
            return com_google_android_gms_internal_zzcac.getStringFlagValue(getKey(), (String) zzdI(), getSource());
        } catch (RemoteException e) {
            return (String) zzdI();
        }
    }

    public final /* synthetic */ Object zza(zzcac com_google_android_gms_internal_zzcac) {
        return zze(com_google_android_gms_internal_zzcac);
    }
}
