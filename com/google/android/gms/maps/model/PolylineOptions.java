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
    private final List<LatLng> zzbpL;
    private boolean zzbpN;
    @NonNull
    private Cap zzbpQ;
    @NonNull
    private Cap zzbpR;
    private int zzbpS;
    @Nullable
    private List<PatternItem> zzbpT;
    private float zzbpi;
    private boolean zzbpj;
    private boolean zzbpk;
    private float zzbpp;

    public PolylineOptions() {
        this.zzbpp = 10.0f;
        this.mColor = -16777216;
        this.zzbpi = 0.0f;
        this.zzbpj = true;
        this.zzbpN = false;
        this.zzbpk = false;
        this.zzbpQ = new ButtCap();
        this.zzbpR = new ButtCap();
        this.zzbpS = 0;
        this.zzbpT = null;
        this.zzbpL = new ArrayList();
    }

    PolylineOptions(List list, float f, int i, float f2, boolean z, boolean z2, boolean z3, @Nullable Cap cap, @Nullable Cap cap2, int i2, @Nullable List<PatternItem> list2) {
        this.zzbpp = 10.0f;
        this.mColor = -16777216;
        this.zzbpi = 0.0f;
        this.zzbpj = true;
        this.zzbpN = false;
        this.zzbpk = false;
        this.zzbpQ = new ButtCap();
        this.zzbpR = new ButtCap();
        this.zzbpS = 0;
        this.zzbpT = null;
        this.zzbpL = list;
        this.zzbpp = f;
        this.mColor = i;
        this.zzbpi = f2;
        this.zzbpj = z;
        this.zzbpN = z2;
        this.zzbpk = z3;
        if (cap != null) {
            this.zzbpQ = cap;
        }
        if (cap2 != null) {
            this.zzbpR = cap2;
        }
        this.zzbpS = i2;
        this.zzbpT = list2;
    }

    public PolylineOptions add(LatLng latLng) {
        this.zzbpL.add(latLng);
        return this;
    }

    public PolylineOptions add(LatLng... latLngArr) {
        this.zzbpL.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpL.add(add);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.zzbpk = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public PolylineOptions endCap(@NonNull Cap cap) {
        this.zzbpR = (Cap) zzac.zzb((Object) cap, (Object) "endCap must not be null");
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.zzbpN = z;
        return this;
    }

    public int getColor() {
        return this.mColor;
    }

    @NonNull
    public Cap getEndCap() {
        return this.zzbpR;
    }

    public int getJointType() {
        return this.zzbpS;
    }

    @Nullable
    public List<PatternItem> getPattern() {
        return this.zzbpT;
    }

    public List<LatLng> getPoints() {
        return this.zzbpL;
    }

    @NonNull
    public Cap getStartCap() {
        return this.zzbpQ;
    }

    public float getWidth() {
        return this.zzbpp;
    }

    public float getZIndex() {
        return this.zzbpi;
    }

    public boolean isClickable() {
        return this.zzbpk;
    }

    public boolean isGeodesic() {
        return this.zzbpN;
    }

    public boolean isVisible() {
        return this.zzbpj;
    }

    public PolylineOptions jointType(int i) {
        this.zzbpS = i;
        return this;
    }

    public PolylineOptions pattern(@Nullable List<PatternItem> list) {
        this.zzbpT = list;
        return this;
    }

    public PolylineOptions startCap(@NonNull Cap cap) {
        this.zzbpQ = (Cap) zzac.zzb((Object) cap, (Object) "startCap must not be null");
        return this;
    }

    public PolylineOptions visible(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.zzbpp = f;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }

    public PolylineOptions zIndex(float f) {
        this.zzbpi = f;
        return this;
    }
}
