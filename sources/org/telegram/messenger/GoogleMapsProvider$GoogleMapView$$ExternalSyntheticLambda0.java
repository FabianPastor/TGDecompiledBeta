package org.telegram.messenger;

import androidx.core.util.Consumer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import org.telegram.messenger.GoogleMapsProvider;

public final /* synthetic */ class GoogleMapsProvider$GoogleMapView$$ExternalSyntheticLambda0 implements OnMapReadyCallback {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ GoogleMapsProvider$GoogleMapView$$ExternalSyntheticLambda0(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void onMapReady(GoogleMap googleMap) {
        this.f$0.accept(new GoogleMapsProvider.GoogleMapImpl(googleMap));
    }
}
