package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener;
import com.google.android.gms.maps.internal.zzbe;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;

final class zzp extends zzbe {
    private /* synthetic */ OnPolylineClickListener zzblO;

    zzp(GoogleMap googleMap, OnPolylineClickListener onPolylineClickListener) {
        this.zzblO = onPolylineClickListener;
    }

    public final void zza(IPolylineDelegate iPolylineDelegate) {
        this.zzblO.onPolylineClick(new Polyline(iPolylineDelegate));
    }
}
