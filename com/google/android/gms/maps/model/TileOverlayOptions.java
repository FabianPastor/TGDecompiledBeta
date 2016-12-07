package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.model.internal.zzi;
import com.google.android.gms.maps.model.internal.zzi.zza;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class TileOverlayOptions extends AbstractSafeParcelable {
    public static final zzp CREATOR = new zzp();
    private float amD;
    private boolean amE;
    private float amL;
    private zzi ank;
    private TileProvider anl;
    private boolean anm;
    private final int mVersionCode;

    public TileOverlayOptions() {
        this.amE = true;
        this.anm = true;
        this.amL = 0.0f;
        this.mVersionCode = 1;
    }

    TileOverlayOptions(int i, IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.amE = true;
        this.anm = true;
        this.amL = 0.0f;
        this.mVersionCode = i;
        this.ank = zza.zzjk(iBinder);
        this.anl = this.ank == null ? null : new TileProvider(this) {
            private final zzi ann = this.ano.ank;
            final /* synthetic */ TileOverlayOptions ano;

            {
                this.ano = r2;
            }

            public Tile getTile(int i, int i2, int i3) {
                try {
                    return this.ann.getTile(i, i2, i3);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.amE = z;
        this.amD = f;
        this.anm = z2;
        this.amL = f2;
    }

    public TileOverlayOptions fadeIn(boolean z) {
        this.anm = z;
        return this;
    }

    public boolean getFadeIn() {
        return this.anm;
    }

    public TileProvider getTileProvider() {
        return this.anl;
    }

    public float getTransparency() {
        return this.amL;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.amD;
    }

    public boolean isVisible() {
        return this.amE;
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.anl = tileProvider;
        this.ank = this.anl == null ? null : new zza(this) {
            final /* synthetic */ TileOverlayOptions ano;

            public Tile getTile(int i, int i2, int i3) {
                return tileProvider.getTile(i, i2, i3);
            }
        };
        return this;
    }

    public TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.amL = f;
        return this;
    }

    public TileOverlayOptions visible(boolean z) {
        this.amE = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzp.zza(this, parcel, i);
    }

    public TileOverlayOptions zIndex(float f) {
        this.amD = f;
        return this;
    }

    IBinder zzbsl() {
        return this.ank.asBinder();
    }
}
