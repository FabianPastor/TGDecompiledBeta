package org.telegram.ui;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda3 implements GoogleMap.OnMyLocationChangeListener {
    public final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda3(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final void onMyLocationChange(Location location) {
        this.f$0.m3171lambda$onMapInit$28$orgtelegramuiLocationActivity(location);
    }
}
