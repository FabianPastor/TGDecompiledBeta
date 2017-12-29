package com.google.android.gms.maps;

import android.location.Location;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.internal.zzp;

public final class GoogleMap {
    private final IGoogleMapDelegate zziqy;
    private UiSettings zziqz;

    @Deprecated
    public interface OnMyLocationChangeListener {
        void onMyLocationChange(Location location);
    }

    public GoogleMap(IGoogleMapDelegate iGoogleMapDelegate) {
        this.zziqy = (IGoogleMapDelegate) zzbq.checkNotNull(iGoogleMapDelegate);
    }

    public final Marker addMarker(MarkerOptions markerOptions) {
        try {
            zzp addMarker = this.zziqy.addMarker(markerOptions);
            return addMarker != null ? new Marker(addMarker) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate) {
        try {
            this.zziqy.animateCamera(cameraUpdate.zzavz());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final CameraPosition getCameraPosition() {
        try {
            return this.zziqy.getCameraPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMaxZoomLevel() {
        try {
            return this.zziqy.getMaxZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final UiSettings getUiSettings() {
        try {
            if (this.zziqz == null) {
                this.zziqz = new UiSettings(this.zziqy.getUiSettings());
            }
            return this.zziqz;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void moveCamera(CameraUpdate cameraUpdate) {
        try {
            this.zziqy.moveCamera(cameraUpdate.zzavz());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMapType(int i) {
        try {
            this.zziqy.setMapType(i);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMyLocationEnabled(boolean z) {
        try {
            this.zziqy.setMyLocationEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Deprecated
    public final void setOnMyLocationChangeListener(OnMyLocationChangeListener onMyLocationChangeListener) {
        if (onMyLocationChangeListener == null) {
            try {
                this.zziqy.setOnMyLocationChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.zziqy.setOnMyLocationChangeListener(new zzh(this, onMyLocationChangeListener));
    }

    public final void setPadding(int i, int i2, int i3, int i4) {
        try {
            this.zziqy.setPadding(i, i2, i3, i4);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
