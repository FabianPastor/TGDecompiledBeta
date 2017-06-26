package com.google.android.gms.maps;

import com.google.android.gms.maps.internal.ILocationSourceDelegate.zza;
import com.google.android.gms.maps.internal.zzah;

final class zzl extends zza {
    private /* synthetic */ LocationSource zzblK;

    zzl(GoogleMap googleMap, LocationSource locationSource) {
        this.zzblK = locationSource;
    }

    public final void activate(zzah com_google_android_gms_maps_internal_zzah) {
        this.zzblK.activate(new zzm(this, com_google_android_gms_maps_internal_zzah));
    }

    public final void deactivate() {
        this.zzblK.deactivate();
    }
}
