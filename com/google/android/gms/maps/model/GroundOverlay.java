package com.google.android.gms.maps.model;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.model.internal.zzc;

public final class GroundOverlay {
    private final zzc zzbpm;

    public GroundOverlay(zzc com_google_android_gms_maps_model_internal_zzc) {
        this.zzbpm = (zzc) zzac.zzw(com_google_android_gms_maps_model_internal_zzc);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof GroundOverlay)) {
            return false;
        }
        try {
            return this.zzbpm.zzb(((GroundOverlay) obj).zzbpm);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getBearing() {
        try {
            return this.zzbpm.getBearing();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public LatLngBounds getBounds() {
        try {
            return this.zzbpm.getBounds();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getHeight() {
        try {
            return this.zzbpm.getHeight();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public String getId() {
        try {
            return this.zzbpm.getId();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public LatLng getPosition() {
        try {
            return this.zzbpm.getPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public Object getTag() {
        try {
            return zzd.zzF(this.zzbpm.getTag());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getTransparency() {
        try {
            return this.zzbpm.getTransparency();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getWidth() {
        try {
            return this.zzbpm.getWidth();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getZIndex() {
        try {
            return this.zzbpm.getZIndex();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int hashCode() {
        try {
            return this.zzbpm.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isClickable() {
        try {
            return this.zzbpm.isClickable();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isVisible() {
        try {
            return this.zzbpm.isVisible();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void remove() {
        try {
            this.zzbpm.remove();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setBearing(float f) {
        try {
            this.zzbpm.setBearing(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setClickable(boolean z) {
        try {
            this.zzbpm.setClickable(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setDimensions(float f) {
        try {
            this.zzbpm.setDimensions(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setDimensions(float f, float f2) {
        try {
            this.zzbpm.zzg(f, f2);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setImage(@NonNull BitmapDescriptor bitmapDescriptor) {
        zzac.zzb((Object) bitmapDescriptor, (Object) "imageDescriptor must not be null");
        try {
            this.zzbpm.zzL(bitmapDescriptor.zzJm());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPosition(LatLng latLng) {
        try {
            this.zzbpm.setPosition(latLng);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPositionFromBounds(LatLngBounds latLngBounds) {
        try {
            this.zzbpm.setPositionFromBounds(latLngBounds);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setTag(Object obj) {
        try {
            this.zzbpm.setTag(zzd.zzA(obj));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setTransparency(float f) {
        try {
            this.zzbpm.setTransparency(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setVisible(boolean z) {
        try {
            this.zzbpm.setVisible(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setZIndex(float f) {
        try {
            this.zzbpm.setZIndex(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
