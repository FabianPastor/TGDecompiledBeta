package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.maps.internal.zza;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final Creator<GoogleMapOptions> CREATOR = new zza();
    private Boolean aoA;
    private Boolean aoB;
    private Boolean aoC;
    private Boolean aoD;
    private Boolean aoE;
    private Boolean aoF;
    private Float aoG;
    private Float aoH;
    private LatLngBounds aoI;
    private Boolean aot;
    private Boolean aou;
    private int aov;
    private CameraPosition aow;
    private Boolean aox;
    private Boolean aoy;
    private Boolean aoz;
    private final int mVersionCode;

    public GoogleMapOptions() {
        this.aov = -1;
        this.aoG = null;
        this.aoH = null;
        this.aoI = null;
        this.mVersionCode = 1;
    }

    GoogleMapOptions(int i, byte b, byte b2, int i2, CameraPosition cameraPosition, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8, byte b9, byte b10, byte b11, Float f, Float f2, LatLngBounds latLngBounds) {
        this.aov = -1;
        this.aoG = null;
        this.aoH = null;
        this.aoI = null;
        this.mVersionCode = i;
        this.aot = zza.zza(b);
        this.aou = zza.zza(b2);
        this.aov = i2;
        this.aow = cameraPosition;
        this.aox = zza.zza(b3);
        this.aoy = zza.zza(b4);
        this.aoz = zza.zza(b5);
        this.aoA = zza.zza(b6);
        this.aoB = zza.zza(b7);
        this.aoC = zza.zza(b8);
        this.aoD = zza.zza(b9);
        this.aoE = zza.zza(b10);
        this.aoF = zza.zza(b11);
        this.aoG = f;
        this.aoH = f2;
        this.aoI = latLngBounds;
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

    public GoogleMapOptions ambientEnabled(boolean z) {
        this.aoF = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions camera(CameraPosition cameraPosition) {
        this.aow = cameraPosition;
        return this;
    }

    public GoogleMapOptions compassEnabled(boolean z) {
        this.aoy = Boolean.valueOf(z);
        return this;
    }

    public Boolean getAmbientEnabled() {
        return this.aoF;
    }

    public CameraPosition getCamera() {
        return this.aow;
    }

    public Boolean getCompassEnabled() {
        return this.aoy;
    }

    public LatLngBounds getLatLngBoundsForCameraTarget() {
        return this.aoI;
    }

    public Boolean getLiteMode() {
        return this.aoD;
    }

    public Boolean getMapToolbarEnabled() {
        return this.aoE;
    }

    public int getMapType() {
        return this.aov;
    }

    public Float getMaxZoomPreference() {
        return this.aoH;
    }

    public Float getMinZoomPreference() {
        return this.aoG;
    }

    public Boolean getRotateGesturesEnabled() {
        return this.aoC;
    }

    public Boolean getScrollGesturesEnabled() {
        return this.aoz;
    }

    public Boolean getTiltGesturesEnabled() {
        return this.aoB;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.aou;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public Boolean getZOrderOnTop() {
        return this.aot;
    }

    public Boolean getZoomControlsEnabled() {
        return this.aox;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.aoA;
    }

    public GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        this.aoI = latLngBounds;
        return this;
    }

    public GoogleMapOptions liteMode(boolean z) {
        this.aoD = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions mapToolbarEnabled(boolean z) {
        this.aoE = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions mapType(int i) {
        this.aov = i;
        return this;
    }

    public GoogleMapOptions maxZoomPreference(float f) {
        this.aoH = Float.valueOf(f);
        return this;
    }

    public GoogleMapOptions minZoomPreference(float f) {
        this.aoG = Float.valueOf(f);
        return this;
    }

    public GoogleMapOptions rotateGesturesEnabled(boolean z) {
        this.aoC = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions scrollGesturesEnabled(boolean z) {
        this.aoz = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions tiltGesturesEnabled(boolean z) {
        this.aoB = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions useViewLifecycleInFragment(boolean z) {
        this.aou = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public GoogleMapOptions zOrderOnTop(boolean z) {
        this.aot = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions zoomControlsEnabled(boolean z) {
        this.aox = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions zoomGesturesEnabled(boolean z) {
        this.aoA = Boolean.valueOf(z);
        return this;
    }

    byte zzbse() {
        return zza.zze(this.aot);
    }

    byte zzbsf() {
        return zza.zze(this.aou);
    }

    byte zzbsg() {
        return zza.zze(this.aox);
    }

    byte zzbsh() {
        return zza.zze(this.aoy);
    }

    byte zzbsi() {
        return zza.zze(this.aoz);
    }

    byte zzbsj() {
        return zza.zze(this.aoA);
    }

    byte zzbsk() {
        return zza.zze(this.aoB);
    }

    byte zzbsl() {
        return zza.zze(this.aoC);
    }

    byte zzbsm() {
        return zza.zze(this.aoD);
    }

    byte zzbsn() {
        return zza.zze(this.aoE);
    }

    byte zzbso() {
        return zza.zze(this.aoF);
    }
}
