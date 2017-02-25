package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class GroundOverlayOptions extends zza {
    public static final Creator<GroundOverlayOptions> CREATOR = new zzd();
    public static final float NO_DIMENSION = -1.0f;
    private LatLngBounds zzbnt;
    private float zzbph;
    private float zzbpm;
    private boolean zzbpn = true;
    private boolean zzbpo = false;
    @NonNull
    private BitmapDescriptor zzbpr;
    private LatLng zzbps;
    private float zzbpt;
    private float zzbpu;
    private float zzbpv = 0.0f;
    private float zzbpw = 0.5f;
    private float zzbpx = 0.5f;

    GroundOverlayOptions(IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.zzbpr = new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
        this.zzbps = latLng;
        this.zzbpt = f;
        this.zzbpu = f2;
        this.zzbnt = latLngBounds;
        this.zzbph = f3;
        this.zzbpm = f4;
        this.zzbpn = z;
        this.zzbpv = f5;
        this.zzbpw = f6;
        this.zzbpx = f7;
        this.zzbpo = z2;
    }

    private GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.zzbps = latLng;
        this.zzbpt = f;
        this.zzbpu = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float f, float f2) {
        this.zzbpw = f;
        this.zzbpx = f2;
        return this;
    }

    public GroundOverlayOptions bearing(float f) {
        this.zzbph = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public GroundOverlayOptions clickable(boolean z) {
        this.zzbpo = z;
        return this;
    }

    public float getAnchorU() {
        return this.zzbpw;
    }

    public float getAnchorV() {
        return this.zzbpx;
    }

    public float getBearing() {
        return this.zzbph;
    }

    public LatLngBounds getBounds() {
        return this.zzbnt;
    }

    public float getHeight() {
        return this.zzbpu;
    }

    public BitmapDescriptor getImage() {
        return this.zzbpr;
    }

    public LatLng getLocation() {
        return this.zzbps;
    }

    public float getTransparency() {
        return this.zzbpv;
    }

    public float getWidth() {
        return this.zzbpt;
    }

    public float getZIndex() {
        return this.zzbpm;
    }

    public GroundOverlayOptions image(@NonNull BitmapDescriptor bitmapDescriptor) {
        zzac.zzb((Object) bitmapDescriptor, (Object) "imageDescriptor must not be null");
        this.zzbpr = bitmapDescriptor;
        return this;
    }

    public boolean isClickable() {
        return this.zzbpo;
    }

    public boolean isVisible() {
        return this.zzbpn;
    }

    public GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzac.zza(this.zzbnt == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzac.zza(this.zzbnt == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        zzac.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.zzbps == null;
        String valueOf = String.valueOf(this.zzbps);
        zzac.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.zzbnt = latLngBounds;
        return this;
    }

    public GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbpv = f;
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.zzbpn = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }

    public GroundOverlayOptions zIndex(float f) {
        this.zzbpm = f;
        return this;
    }

    IBinder zzJJ() {
        return this.zzbpr.zzJl().asBinder();
    }
}
