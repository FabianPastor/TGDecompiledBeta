package org.telegram.ui;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda11 implements GoogleMap.OnMarkerClickListener {
    public final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda11(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final boolean onMarkerClick(Marker marker) {
        return this.f$0.lambda$onMapInit$29(marker);
    }
}
