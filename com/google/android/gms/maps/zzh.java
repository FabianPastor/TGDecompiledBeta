package com.google.android.gms.maps;

import android.location.Location;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.internal.zzay;

final class zzh extends zzay {
    private /* synthetic */ OnMyLocationChangeListener zzblG;

    zzh(GoogleMap googleMap, OnMyLocationChangeListener onMyLocationChangeListener) {
        this.zzblG = onMyLocationChangeListener;
    }

    public final void zzF(IObjectWrapper iObjectWrapper) {
        this.zzblG.onMyLocationChange((Location) zzn.zzE(iObjectWrapper));
    }
}
