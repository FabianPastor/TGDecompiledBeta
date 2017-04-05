package com.google.android.gms.maps.model;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import java.util.List;

public final class Polyline {
    private final IPolylineDelegate zzbpP;

    public Polyline(IPolylineDelegate iPolylineDelegate) {
        this.zzbpP = (IPolylineDelegate) zzac.zzw(iPolylineDelegate);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Polyline)) {
            return false;
        }
        try {
            return this.zzbpP.equalsRemote(((Polyline) obj).zzbpP);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int getColor() {
        try {
            return this.zzbpP.getColor();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @NonNull
    public Cap getEndCap() {
        try {
            return this.zzbpP.getEndCap().zzJJ();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public String getId() {
        try {
            return this.zzbpP.getId();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int getJointType() {
        try {
            return this.zzbpP.getJointType();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Nullable
    public List<PatternItem> getPattern() {
        try {
            return PatternItem.zzI(this.zzbpP.getPattern());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public List<LatLng> getPoints() {
        try {
            return this.zzbpP.getPoints();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @NonNull
    public Cap getStartCap() {
        try {
            return this.zzbpP.getStartCap().zzJJ();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Nullable
    public Object getTag() {
        try {
            return zzd.zzF(this.zzbpP.getTag());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getWidth() {
        try {
            return this.zzbpP.getWidth();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public float getZIndex() {
        try {
            return this.zzbpP.getZIndex();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int hashCode() {
        try {
            return this.zzbpP.hashCodeRemote();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isClickable() {
        try {
            return this.zzbpP.isClickable();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isGeodesic() {
        try {
            return this.zzbpP.isGeodesic();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isVisible() {
        try {
            return this.zzbpP.isVisible();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void remove() {
        try {
            this.zzbpP.remove();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setClickable(boolean z) {
        try {
            this.zzbpP.setClickable(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setColor(int i) {
        try {
            this.zzbpP.setColor(i);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setEndCap(@NonNull Cap cap) {
        zzac.zzb((Object) cap, (Object) "endCap must not be null");
        try {
            this.zzbpP.setEndCap(cap);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setGeodesic(boolean z) {
        try {
            this.zzbpP.setGeodesic(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setJointType(int i) {
        try {
            this.zzbpP.setJointType(i);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPattern(@Nullable List<PatternItem> list) {
        try {
            this.zzbpP.setPattern(list);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setPoints(List<LatLng> list) {
        try {
            this.zzbpP.setPoints(list);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setStartCap(@NonNull Cap cap) {
        zzac.zzb((Object) cap, (Object) "startCap must not be null");
        try {
            this.zzbpP.setStartCap(cap);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setTag(@Nullable Object obj) {
        try {
            this.zzbpP.setTag(zzd.zzA(obj));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setVisible(boolean z) {
        try {
            this.zzbpP.setVisible(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setWidth(float f) {
        try {
            this.zzbpP.setWidth(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setZIndex(float f) {
        try {
            this.zzbpP.setZIndex(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
