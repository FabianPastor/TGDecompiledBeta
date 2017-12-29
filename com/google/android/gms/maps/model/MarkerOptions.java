package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;

public final class MarkerOptions extends zzbfm {
    public static final Creator<MarkerOptions> CREATOR = new zzh();
    private float mAlpha = 1.0f;
    private float mRotation = 0.0f;
    private String zzemt;
    private LatLng zzitp;
    private float zzium;
    private boolean zziun = true;
    private float zziuw = 0.5f;
    private float zziux = 1.0f;
    private String zzivg;
    private BitmapDescriptor zzivh;
    private boolean zzivi;
    private boolean zzivj = false;
    private float zzivk = 0.5f;
    private float zzivl = 0.0f;

    MarkerOptions(LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.zzitp = latLng;
        this.zzemt = str;
        this.zzivg = str2;
        if (iBinder == null) {
            this.zzivh = null;
        } else {
            this.zzivh = new BitmapDescriptor(zza.zzaq(iBinder));
        }
        this.zziuw = f;
        this.zziux = f2;
        this.zzivi = z;
        this.zziun = z2;
        this.zzivj = z3;
        this.mRotation = f3;
        this.zzivk = f4;
        this.zzivl = f5;
        this.mAlpha = f6;
        this.zzium = f7;
    }

    public final MarkerOptions anchor(float f, float f2) {
        this.zziuw = f;
        this.zziux = f2;
        return this;
    }

    public final float getAlpha() {
        return this.mAlpha;
    }

    public final float getAnchorU() {
        return this.zziuw;
    }

    public final float getAnchorV() {
        return this.zziux;
    }

    public final float getInfoWindowAnchorU() {
        return this.zzivk;
    }

    public final float getInfoWindowAnchorV() {
        return this.zzivl;
    }

    public final LatLng getPosition() {
        return this.zzitp;
    }

    public final float getRotation() {
        return this.mRotation;
    }

    public final String getSnippet() {
        return this.zzivg;
    }

    public final String getTitle() {
        return this.zzemt;
    }

    public final float getZIndex() {
        return this.zzium;
    }

    public final MarkerOptions icon(BitmapDescriptor bitmapDescriptor) {
        this.zzivh = bitmapDescriptor;
        return this;
    }

    public final boolean isDraggable() {
        return this.zzivi;
    }

    public final boolean isFlat() {
        return this.zzivj;
    }

    public final boolean isVisible() {
        return this.zziun;
    }

    public final MarkerOptions position(LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.zzitp = latLng;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getPosition(), i, false);
        zzbfp.zza(parcel, 3, getTitle(), false);
        zzbfp.zza(parcel, 4, getSnippet(), false);
        zzbfp.zza(parcel, 5, this.zzivh == null ? null : this.zzivh.zzavz().asBinder(), false);
        zzbfp.zza(parcel, 6, getAnchorU());
        zzbfp.zza(parcel, 7, getAnchorV());
        zzbfp.zza(parcel, 8, isDraggable());
        zzbfp.zza(parcel, 9, isVisible());
        zzbfp.zza(parcel, 10, isFlat());
        zzbfp.zza(parcel, 11, getRotation());
        zzbfp.zza(parcel, 12, getInfoWindowAnchorU());
        zzbfp.zza(parcel, 13, getInfoWindowAnchorV());
        zzbfp.zza(parcel, 14, getAlpha());
        zzbfp.zza(parcel, 15, getZIndex());
        zzbfp.zzai(parcel, zze);
    }
}
