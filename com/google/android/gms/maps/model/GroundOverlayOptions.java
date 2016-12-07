package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zzd.zza;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class GroundOverlayOptions extends AbstractSafeParcelable {
    public static final zzc CREATOR = new zzc();
    public static final float NO_DIMENSION = -1.0f;
    private LatLngBounds akB;
    private float amD;
    private boolean amE;
    private boolean amF;
    private BitmapDescriptor amH;
    private LatLng amI;
    private float amJ;
    private float amK;
    private float amL;
    private float amM;
    private float amN;
    private float amz;
    private final int mVersionCode;

    public GroundOverlayOptions() {
        this.amE = true;
        this.amL = 0.0f;
        this.amM = 0.5f;
        this.amN = 0.5f;
        this.amF = false;
        this.mVersionCode = 1;
    }

    GroundOverlayOptions(int i, IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.amE = true;
        this.amL = 0.0f;
        this.amM = 0.5f;
        this.amN = 0.5f;
        this.amF = false;
        this.mVersionCode = i;
        this.amH = new BitmapDescriptor(zza.zzfe(iBinder));
        this.amI = latLng;
        this.amJ = f;
        this.amK = f2;
        this.akB = latLngBounds;
        this.amz = f3;
        this.amD = f4;
        this.amE = z;
        this.amL = f5;
        this.amM = f6;
        this.amN = f7;
        this.amF = z2;
    }

    private GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.amI = latLng;
        this.amJ = f;
        this.amK = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float f, float f2) {
        this.amM = f;
        this.amN = f2;
        return this;
    }

    public GroundOverlayOptions bearing(float f) {
        this.amz = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public GroundOverlayOptions clickable(boolean z) {
        this.amF = z;
        return this;
    }

    public float getAnchorU() {
        return this.amM;
    }

    public float getAnchorV() {
        return this.amN;
    }

    public float getBearing() {
        return this.amz;
    }

    public LatLngBounds getBounds() {
        return this.akB;
    }

    public float getHeight() {
        return this.amK;
    }

    public BitmapDescriptor getImage() {
        return this.amH;
    }

    public LatLng getLocation() {
        return this.amI;
    }

    public float getTransparency() {
        return this.amL;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getWidth() {
        return this.amJ;
    }

    public float getZIndex() {
        return this.amD;
    }

    public GroundOverlayOptions image(BitmapDescriptor bitmapDescriptor) {
        this.amH = bitmapDescriptor;
        return this;
    }

    public boolean isClickable() {
        return this.amF;
    }

    public boolean isVisible() {
        return this.amE;
    }

    public GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzac.zza(this.akB == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzac.zza(this.akB == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        zzac.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.amI == null;
        String valueOf = String.valueOf(this.amI);
        zzac.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.akB = latLngBounds;
        return this;
    }

    public GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.amL = f;
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.amE = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public GroundOverlayOptions zIndex(float f) {
        this.amD = f;
        return this;
    }

    IBinder zzbsh() {
        return this.amH.zzbrh().asBinder();
    }
}
