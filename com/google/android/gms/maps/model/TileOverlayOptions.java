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
    private float zzbpm;
    private boolean zzbpn = true;
    private float zzbpv = 0.0f;
    private zzi zzbqa;
    private TileProvider zzbqb;
    private boolean zzbqc = true;

    TileOverlayOptions(IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.zzbqa = zzi.zza.zzer(iBinder);
        this.zzbqb = this.zzbqa == null ? null : new TileProvider(this) {
            private final zzi zzbqd = this.zzbqe.zzbqa;
            final /* synthetic */ TileOverlayOptions zzbqe;

            {
                this.zzbqe = r2;
            }

            public Tile getTile(int i, int i2, int i3) {
                try {
                    return this.zzbqd.getTile(i, i2, i3);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.zzbpn = z;
        this.zzbpm = f;
        this.zzbqc = z2;
        this.zzbpv = f2;
    }

    public TileOverlayOptions fadeIn(boolean z) {
        this.zzbqc = z;
        return this;
    }

    public boolean getFadeIn() {
        return this.zzbqc;
    }

    public TileProvider getTileProvider() {
        return this.zzbqb;
    }

    public float getTransparency() {
        return this.zzbpv;
    }

    public float getZIndex() {
        return this.zzbpm;
    }

    public boolean isVisible() {
        return this.zzbpn;
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.zzbqb = tileProvider;
        this.zzbqa = this.zzbqb == null ? null : new zzi.zza(this) {
            public Tile getTile(int i, int i2, int i3) {
                return tileProvider.getTile(i, i2, i3);
            }
        };
        return this;
    }

    public TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbpv = f;
        return this;
    }

    public TileOverlayOptions visible(boolean z) {
        this.zzbpn = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzr.zza(this, parcel, i);
    }

    public TileOverlayOptions zIndex(float f) {
        this.zzbpm = f;
        return this;
    }

    IBinder zzJP() {
        return this.zzbqa.asBinder();
    }
}
