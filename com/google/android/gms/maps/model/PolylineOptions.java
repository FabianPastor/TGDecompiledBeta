package com.google.android.gms.maps.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PolylineOptions extends zza {
    public static final Creator<PolylineOptions> CREATOR = new zzl();
    private int mColor;
    private final List<LatLng> zzbnN;
    private boolean zzbnP;
    @NonNull
    private Cap zzbnS;
    @NonNull
    private Cap zzbnT;
    private int zzbnU;
    @Nullable
    private List<PatternItem> zzbnV;
    private float zzbnk;
    private boolean zzbnl;
    private boolean zzbnm;
    private float zzbnr;

    public PolylineOptions() {
        this.zzbnr = 10.0f;
        this.mColor = -16777216;
        this.zzbnk = 0.0f;
        this.zzbnl = true;
        this.zzbnP = false;
        this.zzbnm = false;
        this.zzbnS = new ButtCap();
        this.zzbnT = new ButtCap();
        this.zzbnU = 0;
        this.zzbnV = null;
        this.zzbnN = new ArrayList();
    }

    PolylineOptions(List list, float f, int i, float f2, boolean z, boolean z2, boolean z3, @Nullable Cap cap, @Nullable Cap cap2, int i2, @Nullable List<PatternItem> list2) {
        this.zzbnr = 10.0f;
        this.mColor = -16777216;
        this.zzbnk = 0.0f;
        this.zzbnl = true;
        this.zzbnP = false;
        this.zzbnm = false;
        this.zzbnS = new ButtCap();
        this.zzbnT = new ButtCap();
        this.zzbnU = 0;
        this.zzbnV = null;
        this.zzbnN = list;
        this.zzbnr = f;
        this.mColor = i;
        this.zzbnk = f2;
        this.zzbnl = z;
        this.zzbnP = z2;
        this.zzbnm = z3;
        if (cap != null) {
            this.zzbnS = cap;
        }
        if (cap2 != null) {
            this.zzbnT = cap2;
        }
        this.zzbnU = i2;
        this.zzbnV = list2;
    }

    public final PolylineOptions add(LatLng latLng) {
        this.zzbnN.add(latLng);
        return this;
    }

    public final PolylineOptions add(LatLng... latLngArr) {
        this.zzbnN.addAll(Arrays.asList(latLngArr));
        return this;
    }

    public final PolylineOptions addAll(Iterable<LatLng> iterable) {
        for (LatLng add : iterable) {
            this.zzbnN.add(add);
        }
        return this;
    }

    public final PolylineOptions clickable(boolean z) {
        this.zzbnm = z;
        return this;
    }

    public final PolylineOptions color(int i) {
        this.mColor = i;
        return this;
    }

    public final PolylineOptions endCap(@NonNull Cap cap) {
        this.zzbnT = (Cap) zzbo.zzb((Object) cap, (Object) "endCap must not be null");
        return this;
    }

    public final PolylineOptions geodesic(boolean z) {
        this.zzbnP = z;
        return this;
    }

    public final int getColor() {
        return this.mColor;
    }

    @NonNull
    public final Cap getEndCap() {
        return this.zzbnT;
    }

    public final int getJointType() {
        return this.zzbnU;
    }

    @Nullable
    public final List<PatternItem> getPattern() {
        return this.zzbnV;
    }

    public final List<LatLng> getPoints() {
        return this.zzbnN;
    }

    @NonNull
    public final Cap getStartCap() {
        return this.zzbnS;
    }

    public final float getWidth() {
        return this.zzbnr;
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

    public final PolylineOptions jointType(int i) {
        this.zzbnU = i;
        return this;
    }

    public final PolylineOptions pattern(@Nullable List<PatternItem> list) {
        this.zzbnV = list;
        return this;
    }

    public final PolylineOptions startCap(@NonNull Cap cap) {
        this.zzbnS = (Cap) zzbo.zzb((Object) cap, (Object) "startCap must not be null");
        return this;
    }

    public final PolylineOptions visible(boolean z) {
        this.zzbnl = z;
        return this;
    }

    public final PolylineOptions width(float f) {
        this.zzbnr = f;
        return this;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 2, getPoints(), false);
        zzd.zza(parcel, 3, getWidth());
        zzd.zzc(parcel, 4, getColor());
        zzd.zza(parcel, 5, getZIndex());
        zzd.zza(parcel, 6, isVisible());
        zzd.zza(parcel, 7, isGeodesic());
        zzd.zza(parcel, 8, isClickable());
        zzd.zza(parcel, 9, getStartCap(), i, false);
        zzd.zza(parcel, 10, getEndCap(), i, false);
        zzd.zzc(parcel, 11, getJointType());
        zzd.zzc(parcel, 12, getPattern(), false);
        zzd.zzI(parcel, zze);
    }

    public final PolylineOptions zIndex(float f) {
        this.zzbnk = f;
        return this;
    }
}
