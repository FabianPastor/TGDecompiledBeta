package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.maps.model.internal.zzi;
import com.google.android.gms.maps.model.internal.zzi.zza;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class TileOverlayOptions extends AbstractSafeParcelable {
    public static final Creator<TileOverlayOptions> CREATOR = new zzp();
    private float apJ;
    private boolean apK;
    private float apR;
    private zzi aqq;
    private TileProvider aqr;
    private boolean aqs;
    private final int mVersionCode;

    public TileOverlayOptions() {
        this.apK = true;
        this.aqs = true;
        this.apR = 0.0f;
        this.mVersionCode = 1;
    }

    TileOverlayOptions(int i, IBinder iBinder, boolean z, float f, boolean z2, float f2) {
        this.apK = true;
        this.aqs = true;
        this.apR = 0.0f;
        this.mVersionCode = i;
        this.aqq = zza.zzjo(iBinder);
        this.aqr = this.aqq == null ? null : new TileProvider(this) {
            private final zzi aqt = this.aqu.aqq;
            final /* synthetic */ TileOverlayOptions aqu;

            {
                this.aqu = r2;
            }

            public Tile getTile(int i, int i2, int i3) {
                try {
                    return this.aqt.getTile(i, i2, i3);
                } catch (RemoteException e) {
                    return null;
                }
            }
        };
        this.apK = z;
        this.apJ = f;
        this.aqs = z2;
        this.apR = f2;
    }

    public TileOverlayOptions fadeIn(boolean z) {
        this.aqs = z;
        return this;
    }

    public boolean getFadeIn() {
        return this.aqs;
    }

    public TileProvider getTileProvider() {
        return this.aqr;
    }

    public float getTransparency() {
        return this.apR;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.apJ;
    }

    public boolean isVisible() {
        return this.apK;
    }

    public TileOverlayOptions tileProvider(final TileProvider tileProvider) {
        this.aqr = tileProvider;
        this.aqq = this.aqr == null ? null : new zza(this) {
            final /* synthetic */ TileOverlayOptions aqu;

            public Tile getTile(int i, int i2, int i3) {
                return tileProvider.getTile(i, i2, i3);
            }
        };
        return this;
    }

    public TileOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        zzaa.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.apR = f;
        return this;
    }

    public TileOverlayOptions visible(boolean z) {
        this.apK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzp.zza(this, parcel, i);
    }

    public TileOverlayOptions zIndex(float f) {
        this.apJ = f;
        return this;
    }

    IBinder zzbtb() {
        return this.aqq.asBinder();
    }
}
