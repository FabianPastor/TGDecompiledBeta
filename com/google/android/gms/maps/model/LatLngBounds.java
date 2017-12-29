package com.google.android.gms.maps.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.Arrays;

public final class LatLngBounds extends zzbfm implements ReflectedParcelable {
    public static final Creator<LatLngBounds> CREATOR = new zze();
    public final LatLng northeast;
    public final LatLng southwest;

    public static final class Builder {
        private double zziva = Double.POSITIVE_INFINITY;
        private double zzivb = Double.NEGATIVE_INFINITY;
        private double zzivc = Double.NaN;
        private double zzivd = Double.NaN;

        public final LatLngBounds build() {
            zzbq.zza(!Double.isNaN(this.zzivc), "no included points");
            return new LatLngBounds(new LatLng(this.zziva, this.zzivc), new LatLng(this.zzivb, this.zzivd));
        }

        public final Builder include(LatLng latLng) {
            Object obj = 1;
            this.zziva = Math.min(this.zziva, latLng.latitude);
            this.zzivb = Math.max(this.zzivb, latLng.latitude);
            double d = latLng.longitude;
            if (Double.isNaN(this.zzivc)) {
                this.zzivc = d;
            } else {
                if (this.zzivc <= this.zzivd) {
                    if (this.zzivc > d || d > this.zzivd) {
                        obj = null;
                    }
                } else if (this.zzivc > d && d > this.zzivd) {
                    obj = null;
                }
                if (obj == null) {
                    if (LatLngBounds.zza(this.zzivc, d) < LatLngBounds.zzb(this.zzivd, d)) {
                        this.zzivc = d;
                    }
                }
                return this;
            }
            this.zzivd = d;
            return this;
        }
    }

    public LatLngBounds(LatLng latLng, LatLng latLng2) {
        zzbq.checkNotNull(latLng, "null southwest");
        zzbq.checkNotNull(latLng2, "null northeast");
        zzbq.zzb(latLng2.latitude >= latLng.latitude, "southern latitude exceeds northern latitude (%s > %s)", Double.valueOf(latLng.latitude), Double.valueOf(latLng2.latitude));
        this.southwest = latLng;
        this.northeast = latLng2;
    }

    public static LatLngBounds createFromAttributes(Context context, AttributeSet attributeSet) {
        if (context == null || attributeSet == null) {
            return null;
        }
        TypedArray obtainAttributes = context.getResources().obtainAttributes(attributeSet, R.styleable.MapAttrs);
        Float valueOf = obtainAttributes.hasValue(R.styleable.MapAttrs_latLngBoundsSouthWestLatitude) ? Float.valueOf(obtainAttributes.getFloat(R.styleable.MapAttrs_latLngBoundsSouthWestLatitude, 0.0f)) : null;
        Float valueOf2 = obtainAttributes.hasValue(R.styleable.MapAttrs_latLngBoundsSouthWestLongitude) ? Float.valueOf(obtainAttributes.getFloat(R.styleable.MapAttrs_latLngBoundsSouthWestLongitude, 0.0f)) : null;
        Float valueOf3 = obtainAttributes.hasValue(R.styleable.MapAttrs_latLngBoundsNorthEastLatitude) ? Float.valueOf(obtainAttributes.getFloat(R.styleable.MapAttrs_latLngBoundsNorthEastLatitude, 0.0f)) : null;
        Float valueOf4 = obtainAttributes.hasValue(R.styleable.MapAttrs_latLngBoundsNorthEastLongitude) ? Float.valueOf(obtainAttributes.getFloat(R.styleable.MapAttrs_latLngBoundsNorthEastLongitude, 0.0f)) : null;
        return (valueOf == null || valueOf2 == null || valueOf3 == null || valueOf4 == null) ? null : new LatLngBounds(new LatLng((double) valueOf.floatValue(), (double) valueOf2.floatValue()), new LatLng((double) valueOf3.floatValue(), (double) valueOf4.floatValue()));
    }

    private static double zza(double d, double d2) {
        return ((d - d2) + 360.0d) % 360.0d;
    }

    private static double zzb(double d, double d2) {
        return ((d2 - d) + 360.0d) % 360.0d;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LatLngBounds)) {
            return false;
        }
        LatLngBounds latLngBounds = (LatLngBounds) obj;
        return this.southwest.equals(latLngBounds.southwest) && this.northeast.equals(latLngBounds.northeast);
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.southwest, this.northeast});
    }

    public final String toString() {
        return zzbg.zzx(this).zzg("southwest", this.southwest).zzg("northeast", this.northeast).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.southwest, i, false);
        zzbfp.zza(parcel, 3, this.northeast, i, false);
        zzbfp.zzai(parcel, zze);
    }
}
