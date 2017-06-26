package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnCircleClickListener;
import com.google.android.gms.maps.internal.zzw;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.internal.zzd;

final class zzn extends zzw {
    private /* synthetic */ OnCircleClickListener zzblM;

    zzn(GoogleMap googleMap, OnCircleClickListener onCircleClickListener) {
        this.zzblM = onCircleClickListener;
    }

    public final void zza(zzd com_google_android_gms_maps_model_internal_zzd) {
        this.zzblM.onCircleClick(new Circle(com_google_android_gms_maps_model_internal_zzd));
    }
}
