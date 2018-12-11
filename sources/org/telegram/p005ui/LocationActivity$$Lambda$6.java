package org.telegram.p005ui;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$6 */
final /* synthetic */ class LocationActivity$$Lambda$6 implements OnMyLocationChangeListener {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$6(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onMyLocationChange(Location location) {
        this.arg$1.lambda$onMapInit$9$LocationActivity(location);
    }
}
