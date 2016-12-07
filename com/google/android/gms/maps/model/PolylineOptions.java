package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolylineOptions extends zza {
    public static final Creator<PolylineOptions> CREATOR = new zzj();
    private int mColor;
    private final int mVersionCode;
    private float zzboJ;
    private boolean zzboK;
    private boolean zzboL;
    private float zzboP;
    private final List<LatLng> zzbpk;
    private boolean zzbpm;

    public PolylineOptions() {
        this.zzboP = 10.0f;
        this.mColor = -16777216;
        this.zzboJ = 0.0f;
        this.zzboK = true;
        this.zzbpm = false;
        this.zzboL = false;
        this.mVersionCode = 1;
        this.zzbpk = new ArrayList();
    }

    PolylineOptions(int i, List list, float f, int i2, float f2, boolean z, boolean z2, boolean z3) {
        this.zzboP = 10.0f;
        this.mColor = -16777216;
        this.zzboJ = 0.0f;
        this.zzboK = true;
        this.zzbpm = false;
        this.zzboL = false;
        this.mVersionCode = i;
        this.zzbpk = list;
        this.zzboP = f;
        this.mColor = i2;
        this.zzboJ = f2;
        this.zzboK = z;
        this.zzbpm = z2;
        this.zzboL = z3;
    }

    public PolylineOptions add(LatLng latLng) {
        this.zzbpk.add(latLng);
        return this;
    }

    public PolylineOptions add(LatLng... latLngArr) {
        this.zzbpk.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbpk.add(add);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.zzboL = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.zzbpm = z;
        return this;
    }

    public int getColor() {
        return this.mColor;
    }

    public List<LatLng> getPoints() {
        return this.zzbpk;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getWidth() {
        return this.zzboP;
    }

    public float getZIndex() {
        return this.zzboJ;
    }

    public boolean isClickable() {
        return this.zzboL;
    }

    public boolean isGeodesic() {
        return this.zzbpm;
    }

    public boolean isVisible() {
        return this.zzboK;
    }

    public PolylineOptions visible(boolean z) {
        this.zzboK = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.zzboP = f;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzj.zza(this, parcel, i);
    }

    public PolylineOptions zIndex(float f) {
        this.zzboJ = f;
        return this;
    }
}
