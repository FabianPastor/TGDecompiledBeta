package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolylineOptions extends zza {
    public static final Creator<PolylineOptions> CREATOR = new zzl();
    private int mColor;
    private final List<LatLng> zzbpP;
    private boolean zzbpR;
    @NonNull
    private Cap zzbpU;
    @NonNull
    private Cap zzbpV;
    private int zzbpW;
    @Nullable
    private List<PatternItem> zzbpX;
    private float zzbpm;
    private boolean zzbpn;
    private boolean zzbpo;
    private float zzbpt;

    public PolylineOptions() {
        this.zzbpt = 10.0f;
        this.mColor = -16777216;
        this.zzbpm = 0.0f;
        this.zzbpn = true;
        this.zzbpR = false;
        this.zzbpo = false;
        this.zzbpU = new ButtCap();
        this.zzbpV = new ButtCap();
        this.zzbpW = 0;
        this.zzbpX = null;
        this.zzbpP = new ArrayList();
    }

    PolylineOptions(List list, float f, int i, float f2, boolean z, boolean z2, boolean z3, @Nullable Cap cap, @Nullable Cap cap2, int i2, @Nullable List<PatternItem> list2) {
        this.zzbpt = 10.0f;
        this.mColor = -16777216;
        this.zzbpm = 0.0f;
        this.zzbpn = true;
        this.zzbpR = false;
        this.zzbpo = false;
        this.zzbpU = new ButtCap();
        this.zzbpV = new ButtCap();
        this.zzbpW = 0;
        this.zzbpX = null;
        this.zzbpP = list;
        this.zzbpt = f;
        this.mColor = i;
        this.zzbpm = f2;
        this.zzbpn = z;
        this.zzbpR = z2;
        this.zzbpo = z3;
        if (cap != null) {
            this.zzbpU = cap;
        }
        if (cap2 != null) {
            this.zzbpV = cap2;
        }
        this.zzbpW = i2;
        this.zzbpX = list2;
    }

    public PolylineOptions add(LatLng latLng) {
        this.zzbpP.add(latLng);
        return this;
    }

    public PolylineOptions add(LatLng... latLngArr) {
        this.zzbpP.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpP.add(add);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.zzbpo = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public PolylineOptions endCap(@NonNull Cap cap) {
        this.zzbpV = (Cap) zzac.zzb((Object) cap, (Object) "endCap must not be null");
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.zzbpR = z;
        return this;
    }

    public int getColor() {
        return this.mColor;
    }

    @NonNull
    public Cap getEndCap() {
        return this.zzbpV;
    }

    public int getJointType() {
        return this.zzbpW;
    }

    @Nullable
    public List<PatternItem> getPattern() {
        return this.zzbpX;
    }

    public List<LatLng> getPoints() {
        return this.zzbpP;
    }

    @NonNull
    public Cap getStartCap() {
        return this.zzbpU;
    }

    public float getWidth() {
        return this.zzbpt;
    }

    public float getZIndex() {
        return this.zzbpm;
    }

    public boolean isClickable() {
        return this.zzbpo;
    }

    public boolean isGeodesic() {
        return this.zzbpR;
    }

    public boolean isVisible() {
        return this.zzbpn;
    }

    public PolylineOptions jointType(int i) {
        this.zzbpW = i;
        return this;
    }

    public PolylineOptions pattern(@Nullable List<PatternItem> list) {
        this.zzbpX = list;
        return this;
    }

    public PolylineOptions startCap(@NonNull Cap cap) {
        this.zzbpU = (Cap) zzac.zzb((Object) cap, (Object) "startCap must not be null");
        return this;
    }

    public PolylineOptions visible(boolean z) {
        this.zzbpn = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.zzbpt = f;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }

    public PolylineOptions zIndex(float f) {
        this.zzbpm = f;
        return this;
    }
}
