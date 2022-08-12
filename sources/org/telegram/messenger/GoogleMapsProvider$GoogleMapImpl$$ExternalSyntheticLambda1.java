package org.telegram.messenger;

import com.google.android.gms.maps.GoogleMap;
import org.telegram.messenger.GoogleMapsProvider;
import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda1 implements GoogleMap.OnCameraMoveStartedListener {
    public final /* synthetic */ IMapsProvider.OnCameraMoveStartedListener f$0;

    public /* synthetic */ GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda1(IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener) {
        this.f$0 = onCameraMoveStartedListener;
    }

    public final void onCameraMoveStarted(int i) {
        GoogleMapsProvider.GoogleMapImpl.lambda$setOnCameraMoveStartedListener$0(this.f$0, i);
    }
}
