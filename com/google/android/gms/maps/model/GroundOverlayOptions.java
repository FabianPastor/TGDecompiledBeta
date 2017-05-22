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
    private LatLngBounds zzbno;
    private float zzbpc;
    private float zzbph;
    private boolean zzbpi = true;
    private boolean zzbpj = false;
    @NonNull
    private BitmapDescriptor zzbpm;
    private LatLng zzbpn;
    private float zzbpo;
    private float zzbpp;
    private float zzbpq = 0.0f;
    private float zzbpr = 0.5f;
    private float zzbps = 0.5f;

    GroundOverlayOptions(IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.zzbpm = new BitmapDescriptor(IObjectWrapper.zza.zzcd(iBinder));
        this.zzbpn = latLng;
        this.zzbpo = f;
        this.zzbpp = f2;
        this.zzbno = latLngBounds;
        this.zzbpc = f3;
        this.zzbph = f4;
        this.zzbpi = z;
        this.zzbpq = f5;
        this.zzbpr = f6;
        this.zzbps = f7;
        this.zzbpj = z2;
    }

    private GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.zzbpn = latLng;
        this.zzbpo = f;
        this.zzbpp = f2;
        return this;
    }

    public GroundOverlayOptions anchor(float f, float f2) {
        this.zzbpr = f;
        this.zzbps = f2;
        return this;
    }

    public GroundOverlayOptions bearing(float f) {
        this.zzbpc = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public GroundOverlayOptions clickable(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public float getAnchorU() {
        return this.zzbpr;
    }

    public float getAnchorV() {
        return this.zzbps;
    }

    public float getBearing() {
        return this.zzbpc;
    }

    public LatLngBounds getBounds() {
        return this.zzbno;
    }

    public float getHeight() {
        return this.zzbpp;
    }

    public BitmapDescriptor getImage() {
        return this.zzbpm;
    }

    public LatLng getLocation() {
        return this.zzbpn;
    }

    public float getTransparency() {
        return this.zzbpq;
    }

    public float getWidth() {
        return this.zzbpo;
    }

    public float getZIndex() {
        return this.zzbph;
    }

    public GroundOverlayOptions image(@NonNull BitmapDescriptor bitmapDescriptor) {
        zzac.zzb((Object) bitmapDescriptor, (Object) "imageDescriptor must not be null");
        this.zzbpm = bitmapDescriptor;
        return this;
    }

    public boolean isClickable() {
        return this.zzbpj;
    }

    public boolean isVisible() {
        return this.zzbpi;
    }

    public GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzac.zza(this.zzbno == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzac.zza(this.zzbno == null, (Object) "Position has already been set using positionFromBounds");
        zzac.zzb(latLng != null, (Object) "Location must be specified");
        zzac.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzac.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.zzbpn == null;
        String valueOf = String.valueOf(this.zzbpn);
        zzac.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.zzbno = latLngBounds;
        return this;
    }

    public GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzac.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbpq = f;
        return this;
    }

    public GroundOverlayOptions visible(boolean z) {
        this.zzbpi = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzd.zza(this, parcel, i);
    }

    public GroundOverlayOptions zIndex(float f) {
        this.zzbph = f;
        return this;
    }

    IBinder zzJK() {
        return this.zzbpm.zzJm().asBinder();
    }
}
