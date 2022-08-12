package org.telegram.ui;

import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda31 implements IMapsProvider.OnMarkerClickListener {
    public final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda31(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final boolean onClick(IMapsProvider.IMarker iMarker) {
        return this.f$0.lambda$onMapInit$33(iMarker);
    }
}
