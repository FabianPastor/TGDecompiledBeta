package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.dynamic.zzd.zza;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class GroundOverlayOptions extends AbstractSafeParcelable {
    public static final Creator<GroundOverlayOptions> CREATOR = new zzc();
    public static final float NO_DIMENSION = -1.0f;
    private LatLngBounds anI;
    private float apF;
    private float apJ;
    private boolean apK;
    private boolean apL;
    private BitmapDescriptor apN;
    private LatLng apO;
    private float apP;
    private float apQ;
    private float apR;
    private float apS;
    private float apT;
    private final int mVersionCode;

    public GroundOverlayOptions() {
        this.apK = true;
        this.apR = 0.0f;
        this.apS = 0.5f;
        this.apT = 0.5f;
        this.apL = false;
        this.mVersionCode = 1;
    }

    GroundOverlayOptions(int i, IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.apK = true;
        this.apR = 0.0f;
        this.apS = 0.5f;
        this.apT = 0.5f;
        this.apL = false;
        this.mVersionCode = i;
        this.apN = new BitmapDescriptor(zza.zzfd(iBinder));
        this.apO = latLng;
        this.apP = f;
        this.apQ = f2;
        this.anI = latLngBounds;
        this.apF = f3;
        this.apJ = f4;
        this.apK = z;
        this.apR = f5;
        this.apS = f6;
        this.apT = f7;
        this.apL = z2;
    }

    private GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.apO = latLng;
        this.apP = f;
        this.apQ = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float f, float f2) {
        this.apS = f;
        this.apT = f2;
        return this;
    }

    public GroundOverlayOptions bearing(float f) {
        this.apF = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public GroundOverlayOptions clickable(boolean z) {
        this.apL = z;
        return this;
    }

    public float getAnchorU() {
        return this.apS;
    }

    public float getAnchorV() {
        return this.apT;
    }

    public float getBearing() {
        return this.apF;
    }

    public LatLngBounds getBounds() {
        return this.anI;
    }

    public float getHeight() {
        return this.apQ;
    }

    public BitmapDescriptor getImage() {
        return this.apN;
    }

    public LatLng getLocation() {
        return this.apO;
    }

    public float getTransparency() {
        return this.apR;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getWidth() {
        return this.apP;
    }

    public float getZIndex() {
        return this.apJ;
    }

    public GroundOverlayOptions image(BitmapDescriptor bitmapDescriptor) {
        this.apN = bitmapDescriptor;
        return this;
    }

    public boolean isClickable() {
        return this.apL;
    }

    public boolean isVisible() {
        return this.apK;
    }

    public GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzaa.zza(this.anI == null, (Object) "Position has already been set using positionFromBounds");
        zzaa.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzaa.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzaa.zza(this.anI == null, (Object) "Position has already been set using positionFromBounds");
        zzaa.zzb(latLng != null, (Object) "Location must be specified");
        zzaa.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzaa.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.apO == null;
        String valueOf = String.valueOf(this.apO);
        zzaa.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.anI = latLngBounds;
        return this;
    }

    public GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        zzaa.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.apR = f;
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.apK = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzc.zza(this, parcel, i);
    }

    public GroundOverlayOptions zIndex(float f) {
        this.apJ = f;
        return this;
    }

    IBinder zzbsx() {
        return this.apN.zzbsc().asBinder();
    }
}
