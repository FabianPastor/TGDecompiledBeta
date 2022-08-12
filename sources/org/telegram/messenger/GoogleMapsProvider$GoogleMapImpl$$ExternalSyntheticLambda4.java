package org.telegram.messenger;

import android.location.Location;
import androidx.core.util.Consumer;
import com.google.android.gms.maps.GoogleMap;

public final /* synthetic */ class GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda4 implements GoogleMap.OnMyLocationChangeListener {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda4(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void onMyLocationChange(Location location) {
        this.f$0.accept(location);
    }
}
