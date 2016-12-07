package com.google.android.gms.maps;

import android.graphics.Point;
import android.os.RemoteException;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.VisibleRegion;

public final class Projection {
    private final IProjectionDelegate alP;

    Projection(IProjectionDelegate iProjectionDelegate) {
        this.alP = iProjectionDelegate;
    }

    public LatLng fromScreenLocation(Point point) {
        try {
            return this.alP.fromScreenLocation(zze.zzac(point));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public VisibleRegion getVisibleRegion() {
        try {
            return this.alP.getVisibleRegion();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public Point toScreenLocation(LatLng latLng) {
        try {
            return (Point) zze.zzae(this.alP.toScreenLocation(latLng));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
