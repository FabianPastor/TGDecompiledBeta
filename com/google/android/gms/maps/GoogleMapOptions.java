package com.google.android.gms.maps;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public final class GoogleMapOptions extends zza implements ReflectedParcelable {
    public static final Creator<GoogleMapOptions> CREATOR = new zza();
    private final int mVersionCode;
    private Boolean zzbnA;
    private Boolean zzbnB;
    private int zzbnC;
    private CameraPosition zzbnD;
    private Boolean zzbnE;
    private Boolean zzbnF;
    private Boolean zzbnG;
    private Boolean zzbnH;
    private Boolean zzbnI;
    private Boolean zzbnJ;
    private Boolean zzbnK;
    private Boolean zzbnL;
    private Boolean zzbnM;
    private Float zzbnN;
    private Float zzbnO;
    private LatLngBounds zzbnP;

    public GoogleMapOptions() {
        this.zzbnC = -1;
        this.zzbnN = null;
        this.zzbnO = null;
        this.zzbnP = null;
        this.mVersionCode = 1;
    }

    GoogleMapOptions(int i, byte b, byte b2, int i2, CameraPosition cameraPosition, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8, byte b9, byte b10, byte b11, Float f, Float f2, LatLngBounds latLngBounds) {
        this.zzbnC = -1;
        this.zzbnN = null;
        this.zzbnO = null;
        this.zzbnP = null;
        this.mVersionCode = i;
        this.zzbnA = com.google.android.gms.maps.internal.zza.zza(b);
        this.zzbnB = com.google.android.gms.maps.internal.zza.zza(b2);
        this.zzbnC = i2;
        this.zzbnD = cameraPosition;
        this.zzbnE = com.google.android.gms.maps.internal.zza.zza(b3);
        this.zzbnF = com.google.android.gms.maps.internal.zza.zza(b4);
        this.zzbnG = com.google.android.gms.maps.internal.zza.zza(b5);
        this.zzbnH = com.google.android.gms.maps.internal.zza.zza(b6);
        this.zzbnI = com.google.android.gms.maps.internal.zza.zza(b7);
        this.zzbnJ = com.google.android.gms.maps.internal.zza.zza(b8);
        this.zzbnK = com.google.android.gms.maps.internal.zza.zza(b9);
        this.zzbnL = com.google.android.gms.maps.internal.zza.zza(b10);
        this.zzbnM = com.google.android.gms.maps.internal.zza.zza(b11);
        this.zzbnN = f;
        this.zzbnO = f2;
        this.zzbnP = latLngBounds;
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
        this.zzbnM = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions camera(CameraPosition cameraPosition) {
        this.zzbnD = cameraPosition;
        return this;
    }

    public GoogleMapOptions compassEnabled(boolean z) {
        this.zzbnF = Boolean.valueOf(z);
        return this;
    }

    public Boolean getAmbientEnabled() {
        return this.zzbnM;
    }

    public CameraPosition getCamera() {
        return this.zzbnD;
    }

    public Boolean getCompassEnabled() {
        return this.zzbnF;
    }

    public LatLngBounds getLatLngBoundsForCameraTarget() {
        return this.zzbnP;
    }

    public Boolean getLiteMode() {
        return this.zzbnK;
    }

    public Boolean getMapToolbarEnabled() {
        return this.zzbnL;
    }

    public int getMapType() {
        return this.zzbnC;
    }

    public Float getMaxZoomPreference() {
        return this.zzbnO;
    }

    public Float getMinZoomPreference() {
        return this.zzbnN;
    }

    public Boolean getRotateGesturesEnabled() {
        return this.zzbnJ;
    }

    public Boolean getScrollGesturesEnabled() {
        return this.zzbnG;
    }

    public Boolean getTiltGesturesEnabled() {
        return this.zzbnI;
    }

    public Boolean getUseViewLifecycleInFragment() {
        return this.zzbnB;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public Boolean getZOrderOnTop() {
        return this.zzbnA;
    }

    public Boolean getZoomControlsEnabled() {
        return this.zzbnE;
    }

    public Boolean getZoomGesturesEnabled() {
        return this.zzbnH;
    }

    public GoogleMapOptions latLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        this.zzbnP = latLngBounds;
        return this;
    }

    public GoogleMapOptions liteMode(boolean z) {
        this.zzbnK = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions mapToolbarEnabled(boolean z) {
        this.zzbnL = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions mapType(int i) {
        this.zzbnC = i;
        return this;
    }

    public GoogleMapOptions maxZoomPreference(float f) {
        this.zzbnO = Float.valueOf(f);
        return this;
    }

    public GoogleMapOptions minZoomPreference(float f) {
        this.zzbnN = Float.valueOf(f);
        return this;
    }

    public GoogleMapOptions rotateGesturesEnabled(boolean z) {
        this.zzbnJ = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions scrollGesturesEnabled(boolean z) {
        this.zzbnG = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions tiltGesturesEnabled(boolean z) {
        this.zzbnI = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions useViewLifecycleInFragment(boolean z) {
        this.zzbnB = Boolean.valueOf(z);
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    public GoogleMapOptions zOrderOnTop(boolean z) {
        this.zzbnA = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions zoomControlsEnabled(boolean z) {
        this.zzbnE = Boolean.valueOf(z);
        return this;
    }

    public GoogleMapOptions zoomGesturesEnabled(boolean z) {
        this.zzbnH = Boolean.valueOf(z);
        return this;
    }

    byte zzIA() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnA);
    }

    byte zzIB() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnB);
    }

    byte zzIC() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnE);
    }

    byte zzID() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnF);
    }

    byte zzIE() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnG);
    }

    byte zzIF() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnH);
    }

    byte zzIG() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnI);
    }

    byte zzIH() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnJ);
    }

    byte zzII() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnK);
    }

    byte zzIJ() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnL);
    }

    byte zzIK() {
        return com.google.android.gms.maps.internal.zza.zze(this.zzbnM);
    }
}
