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
    private final List<LatLng> zzbpK;
    private boolean zzbpM;
    @NonNull
    private Cap zzbpP;
    @NonNull
    private Cap zzbpQ;
    private int zzbpR;
    @Nullable
    private List<PatternItem> zzbpS;
    private float zzbph;
    private boolean zzbpi;
    private boolean zzbpj;
    private float zzbpo;

    public PolylineOptions() {
        this.zzbpo = 10.0f;
        this.mColor = -16777216;
        this.zzbph = 0.0f;
        this.zzbpi = true;
        this.zzbpM = false;
        this.zzbpj = false;
        this.zzbpP = new ButtCap();
        this.zzbpQ = new ButtCap();
        this.zzbpR = 0;
        this.zzbpS = null;
        this.zzbpK = new ArrayList();
    }

    PolylineOptions(List list, float f, int i, float f2, boolean z, boolean z2, boolean z3, @Nullable Cap cap, @Nullable Cap cap2, int i2, @Nullable List<PatternItem> list2) {
        this.zzbpo = 10.0f;
        this.mColor = -16777216;
        this.zzbph = 0.0f;
        this.zzbpi = true;
        this.zzbpM = false;
        this.zzbpj = false;
        this.zzbpP = new ButtCap();
        this.zzbpQ = new ButtCap();
        this.zzbpR = 0;
        this.zzbpS = null;
        this.zzbpK = list;
        this.zzbpo = f;
        this.mColor = i;
        this.zzbph = f2;
        this.zzbpi = z;
        this.zzbpM = z2;
        this.zzbpj = z3;
        if (cap != null) {
            this.zzbpP = cap;
        }
        if (cap2 != null) {
            this.zzbpQ = cap2;
        }
        this.zzbpR = i2;
        this.zzbpS = list2;
    }

    public PolylineOptions add(LatLng latLng) {
        this.zzbpK.add(latLng);
        return this;
    }

    public PolylineOptions add(LatLng... latLngArr) {
        this.zzbpK.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpK.add(add);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.zzbpj = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public PolylineOptions endCap(@NonNull Cap cap) {
        this.zzbpQ = (Cap) zzac.zzb((Object) cap, (Object) "endCap must not be null");
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.zzbpM = z;
        return this;
    }

    public int getColor() {
        return this.mColor;
    }

    @NonNull
    public Cap getEndCap() {
        return this.zzbpQ;
    }

    public int getJointType() {
        return this.zzbpR;
    }

    @Nullable
    public List<PatternItem> getPattern() {
        return this.zzbpS;
    }

    public List<LatLng> getPoints() {
        return this.zzbpK;
    }

    @NonNull
    public Cap getStartCap() {
        return this.zzbpP;
    }

    public float getWidth() {
        return this.zzbpo;
    }

    public float getZIndex() {
        return this.zzbph;
    }

    public boolean isClickable() {
        return this.zzbpj;
    }

    public boolean isGeodesic() {
        return this.zzbpM;
    }

    public boolean isVisible() {
        return this.zzbpi;
    }

    public PolylineOptions jointType(int i) {
        this.zzbpR = i;
        return this;
    }

    public PolylineOptions pattern(@Nullable List<PatternItem> list) {
        this.zzbpS = list;
        return this;
    }

    public PolylineOptions startCap(@NonNull Cap cap) {
        this.zzbpP = (Cap) zzac.zzb((Object) cap, (Object) "startCap must not be null");
        return this;
    }

    public PolylineOptions visible(boolean z) {
        this.zzbpi = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.zzbpo = f;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }

    public PolylineOptions zIndex(float f) {
        this.zzbph = f;
        return this;
    }
}
