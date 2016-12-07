package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.maps.internal.zza;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions extends AbstractSafeParcelable implements ReflectedParcelable {
    public static final zza CREATOR = new zza();
    private Float alA;
    private LatLngBounds alB;
    private Boolean alm;
    private Boolean aln;
    private int alo;
    private CameraPosition alp;
    private Boolean alq;
    private Boolean alr;
    private Boolean als;
    private Boolean alt;
    private Boolean alu;
    private Boolean alv;
    private Boolean alw;
    private Boolean alx;
    private Boolean aly;
    private Float alz;
    private final int mVersionCode;

    public GoogleMapOptions() {
        this.alo = -1;
        this.alz = null;
        this.alA = null;
        this.alB = null;
        this.mVersionCode = 1;
    }

    GoogleMapOptions(int i, byte b, byte b2, int i2, CameraPosition cameraPosition, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8, byte b9, byte b10, byte b11, Float f, Float f2, LatLngBounds latLngBounds) {
        this.alo = -1;
        this.alz = null;
        this.alA = null;
        this.alB = null;
        this.mVersionCode = i;
        this.alm = zza.zza(b);
        this.aln = zza.zza(b2);
        this.alo = i2;
        this.alp = cameraPosition;
        this.alq = zza.zza(b3);
        this.alr = zza.zza(b4);
        this.als = zza.zza(b5);
        this.alt = zza.zza(b6);
        this.alu = zza.zza(b7);
        this.alv = zza.zza(b8);
        this.alw = zza.zza(b9);
        this.alx = zza.zza(b10);
        this.aly = zza.zza(b11);
        this.alz = f;
        this.alA = f2;
        this.alB = latLngBounds;
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
        this.aly = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions camera(CameraPosition cameraPosition) {
        this.alp = cameraPosition;
        return this;
    }

    public GoogleMapOptions compassEnabled(boolean z) {
        this.alr = Boolean.valueOf(z);
        return this;
    }

    public Boolean getAmbientEnabled() {
        return this.aly;
    }

    public CameraPosition getCamera() {
        return this.alp;
    }

    public Boolean getCompassEnabled() {
        return this.alr;
    }

    public LatLngBounds getLatLngBoundsForCameraTarget() {
        return this.alB;
    }

    public Boolean getLiteMode() {
        return this.alw;
    }

    public Boolean getMapToolbarEnabled() {
        return this.alx;
    }

    public int getMapType() {
        return this.alo;
    }

    public Float getMaxZoomPreference() {
        return this.alA;
    }

    public Float getMinZoomPreference() {
        return this.alz;
    }

    public Boolean getRotateGesturesEnabled() {
        return this.alv;
    }

    public Boolean getScrollGesturesEnabled() {
        return this.als;
    }

    public Boolean getTiltGesturesEnabled() {
        return this.alu;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.aln;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public Boolean getZOrderOnTop() {
        return this.alm;
    }

    public Boolean getZoomControlsEnabled() {
        return this.alq;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.alt;
    }

    public GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        this.alB = latLngBounds;
        return this;
    }

    public GoogleMapOptions liteMode(boolean z) {
        this.alw = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions mapToolbarEnabled(boolean z) {
        this.alx = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions mapType(int i) {
        this.alo = i;
        return this;
    }

    public GoogleMapOptions maxZoomPreference(float f) {
        this.alA = Float.valueOf(f);
        return this;
    }

    public GoogleMapOptions minZoomPreference(float f) {
        this.alz = Float.valueOf(f);
        return this;
    }

    public GoogleMapOptions rotateGesturesEnabled(boolean z) {
        this.alv = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions scrollGesturesEnabled(boolean z) {
        this.als = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions tiltGesturesEnabled(boolean z) {
        this.alu = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions useViewLifecycleInFragment(boolean z) {
        this.aln = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public GoogleMapOptions zOrderOnTop(boolean z) {
        this.alm = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions zoomControlsEnabled(boolean z) {
        this.alq = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions zoomGesturesEnabled(boolean z) {
        this.alt = Boolean.valueOf(z);
        return this;
    }

    byte zzbrj() {
        return zza.zze(this.alm);
    }

    byte zzbrk() {
        return zza.zze(this.aln);
    }

    byte zzbrl() {
        return zza.zze(this.alq);
    }

    byte zzbrm() {
        return zza.zze(this.alr);
    }

    byte zzbrn() {
        return zza.zze(this.als);
    }

    byte zzbro() {
        return zza.zze(this.alt);
    }

    byte zzbrp() {
        return zza.zze(this.alu);
    }

    byte zzbrq() {
        return zza.zze(this.alv);
    }

    byte zzbrr() {
        return zza.zze(this.alw);
    }

    byte zzbrs() {
        return zza.zze(this.alx);
    }

    byte zzbrt() {
        return zza.zze(this.aly);
    }
}
