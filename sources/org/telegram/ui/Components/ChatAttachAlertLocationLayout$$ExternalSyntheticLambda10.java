package org.telegram.ui.Components;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda10 implements GoogleMap.OnMyLocationChangeListener {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda10(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout) {
        this.f$0 = chatAttachAlertLocationLayout;
    }

    public final void onMyLocationChange(Location location) {
        this.f$0.lambda$onMapInit$18(location);
    }
}
