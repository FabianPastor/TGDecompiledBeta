package org.telegram.ui;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$n5ruFpUEwgnDDJzhX4rkCKjDMXw implements OnMyLocationChangeListener {
    private final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ -$$Lambda$LocationActivity$n5ruFpUEwgnDDJzhX4rkCKjDMXw(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final void onMyLocationChange(Location location) {
        this.f$0.lambda$onMapInit$15$LocationActivity(location);
    }
}
