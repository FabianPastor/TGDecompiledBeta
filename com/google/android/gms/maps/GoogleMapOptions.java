package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions extends zza implements ReflectedParcelable {
    public static final Creator<GoogleMapOptions> CREATOR = new zzz();
    private Boolean zzblZ;
    private Boolean zzbma;
    private int zzbmb = -1;
    private CameraPosition zzbmc;
    private Boolean zzbmd;
    private Boolean zzbme;
    private Boolean zzbmf;
    private Boolean zzbmg;
    private Boolean zzbmh;
    private Boolean zzbmi;
    private Boolean zzbmj;
    private Boolean zzbmk;
    private Boolean zzbml;
    private Float zzbmm = null;
    private Float zzbmn = null;
    private LatLngBounds zzbmo = null;

    GoogleMapOptions(byte b, byte b2, int i, CameraPosition cameraPosition, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8, byte b9, byte b10, byte b11, Float f, Float f2, LatLngBounds latLngBounds) {
        this.zzblZ = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzbma = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzbmb = i;
        this.zzbmc = cameraPosition;
        this.zzbmd = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzbme = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbmf = com.google.android.gms.maps.internal.zza.zza(b5);
        this.zzbmg = com.google.android.gms.maps.internal.zza.zza(b6);
        this.zzbmh = com.google.android.gms.maps.internal.zza.zza(b7);
        this.zzbmi = com.google.android.gms.maps.internal.zza.zza(b8);
        this.zzbmj = com.google.android.gms.maps.internal.zza.zza(b9);
        this.zzbmk = com.google.android.gms.maps.internal.zza.zza(b10);
        this.zzbml = com.google.android.gms.maps.internal.zza.zza(b11);
        this.zzbmm = f;
        this.zzbmn = f2;
        this.zzbmo = latLngBounds;
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
        this.zzbml = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions camera(CameraPosition cameraPosition) {
        this.zzbmc = cameraPosition;
        return this;
    }

    public final GoogleMapOptions compassEnabled(boolean z) {
        this.zzbme = Boolean.valueOf(z);
        return this;
    }

    public final Boolean getAmbientEnabled() {
        return this.zzbml;
    }

    public final CameraPosition getCamera() {
        return this.zzbmc;
    }

    public final Boolean getCompassEnabled() {
        return this.zzbme;
    }

    public final LatLngBounds getLatLngBoundsForCameraTarget() {
        return this.zzbmo;
    }

    public final Boolean getLiteMode() {
        return this.zzbmj;
    }

    public final Boolean getMapToolbarEnabled() {
        return this.zzbmk;
    }

    public final int getMapType() {
        return this.zzbmb;
    }

    public final Float getMaxZoomPreference() {
        return this.zzbmn;
    }

    public final Float getMinZoomPreference() {
        return this.zzbmm;
    }

    public final Boolean getRotateGesturesEnabled() {
        return this.zzbmi;
    }

    public final Boolean getScrollGesturesEnabled() {
        return this.zzbmf;
    }

    public final Boolean getTiltGesturesEnabled() {
        return this.zzbmh;
    }

    public final Boolean getUseViewLifecycleInFragment() {
        return this.zzbma;
    }

    public final Boolean getZOrderOnTop() {
        return this.zzblZ;
    }

    public final Boolean getZoomControlsEnabled() {
        return this.zzbmd;
    }

    public final Boolean getZoomGesturesEnabled() {
        return this.zzbmg;
    }

    public final GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        this.zzbmo = latLngBounds;
        return this;
    }

    public final GoogleMapOptions liteMode(boolean z) {
        this.zzbmj = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions mapToolbarEnabled(boolean z) {
        this.zzbmk = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions mapType(int i) {
        this.zzbmb = i;
        return this;
    }

    public final GoogleMapOptions maxZoomPreference(float f) {
        this.zzbmn = Float.valueOf(f);
        return this;
    }

    public final GoogleMapOptions minZoomPreference(float f) {
        this.zzbmm = Float.valueOf(f);
        return this;
    }

    public final GoogleMapOptions rotateGesturesEnabled(boolean z) {
        this.zzbmi = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions scrollGesturesEnabled(boolean z) {
        this.zzbmf = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions tiltGesturesEnabled(boolean z) {
        this.zzbmh = Boolean.valueOf(z);
        return this;
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("MapType", Integer.valueOf(this.zzbmb)).zzg("LiteMode", this.zzbmj).zzg("Camera", this.zzbmc).zzg("CompassEnabled", this.zzbme).zzg("ZoomControlsEnabled", this.zzbmd).zzg("ScrollGesturesEnabled", this.zzbmf).zzg("ZoomGesturesEnabled", this.zzbmg).zzg("TiltGesturesEnabled", this.zzbmh).zzg("RotateGesturesEnabled", this.zzbmi).zzg("MapToolbarEnabled", this.zzbmk).zzg("AmbientEnabled", this.zzbml).zzg("MinZoomPreference", this.zzbmm).zzg("MaxZoomPreference", this.zzbmn).zzg("LatLngBoundsForCameraTarget", this.zzbmo).zzg("ZOrderOnTop", this.zzblZ).zzg("UseViewLifecycleInFragment", this.zzbma).toString();
    }

    public final GoogleMapOptions useViewLifecycleInFragment(boolean z) {
        this.zzbma = Boolean.valueOf(z);
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zza(parcel, 2, com.google.android.gms.maps.internal.zza.zzb(this.zzblZ));
        zzd.zza(parcel, 3, com.google.android.gms.maps.internal.zza.zzb(this.zzbma));
        zzd.zzc(parcel, 4, getMapType());
        zzd.zza(parcel, 5, getCamera(), i, false);
        zzd.zza(parcel, 6, com.google.android.gms.maps.internal.zza.zzb(this.zzbmd));
        zzd.zza(parcel, 7, com.google.android.gms.maps.internal.zza.zzb(this.zzbme));
        zzd.zza(parcel, 8, com.google.android.gms.maps.internal.zza.zzb(this.zzbmf));
        zzd.zza(parcel, 9, com.google.android.gms.maps.internal.zza.zzb(this.zzbmg));
        zzd.zza(parcel, 10, com.google.android.gms.maps.internal.zza.zzb(this.zzbmh));
        zzd.zza(parcel, 11, com.google.android.gms.maps.internal.zza.zzb(this.zzbmi));
        zzd.zza(parcel, 12, com.google.android.gms.maps.internal.zza.zzb(this.zzbmj));
        zzd.zza(parcel, 14, com.google.android.gms.maps.internal.zza.zzb(this.zzbmk));
        zzd.zza(parcel, 15, com.google.android.gms.maps.internal.zza.zzb(this.zzbml));
        zzd.zza(parcel, 16, getMinZoomPreference(), false);
        zzd.zza(parcel, 17, getMaxZoomPreference(), false);
        zzd.zza(parcel, 18, getLatLngBoundsForCameraTarget(), i, false);
        zzd.zzI(parcel, zze);
    }

    public final GoogleMapOptions zOrderOnTop(boolean z) {
        this.zzblZ = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions zoomControlsEnabled(boolean z) {
        this.zzbmd = Boolean.valueOf(z);
        return this;
    }

    public final GoogleMapOptions zoomGesturesEnabled(boolean z) {
        this.zzbmg = Boolean.valueOf(z);
        return this;
    }
}
