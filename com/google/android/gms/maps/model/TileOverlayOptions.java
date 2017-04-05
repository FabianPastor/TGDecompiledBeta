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
    private zzi zzbpW;
    private TileProvider zzbpX;
    private boolean zzbpY = true;
    private float zzbpi;
    private boolean zzbpj = true;
    private float zzbpr = 0.0f;

    TileOverlayOptions(IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.zzbpW = zzi.zza.zzer(iBinder);
        this.zzbpX = this.zzbpW == null ? null : new TileProvider(this) {
            private final zzi zzbpZ = this.zzbqa.zzbpW;
            final /* synthetic */ TileOverlayOptions zzbqa;

            {
                this.zzbqa = r2;
            }

            public Tile getTile(int i, int i2, int i3) {
                try {
                    return this.zzbpZ.getTile(i, i2, i3);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.zzbpj = z;
        this.zzbpi = f;
        this.zzbpY = z2;
        this.zzbpr = f2;
    }

    public TileOverlayOptions fadeIn(boolean z) {
        this.zzbpY = z;
        return this;
    }

    public boolean getFadeIn() {
        return this.zzbpY;
    }

    public TileProvider getTileProvider() {
        return this.zzbpX;
    }

    public float getTransparency() {
        return this.zzbpr;
    }

    public float getZIndex() {
        return this.zzbpi;
    }

    public boolean isVisible() {
        return this.zzbpj;
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.zzbpX = tileProvider;
        this.zzbpW = this.zzbpX == null ? null : new zzi.zza(this) {
            public Tile getTile(int i, int i2, int i3) {
                return tileProvider.getTile(i, i2, i3);
            }
        };
        return this;
    }

    public TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbpr = f;
        return this;
    }

    public TileOverlayOptions visible(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzr.zza(this, parcel, i);
    }

    public TileOverlayOptions zIndex(float f) {
        this.zzbpi = f;
        return this;
    }

    IBinder zzJQ() {
        return this.zzbpW.asBinder();
    }
}
