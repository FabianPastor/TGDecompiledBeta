package org.telegram.ui;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$mxPTh8cTielbUWro4l5IfzxMVZU implements OnMyLocationChangeListener {
    private final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ -$$Lambda$LocationActivity$mxPTh8cTielbUWro4l5IfzxMVZU(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final void onMyLocationChange(Location location) {
        this.f$0.lambda$onMapInit$17$LocationActivity(location);
    }
}
