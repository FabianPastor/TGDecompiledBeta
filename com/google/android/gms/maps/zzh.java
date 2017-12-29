package com.google.android.gms.maps;

import android.location.Location;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.internal.zzay;

final class zzh extends zzay {
    private /* synthetic */ OnMyLocationChangeListener zzirh;

    zzh(GoogleMap googleMap, OnMyLocationChangeListener onMyLocationChangeListener) {
        this.zzirh = onMyLocationChangeListener;
    }

    public final void zzy(IObjectWrapper iObjectWrapper) {
        this.zzirh.onMyLocationChange((Location) zzn.zzx(iObjectWrapper));
    }
}
