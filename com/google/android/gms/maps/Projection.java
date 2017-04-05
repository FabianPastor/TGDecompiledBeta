package com.google.android.gms.maps;

import android.graphics.Point;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.VisibleRegion;

public final class Projection {
    private final IProjectionDelegate zzboz;

    Projection(IProjectionDelegate iProjectionDelegate) {
        this.zzboz = iProjectionDelegate;
    }

    public LatLng fromScreenLocation(Point point) {
        try {
            return this.zzboz.fromScreenLocation(zzd.zzA(point));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public VisibleRegion getVisibleRegion() {
        try {
            return this.zzboz.getVisibleRegion();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public Point toScreenLocation(LatLng latLng) {
        try {
            return (Point) zzd.zzF(this.zzboz.toScreenLocation(latLng));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
