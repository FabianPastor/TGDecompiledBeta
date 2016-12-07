package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.dynamic.zzd.zza;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class MarkerOptions extends AbstractSafeParcelable {
    public static final zzg CREATOR = new zzg();
    private String HP;
    private float amD;
    private boolean amE;
    private float amM;
    private float amN;
    private String amW;
    private BitmapDescriptor amX;
    private boolean amY;
    private boolean amZ;
    private LatLng ame;
    private float ana;
    private float anb;
    private float anc;
    private float mAlpha;
    private final int mVersionCode;

    public MarkerOptions() {
        this.amM = 0.5f;
        this.amN = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.amE = true;
        this.amZ = false;
        this.ana = 0.0f;
        this.anb = 0.5f;
        this.anc = 0.0f;
        this.mAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.mVersionCode = 1;
    }

    MarkerOptions(int i, LatLng latLng, String str, String str2, IBinder iBinder, float f, float f2, boolean z, boolean z2, boolean z3, float f3, float f4, float f5, float f6, float f7) {
        this.amM = 0.5f;
        this.amN = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.amE = true;
        this.amZ = false;
        this.ana = 0.0f;
        this.anb = 0.5f;
        this.anc = 0.0f;
        this.mAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.mVersionCode = i;
        this.ame = latLng;
        this.HP = str;
        this.amW = str2;
        this.amX = iBinder == null ? null : new BitmapDescriptor(zza.zzfe(iBinder));
        this.amM = f;
        this.amN = f2;
        this.amY = z;
        this.amE = z2;
        this.amZ = z3;
        this.ana = f3;
        this.anb = f4;
        this.anc = f5;
        this.mAlpha = f6;
        this.amD = f7;
    }

    public MarkerOptions alpha(float f) {
        this.mAlpha = f;
        return this;
    }

    public MarkerOptions anchor(float f, float f2) {
        this.amM = f;
        this.amN = f2;
        return this;
    }

    public MarkerOptions draggable(boolean z) {
        this.amY = z;
        return this;
    }

    public MarkerOptions flat(boolean z) {
        this.amZ = z;
        return this;
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public float getAnchorU() {
        return this.amM;
    }

    public float getAnchorV() {
        return this.amN;
    }

    public BitmapDescriptor getIcon() {
        return this.amX;
    }

    public float getInfoWindowAnchorU() {
        return this.anb;
    }

    public float getInfoWindowAnchorV() {
        return this.anc;
    }

    public LatLng getPosition() {
        return this.ame;
    }

    public float getRotation() {
        return this.ana;
    }

    public String getSnippet() {
        return this.amW;
    }

    public String getTitle() {
        return this.HP;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getZIndex() {
        return this.amD;
    }

    public MarkerOptions icon(@Nullable BitmapDescriptor bitmapDescriptor) {
        this.amX = bitmapDescriptor;
        return this;
    }

    public MarkerOptions infoWindowAnchor(float f, float f2) {
        this.anb = f;
        this.anc = f2;
        return this;
    }

    public boolean isDraggable() {
        return this.amY;
    }

    public boolean isFlat() {
        return this.amZ;
    }

    public boolean isVisible() {
        return this.amE;
    }

    public MarkerOptions position(@NonNull LatLng latLng) {
        if (latLng == null) {
            throw new IllegalArgumentException("latlng cannot be null - a position is required.");
        }
        this.ame = latLng;
        return this;
    }

    public MarkerOptions rotation(float f) {
        this.ana = f;
        return this;
    }

    public MarkerOptions snippet(@Nullable String str) {
        this.amW = str;
        return this;
    }

    public MarkerOptions title(@Nullable String str) {
        this.HP = str;
        return this;
    }

    public MarkerOptions visible(boolean z) {
        this.amE = z;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzg.zza(this, parcel, i);
    }

    public MarkerOptions zIndex(float f) {
        this.amD = f;
        return this;
    }

    IBinder zzbsj() {
        return this.amX == null ? null : this.amX.zzbrh().asBinder();
    }
}
