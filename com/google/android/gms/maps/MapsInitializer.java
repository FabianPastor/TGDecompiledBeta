package com.google.android.gms.maps;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.internal.zzc;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class MapsInitializer {
    private static boolean zzpT = false;

    private MapsInitializer() {
    }

    public static synchronized int initialize(Context context) {
        int i = 0;
        synchronized (MapsInitializer.class) {
            zzac.zzb((Object) context, (Object) "Context is null");
            if (!zzpT) {
                try {
                    zza(zzai.zzbI(context));
                    zzpT = true;
                } catch (GooglePlayServicesNotAvailableException e) {
                    i = e.errorCode;
                }
            }
        }
        return i;
    }

    public static void zza(zzc com_google_android_gms_maps_internal_zzc) {
        try {
            CameraUpdateFactory.zza(com_google_android_gms_maps_internal_zzc.zzJD());
            BitmapDescriptorFactory.zza(com_google_android_gms_maps_internal_zzc.zzJE());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
