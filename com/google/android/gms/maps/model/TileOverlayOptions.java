package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.model.internal.zzi;

public final class TileOverlayOptions extends zza {
    public static final Creator<TileOverlayOptions> CREATOR = new zzr();
    private zzi zzbpV;
    private TileProvider zzbpW;
    private boolean zzbpX = true;
    private float zzbph;
    private boolean zzbpi = true;
    private float zzbpq = 0.0f;

    TileOverlayOptions(IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.zzbpV = zzi.zza.zzer(iBinder);
        this.zzbpW = this.zzbpV == null ? null : new TileProvider(this) {
            private final zzi zzbpY = this.zzbpZ.zzbpV;
            final /* synthetic */ TileOverlayOptions zzbpZ;

            {
                this.zzbpZ = r2;
            }

            public Tile getTile(int i, int i2, int i3) {
                try {
                    return this.zzbpY.getTile(i, i2, i3);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.zzbpi = z;
        this.zzbph = f;
        this.zzbpX = z2;
        this.zzbpq = f2;
    }

    public TileOverlayOptions fadeIn(boolean z) {
        this.zzbpX = z;
        return this;
    }

    public boolean getFadeIn() {
        return this.zzbpX;
    }

    public TileProvider getTileProvider() {
        return this.zzbpW;
    }

    public float getTransparency() {
        return this.zzbpq;
    }

    public float getZIndex() {
        return this.zzbph;
    }

    public boolean isVisible() {
        return this.zzbpi;
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.zzbpW = tileProvider;
        this.zzbpV = this.zzbpW == null ? null : new zzi.zza(this) {
            public Tile getTile(int i, int i2, int i3) {
                return tileProvider.getTile(i, i2, i3);
            }
        };
        return this;
    }

    public TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbpq = f;
        return this;
    }

    public TileOverlayOptions visible(boolean z) {
        this.zzbpi = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzr.zza(this, parcel, i);
    }

    public TileOverlayOptions zIndex(float f) {
        this.zzbph = f;
        return this;
    }

    IBinder zzJQ() {
        return this.zzbpV.asBinder();
    }
}
