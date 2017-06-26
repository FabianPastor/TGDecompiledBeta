package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolygonOptions extends zza {
    public static final Creator<PolygonOptions> CREATOR = new zzk();
    private int mFillColor;
    private int mStrokeColor;
    private float mStrokeWidth;
    private final List<LatLng> zzbnN;
    private final List<List<LatLng>> zzbnO;
    private boolean zzbnP;
    private int zzbnQ;
    private float zzbnk;
    private boolean zzbnl;
    private boolean zzbnm;
    @Nullable
    private List<PatternItem> zzbnn;

    public PolygonOptions() {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzbnk = 0.0f;
        this.zzbnl = true;
        this.zzbnP = false;
        this.zzbnm = false;
        this.zzbnQ = 0;
        this.zzbnn = null;
        this.zzbnN = new ArrayList();
        this.zzbnO = new ArrayList();
    }

    PolygonOptions(List<LatLng> list, List list2, float f, int i, int i2, float f2, boolean z, boolean z2, boolean z3, int i3, @Nullable List<PatternItem> list3) {
        this.mStrokeWidth = 10.0f;
        this.mStrokeColor = -16777216;
        this.mFillColor = 0;
        this.zzbnk = 0.0f;
        this.zzbnl = true;
        this.zzbnP = false;
        this.zzbnm = false;
        this.zzbnQ = 0;
        this.zzbnn = null;
        this.zzbnN = list;
        this.zzbnO = list2;
        this.mStrokeWidth = f;
        this.mStrokeColor = i;
        this.mFillColor = i2;
        this.zzbnk = f2;
        this.zzbnl = z;
        this.zzbnP = z2;
        this.zzbnm = z3;
        this.zzbnQ = i3;
        this.zzbnn = list3;
    }

    public final PolygonOptions add(LatLng latLng) {
        this.zzbnN.add(latLng);
        return this;
    }

    public final PolygonOptions add(LatLng... latLngArr) {
        this.zzbnN.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public final PolygonOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbnN.add(add);
        }
        return this;
    }

    public final PolygonOptions addHole(Iterable<LatLng> iterable) {
        ArrayList arrayList = new ArrayList();
        for (LatLng add : iterable) {
            arrayList.add(add);
        }
        this.zzbnO.add(arrayList);
        return this;
    }

    public final PolygonOptions clickable(boolean z) {
        this.zzbnm = z;
        return this;
    }

    public final PolygonOptions fillColor(int i) {
        this.mFillColor = i;
        return this;
    }

    public final PolygonOptions geodesic(boolean z) {
        this.zzbnP = z;
        return this;
    }

    public final int getFillColor() {
        return this.mFillColor;
    }

    public final List<List<LatLng>> getHoles() {
        return this.zzbnO;
    }

    public final List<LatLng> getPoints() {
        return this.zzbnN;
    }

    public final int getStrokeColor() {
        return this.mStrokeColor;
    }

    public final int getStrokeJointType() {
        return this.zzbnQ;
    }

    @Nullable
    public final List<PatternItem> getStrokePattern() {
        return this.zzbnn;
    }

    public final float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public final float getZIndex() {
        return this.zzbnk;
    }

    public final boolean isClickable() {
        return this.zzbnm;
    }

    public final boolean isGeodesic() {
        return this.zzbnP;
    }

    public final boolean isVisible() {
        return this.zzbnl;
    }

    public final PolygonOptions strokeColor(int i) {
        this.mStrokeColor = i;
        return this;
    }

    public final PolygonOptions strokeJointType(int i) {
        this.zzbnQ = i;
        return this;
    }

    public final PolygonOptions strokePattern(@Nullable List<PatternItem> list) {
        this.zzbnn = list;
        return this;
    }

    public final PolygonOptions strokeWidth(float f) {
        this.mStrokeWidth = f;
        return this;
    }

    public final PolygonOptions visible(boolean z) {
        this.zzbnl = z;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, getPoints(), false);
        zzd.zzd(parcel, 3, this.zzbnO, false);
        zzd.zza(parcel, 4, getStrokeWidth());
        zzd.zzc(parcel, 5, getStrokeColor());
        zzd.zzc(parcel, 6, getFillColor());
        zzd.zza(parcel, 7, getZIndex());
        zzd.zza(parcel, 8, isVisible());
        zzd.zza(parcel, 9, isGeodesic());
        zzd.zza(parcel, 10, isClickable());
        zzd.zzc(parcel, 11, getStrokeJointType());
        zzd.zzc(parcel, 12, getStrokePattern(), false);
        zzd.zzI(parcel, zze);
    }

    public final PolygonOptions zIndex(float f) {
        this.zzbnk = f;
        return this;
    }
}
