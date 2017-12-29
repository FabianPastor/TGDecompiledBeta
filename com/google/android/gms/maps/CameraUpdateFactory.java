package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class CameraUpdateFactory {
    private static ICameraUpdateFactoryDelegate zziqx;

    public static CameraUpdate newLatLng(LatLng latLng) {
        try {
            return new CameraUpdate(zzawa().newLatLng(latLng));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static CameraUpdate newLatLngBounds(LatLngBounds latLngBounds, int i) {
        try {
            return new CameraUpdate(zzawa().newLatLngBounds(latLngBounds, i));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static CameraUpdate newLatLngZoom(LatLng latLng, float f) {
        try {
            return new CameraUpdate(zzawa().newLatLngZoom(latLng, f));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static void zza(ICameraUpdateFactoryDelegate iCameraUpdateFactoryDelegate) {
        zziqx = (ICameraUpdateFactoryDelegate) zzbq.checkNotNull(iCameraUpdateFactoryDelegate);
    }

    private static ICameraUpdateFactoryDelegate zzawa() {
        return (ICameraUpdateFactoryDelegate) zzbq.checkNotNull(zziqx, "CameraUpdateFactory is not initialized");
    }
}
