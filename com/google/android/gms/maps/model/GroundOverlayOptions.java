package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.IObjectWrapper;

public final class GroundOverlayOptions extends zza {
    public static final Creator<GroundOverlayOptions> CREATOR = new zzd();
    public static final float NO_DIMENSION = -1.0f;
    private LatLngBounds zzblq;
    private float zzbnf;
    private float zzbnk;
    private boolean zzbnl = true;
    private boolean zzbnm = false;
    @NonNull
    private BitmapDescriptor zzbnp;
    private LatLng zzbnq;
    private float zzbnr;
    private float zzbns;
    private float zzbnt = 0.0f;
    private float zzbnu = 0.5f;
    private float zzbnv = 0.5f;

    GroundOverlayOptions(IBinder iBinder, LatLng latLng, float f, float f2, LatLngBounds latLngBounds, float f3, float f4, boolean z, float f5, float f6, float f7, boolean z2) {
        this.zzbnp = new BitmapDescriptor(IObjectWrapper.zza.zzM(iBinder));
        this.zzbnq = latLng;
        this.zzbnr = f;
        this.zzbns = f2;
        this.zzblq = latLngBounds;
        this.zzbnf = f3;
        this.zzbnk = f4;
        this.zzbnl = z;
        this.zzbnt = f5;
        this.zzbnu = f6;
        this.zzbnv = f7;
        this.zzbnm = z2;
    }

    private final GroundOverlayOptions zza(LatLng latLng, float f, float f2) {
        this.zzbnq = latLng;
        this.zzbnr = f;
        this.zzbns = f2;
        return this;
    }

    public final GroundOverlayOptions anchor(float f, float f2) {
        this.zzbnu = f;
        this.zzbnv = f2;
        return this;
    }

    public final GroundOverlayOptions bearing(float f) {
        this.zzbnf = ((f % 360.0f) + 360.0f) % 360.0f;
        return this;
    }

    public final GroundOverlayOptions clickable(boolean z) {
        this.zzbnm = z;
        return this;
    }

    public final float getAnchorU() {
        return this.zzbnu;
    }

    public final float getAnchorV() {
        return this.zzbnv;
    }

    public final float getBearing() {
        return this.zzbnf;
    }

    public final LatLngBounds getBounds() {
        return this.zzblq;
    }

    public final float getHeight() {
        return this.zzbns;
    }

    public final BitmapDescriptor getImage() {
        return this.zzbnp;
    }

    public final LatLng getLocation() {
        return this.zzbnq;
    }

    public final float getTransparency() {
        return this.zzbnt;
    }

    public final float getWidth() {
        return this.zzbnr;
    }

    public final float getZIndex() {
        return this.zzbnk;
    }

    public final GroundOverlayOptions image(@NonNull BitmapDescriptor bitmapDescriptor) {
        zzbo.zzb((Object) bitmapDescriptor, (Object) "imageDescriptor must not be null");
        this.zzbnp = bitmapDescriptor;
        return this;
    }

    public final boolean isClickable() {
        return this.zzbnm;
    }

    public final boolean isVisible() {
        return this.zzbnl;
    }

    public final GroundOverlayOptions position(LatLng latLng, float f) {
        boolean z = true;
        zzbo.zza(this.zzblq == null, (Object) "Position has already been set using positionFromBounds");
        zzbo.zzb(latLng != null, (Object) "Location must be specified");
        if (f < 0.0f) {
            z = false;
        }
        zzbo.zzb(z, (Object) "Width must be non-negative");
        return zza(latLng, f, -1.0f);
    }

    public final GroundOverlayOptions position(LatLng latLng, float f, float f2) {
        boolean z = true;
        zzbo.zza(this.zzblq == null, (Object) "Position has already been set using positionFromBounds");
        zzbo.zzb(latLng != null, (Object) "Location must be specified");
        zzbo.zzb(f >= 0.0f, (Object) "Width must be non-negative");
        if (f2 < 0.0f) {
            z = false;
        }
        zzbo.zzb(z, (Object) "Height must be non-negative");
        return zza(latLng, f, f2);
    }

    public final GroundOverlayOptions positionFromBounds(LatLngBounds latLngBounds) {
        boolean z = this.zzbnq == null;
        String valueOf = String.valueOf(this.zzbnq);
        zzbo.zza(z, new StringBuilder(String.valueOf(valueOf).length() + 46).append("Position has already been set using position: ").append(valueOf).toString());
        this.zzblq = latLngBounds;
        return this;
    }

    public final GroundOverlayOptions transparency(float f) {
        boolean z = f >= 0.0f && f <= 1.0f;
        zzbo.zzb(z, (Object) "Transparency must be in the range [0..1]");
        this.zzbnt = f;
        return this;
    }

    public final GroundOverlayOptions visible(boolean z) {
        this.zzbnl = z;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, this.zzbnp.zzwe().asBinder(), false);
        zzd.zza(parcel, 3, getLocation(), i, false);
        zzd.zza(parcel, 4, getWidth());
        zzd.zza(parcel, 5, getHeight());
        zzd.zza(parcel, 6, getBounds(), i, false);
        zzd.zza(parcel, 7, getBearing());
        zzd.zza(parcel, 8, getZIndex());
        zzd.zza(parcel, 9, isVisible());
        zzd.zza(parcel, 10, getTransparency());
        zzd.zza(parcel, 11, getAnchorU());
        zzd.zza(parcel, 12, getAnchorV());
        zzd.zza(parcel, 13, isClickable());
        zzd.zzI(parcel, zze);
    }

    public final GroundOverlayOptions zIndex(float f) {
        this.zzbnk = f;
        return this;
    }
}
