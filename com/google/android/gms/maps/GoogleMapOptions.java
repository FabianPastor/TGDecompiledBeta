package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.maps.internal.zza;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions extends zzbfm implements ReflectedParcelable {
    public static final Creator<GoogleMapOptions> CREATOR = new zzaa();
    private Boolean zzisb;
    private Boolean zzisc;
    private int zzisd = -1;
    private CameraPosition zzise;
    private Boolean zzisf;
    private Boolean zzisg;
    private Boolean zzish;
    private Boolean zzisi;
    private Boolean zzisj;
    private Boolean zzisk;
    private Boolean zzisl;
    private Boolean zzism;
    private Boolean zzisn;
    private Float zziso = null;
    private Float zzisp = null;
    private LatLngBounds zzisq = null;

    GoogleMapOptions(byte b, byte b2, int i, CameraPosition cameraPosition, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8, byte b9, byte b10, byte b11, Float f, Float f2, LatLngBounds latLngBounds) {
        this.zzisb = zza.zza(b);
        this.zzisc = zza.zza(b2);
        this.zzisd = i;
        this.zzise = cameraPosition;
        this.zzisf = zza.zza(b3);
        this.zzisg = zza.zza(b4);
        this.zzish = zza.zza(b5);
        this.zzisi = zza.zza(b6);
        this.zzisj = zza.zza(b7);
        this.zzisk = zza.zza(b8);
        this.zzisl = zza.zza(b9);
        this.zzism = zza.zza(b10);
        this.zzisn = zza.zza(b11);
        this.zziso = f;
        this.zzisp = f2;
        this.zzisq = latLngBounds;
    }

    public static GoogleMapOptions createFromAttributes(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) {
            return null;
        }
        TypedArray obtainAttributes = context.getResources().obtainAttributes(attributeSet, R.styleable.MapAttrs);
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_mapType)) {
            googleMapOptions.mapType(obtainAttributes.getInt(R.styleable.MapAttrs_mapType, -1));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_zOrderOnTop)) {
            googleMapOptions.zOrderOnTop(obtainAttributes.getBoolean(R.styleable.MapAttrs_zOrderOnTop, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_useViewLifecycle)) {
            googleMapOptions.useViewLifecycleInFragment(obtainAttributes.getBoolean(R.styleable.MapAttrs_useViewLifecycle, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiCompass)) {
            googleMapOptions.compassEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiCompass, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiRotateGestures)) {
            googleMapOptions.rotateGesturesEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiRotateGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiScrollGestures)) {
            googleMapOptions.scrollGesturesEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiScrollGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiTiltGestures)) {
            googleMapOptions.tiltGesturesEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiTiltGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiZoomGestures)) {
            googleMapOptions.zoomGesturesEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiZoomGestures, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiZoomControls)) {
            googleMapOptions.zoomControlsEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiZoomControls, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_liteMode)) {
            googleMapOptions.liteMode(obtainAttributes.getBoolean(R.styleable.MapAttrs_liteMode, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_uiMapToolbar)) {
            googleMapOptions.mapToolbarEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_uiMapToolbar, true));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_ambientEnabled)) {
            googleMapOptions.ambientEnabled(obtainAttributes.getBoolean(R.styleable.MapAttrs_ambientEnabled, false));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_cameraMinZoomPreference)) {
            googleMapOptions.minZoomPreference(obtainAttributes.getFloat(R.styleable.MapAttrs_cameraMinZoomPreference, Float.NEGATIVE_INFINITY));
        }
        if (obtainAttributes.hasValue(R.styleable.MapAttrs_cameraMinZoomPreference)) {
            googleMapOptions.maxZoomPreference(obtainAttributes.getFloat(R.styleable.MapAttrs_cameraMaxZoomPreference, Float.POSITIVE_INFINITY));
        }
        googleMapOptions.latLngBoundsForCameraTarget(LatLngBounds.createFromAttributes(context, attributeSet));
        googleMapOptions.camera(CameraPosition.createFromAttributes(context, attributeSet));
        obtainAttributes.recycle();
        return googleMapOptions;
    }

    public final GoogleMapOptions ambientEnabled(boolean z) {
        this.zzisn = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions camera(CameraPosition cameraPosition) {
        this.zzise = cameraPosition;
        return this;
    }

    public final GoogleMapOptions compassEnabled(boolean z) {
        this.zzisg = Boolean.valueOf(z);
        return this;
    }

    public final CameraPosition getCamera() {
        return this.zzise;
    }

    public final LatLngBounds getLatLngBoundsForCameraTarget() {
        return this.zzisq;
    }

    public final int getMapType() {
        return this.zzisd;
    }

    public final Float getMaxZoomPreference() {
        return this.zzisp;
    }

    public final Float getMinZoomPreference() {
        return this.zziso;
    }

    public final GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        this.zzisq = latLngBounds;
        return this;
    }

    public final GoogleMapOptions liteMode(boolean z) {
        this.zzisl = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions mapToolbarEnabled(boolean z) {
        this.zzism = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions mapType(int i) {
        this.zzisd = i;
        return this;
    }

    public final GoogleMapOptions maxZoomPreference(float f) {
        this.zzisp = Float.valueOf(f);
        return this;
    }

    public final GoogleMapOptions minZoomPreference(float f) {
        this.zziso = Float.valueOf(f);
        return this;
    }

    public final GoogleMapOptions rotateGesturesEnabled(boolean z) {
        this.zzisk = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions scrollGesturesEnabled(boolean z) {
        this.zzish = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions tiltGesturesEnabled(boolean z) {
        this.zzisj = Boolean.valueOf(z);
        return this;
    }

    public final String toString() {
        return zzbg.zzx(this).zzg("MapType", Integer.valueOf(this.zzisd)).zzg("LiteMode", this.zzisl).zzg("Camera", this.zzise).zzg("CompassEnabled", this.zzisg).zzg("ZoomControlsEnabled", this.zzisf).zzg("ScrollGesturesEnabled", this.zzish).zzg("ZoomGesturesEnabled", this.zzisi).zzg("TiltGesturesEnabled", this.zzisj).zzg("RotateGesturesEnabled", this.zzisk).zzg("MapToolbarEnabled", this.zzism).zzg("AmbientEnabled", this.zzisn).zzg("MinZoomPreference", this.zziso).zzg("MaxZoomPreference", this.zzisp).zzg("LatLngBoundsForCameraTarget", this.zzisq).zzg("ZOrderOnTop", this.zzisb).zzg("UseViewLifecycleInFragment", this.zzisc).toString();
    }

    public final GoogleMapOptions useViewLifecycleInFragment(boolean z) {
        this.zzisc = Boolean.valueOf(z);
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, zza.zzb(this.zzisb));
        zzbfp.zza(parcel, 3, zza.zzb(this.zzisc));
        zzbfp.zzc(parcel, 4, getMapType());
        zzbfp.zza(parcel, 5, getCamera(), i, false);
        zzbfp.zza(parcel, 6, zza.zzb(this.zzisf));
        zzbfp.zza(parcel, 7, zza.zzb(this.zzisg));
        zzbfp.zza(parcel, 8, zza.zzb(this.zzish));
        zzbfp.zza(parcel, 9, zza.zzb(this.zzisi));
        zzbfp.zza(parcel, 10, zza.zzb(this.zzisj));
        zzbfp.zza(parcel, 11, zza.zzb(this.zzisk));
        zzbfp.zza(parcel, 12, zza.zzb(this.zzisl));
        zzbfp.zza(parcel, 14, zza.zzb(this.zzism));
        zzbfp.zza(parcel, 15, zza.zzb(this.zzisn));
        zzbfp.zza(parcel, 16, getMinZoomPreference(), false);
        zzbfp.zza(parcel, 17, getMaxZoomPreference(), false);
        zzbfp.zza(parcel, 18, getLatLngBoundsForCameraTarget(), i, false);
        zzbfp.zzai(parcel, zze);
    }

    public final GoogleMapOptions zOrderOnTop(boolean z) {
        this.zzisb = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions zoomControlsEnabled(boolean z) {
        this.zzisf = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions zoomGesturesEnabled(boolean z) {
        this.zzisi = Boolean.valueOf(z);
        return this;
    }
}
