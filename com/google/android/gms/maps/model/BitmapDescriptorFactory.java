package com.google.android.gms.maps.model;

import android.graphics.Bitmap;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.maps.model.internal.zza;

public final class BitmapDescriptorFactory {
    private static zza zziud;

    public static BitmapDescriptor fromBitmap(Bitmap bitmap) {
        try {
            return new BitmapDescriptor(zzawe().zzd(bitmap));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static BitmapDescriptor fromResource(int i) {
        try {
            return new BitmapDescriptor(zzawe().zzea(i));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static void zza(zza com_google_android_gms_maps_model_internal_zza) {
        if (zziud == null) {
            zziud = (zza) zzbq.checkNotNull(com_google_android_gms_maps_model_internal_zza);
        }
    }

    private static zza zzawe() {
        return (zza) zzbq.checkNotNull(zziud, "IBitmapDescriptorFactory is not initialized");
    }
}
