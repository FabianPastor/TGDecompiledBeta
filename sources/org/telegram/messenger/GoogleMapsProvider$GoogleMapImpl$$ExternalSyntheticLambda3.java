package org.telegram.messenger;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import org.telegram.messenger.GoogleMapsProvider;
import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda3 implements GoogleMap.OnMarkerClickListener {
    public final /* synthetic */ GoogleMapsProvider.GoogleMapImpl f$0;
    public final /* synthetic */ IMapsProvider.OnMarkerClickListener f$1;

    public /* synthetic */ GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda3(GoogleMapsProvider.GoogleMapImpl googleMapImpl, IMapsProvider.OnMarkerClickListener onMarkerClickListener) {
        this.f$0 = googleMapImpl;
        this.f$1 = onMarkerClickListener;
    }

    public final boolean onMarkerClick(Marker marker) {
        return this.f$0.lambda$setOnMarkerClickListener$1(this.f$1, marker);
    }
}
