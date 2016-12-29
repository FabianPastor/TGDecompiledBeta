package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.model.internal.zzi;

public final class TileOverlayOptions extends zza {
    public static final Creator<TileOverlayOptions> CREATOR = new zzp();
    private final int mVersionCode;
    private float zzboJ;
    private boolean zzboK;
    private float zzboR;
    private zzi zzbpq;
    private TileProvider zzbpr;
    private boolean zzbps;

    public TileOverlayOptions() {
        this.zzboK = true;
        this.zzbps = true;
        this.zzboR = 0.0f;
        this.mVersionCode = 1;
    }

    TileOverlayOptions(int i, IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.zzboK = true;
        this.zzbps = true;
        this.zzboR = 0.0f;
        this.mVersionCode = i;
        this.zzbpq = zzi.zza.zzeq(iBinder);
        this.zzbpr = this.zzbpq == null ? null : new TileProvider(this) {
            private final zzi zzbpt = this.zzbpu.zzbpq;
            final /* synthetic */ TileOverlayOptions zzbpu;

            {
                this.zzbpu = r2;
            }

            public Tile getTile(int i, int i2, int i3) {
                try {
                    return this.zzbpt.getTile(i, i2, i3);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.zzboK = z;
        this.zzboJ = f;
        this.zzbps = z2;
        this.zzboR = f2;
    }

    public TileOverlayOptions fadeIn(boolean z) {
        this.zzbps = z;
        return this;
    }

    public boolean getFadeIn() {
        return this.zzbps;
    }

    public TileProvider getTileProvider() {
        return this.zzbpr;
    }

    public float getTransparency() {
        return this.zzboR;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.zzboJ;
    }

    public boolean isVisible() {
        return this.zzboK;
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.zzbpr = tileProvider;
        this.zzbpq = this.zzbpr == null ? null : new zzi.zza(this) {
            public Tile getTile(int i, int i2, int i3) {
                return tileProvider.getTile(i, i2, i3);
            }
        };
        return this;
    }

    public TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzboR = f;
        return this;
    }

    public TileOverlayOptions visible(boolean z) {
        this.zzboK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzp.zza(this, parcel, i);
    }

    public TileOverlayOptions zIndex(float f) {
        this.zzboJ = f;
        return this;
    }

    IBinder zzIX() {
        return this.zzbpq.asBinder();
    }
}
