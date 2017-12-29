package com.google.android.gms.maps.model;

import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.maps.model.internal.zzp;

public final class Marker {
    private final zzp zzivf;

    public Marker(zzp com_google_android_gms_maps_model_internal_zzp) {
        this.zzivf = (zzp) zzbq.checkNotNull(com_google_android_gms_maps_model_internal_zzp);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof Marker)) {
            return false;
        }
        try {
            return this.zzivf.zzj(((Marker) obj).zzivf);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final LatLng getPosition() {
        try {
            return this.zzivf.getPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final int hashCode() {
        try {
            return this.zzivf.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setPosition(LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        try {
            this.zzivf.setPosition(latLng);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
