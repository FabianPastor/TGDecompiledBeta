package com.google.android.gms.maps.model;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolylineOptions extends AbstractSafeParcelable {
    public static final zzj CREATOR = new zzj();
    private float amD;
    private boolean amE;
    private boolean amF;
    private float amJ;
    private final List<LatLng> ane;
    private boolean ang;
    private int mColor;
    private final int mVersionCode;

    public PolylineOptions() {
        this.amJ = 10.0f;
        this.mColor = -16777216;
        this.amD = 0.0f;
        this.amE = true;
        this.ang = false;
        this.amF = false;
        this.mVersionCode = 1;
        this.ane = new ArrayList();
    }

    PolylineOptions(int i, List list, float f, int i2, float f2, boolean z, boolean z2, boolean z3) {
        this.amJ = 10.0f;
        this.mColor = -16777216;
        this.amD = 0.0f;
        this.amE = true;
        this.ang = false;
        this.amF = false;
        this.mVersionCode = i;
        this.ane = list;
        this.amJ = f;
        this.mColor = i2;
        this.amD = f2;
        this.amE = z;
        this.ang = z2;
        this.amF = z3;
    }

    public PolylineOptions add(LatLng latLng) {
        this.ane.add(latLng);
        return this;
    }

    public PolylineOptions add(LatLng... latLngArr) {
        this.ane.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.ane.add(add);
        }
        return this;
    }

    public PolylineOptions clickable(boolean z) {
        this.amF = z;
        return this;
    }

    public PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public PolylineOptions geodesic(boolean z) {
        this.ang = z;
        return this;
    }

    public int getColor() {
        return this.mColor;
    }

    public List<LatLng> getPoints() {
        return this.ane;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public float getWidth() {
        return this.amJ;
    }

    public float getZIndex() {
        return this.amD;
    }

    public boolean isClickable() {
        return this.amF;
    }

    public boolean isGeodesic() {
        return this.ang;
    }

    public boolean isVisible() {
        return this.amE;
    }

    public PolylineOptions visible(boolean z) {
        this.amE = z;
        return this;
    }

    public PolylineOptions width(float f) {
        this.amJ = f;
        return this;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzj.zza(this, parcel, i);
    }

    public PolylineOptions zIndex(float f) {
        this.amD = f;
        return this;
    }
}
