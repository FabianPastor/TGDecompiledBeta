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
    private LatLngBounds zzbnp;
    private float zzbpd;
    private float zzbpi;
    private boolean zzbpj = true;
    private boolean zzbpk = false;
    @NonNull
    private BitmapDescriptor zzbpn;
    private LatLng zzbpo;
    private float zzbpp;
    private float zzbpq;
    private float zzbpr = 0.0f;
    private float zzbps = 0.5f;
    private float zzbpt = 0.5f;

    GroundOverlayOptions(IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.zzbpn = new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
        this.zzbpo = latLng;
        this.zzbpp = f;
        this.zzbpq = f2;
        this.zzbnp = latLngBounds;
        this.zzbpd = f3;
        this.zzbpi = f4;
        this.zzbpj = z;
        this.zzbpr = f5;
        this.zzbps = f6;
        this.zzbpt = f7;
        this.zzbpk = z2;
    }

    private GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.zzbpo = latLng;
        this.zzbpp = f;
        this.zzbpq = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float f, float f2) {
        this.zzbps = f;
        this.zzbpt = f2;
        return this;
    }

    public GroundOverlayOptions bearing(float f) {
        this.zzbpd = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public GroundOverlayOptions clickable(boolean z) {
        this.zzbpk = z;
        return this;
    }

    public float getAnchorU() {
        return this.zzbps;
    }

    public float getAnchorV() {
        return this.zzbpt;
    }

    public float getBearing() {
        return this.zzbpd;
    }

    public LatLngBounds getBounds() {
        return this.zzbnp;
    }

    public float getHeight() {
        return this.zzbpq;
    }

    public BitmapDescriptor getImage() {
        return this.zzbpn;
    }

    public LatLng getLocation() {
        return this.zzbpo;
    }

    public float getTransparency() {
        return this.zzbpr;
    }

    public float getWidth() {
        return this.zzbpp;
    }

    public float getZIndex() {
        return this.zzbpi;
    }

    public GroundOverlayOptions image(@NonNull BitmapDescriptor bitmapDescriptor) {
        zzac.zzb((Object) bitmapDescriptor, (Object) "imageDescriptor must not be null");
        this.zzbpn = bitmapDescriptor;
        return this;
    }

    public boolean isClickable() {
        return this.zzbpk;
    }

    public boolean isVisible() {
        return this.zzbpj;
    }

    public GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzac.zza(this.zzbnp == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzac.zza(this.zzbnp == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        zzac.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.zzbpo == null;
        String valueOf = String.valueOf(this.zzbpo);
        zzac.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.zzbnp = latLngBounds;
        return this;
    }

    public GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbpr = f;
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }

    public GroundOverlayOptions zIndex(float f) {
        this.zzbpi = f;
        return this;
    }

    IBinder zzJK() {
        return this.zzbpn.zzJm().asBinder();
    }
}
